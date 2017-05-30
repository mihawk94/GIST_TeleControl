package gist.telecontrol;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class LANExchangerThread extends Thread{

    private Context mContext;
    private Socket mSocket;
    private String mMessage;
    private String mAddress;
    private String mData;
    private String mName;
    private OutputStream mOutputStream;
    //private LANKeepAliveThread mLANKeepAliveThread;
    private boolean mFinish;
    private int mCode;
    private int mPort;
    private HashSet<String> mNames;

    //Realizar cambios en EXCHANGE_SERVER para diferenciar Cliente de Control

    public LANExchangerThread(Context context, Socket socket, int port, HashSet<String> names){
        mContext = context;
        mSocket = socket;
        mAddress = mSocket.getInetAddress().getHostAddress();
        mPort = port;
        mNames = names;
        mCode = 0;
    }

    public LANExchangerThread(Context context, Socket socket, int port){
        mContext = context;
        mSocket = socket;
        mAddress = mSocket.getInetAddress().getHostAddress();
        mPort = port;
        mCode = 0;
    }

    public LANExchangerThread(Context context, Socket socket, String message){
        mContext = context;
        mSocket = socket;
        mMessage = message;
        mCode = 1;
    }

    /*
    public LANExchangerThread(Context context, Socket socket, OutputStream outputStream){
        mContext = context;
        mSocket = socket;
        mOutputStream = outputStream;
        mCode = 2;
    }
    */

    public void run(){

        switch(mCode){
            case 0:
                runServer();
                break;
            case 1:
                sendMessage();
                break;
            case 2:
                runClient();
                break;
            default:
                break;
        }

    }

    private void runServer(){

        mFinish = false;

        InputStream tmpIn;
        //OutputStream tmpOut;

        Intent intent;

        try {
            tmpIn = mSocket.getInputStream();
            //tmpOut = mSocket.getOutputStream();
        } catch (IOException e) {
            try{
                if(!mSocket.isClosed()) mSocket.close();
            }
            catch(IOException ioe){
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                if(mPort == 48184){
                    mData = mPort + ":EXCHANGE_SERVER: Error while closing socket after an error: /" + mAddress;
                }
                else{
                    mData = mPort + ":EXCHANGE_SERVER: Error while closing socket after an error: /" + mName;
                }
                intent.putExtra("message", mData);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            //Information about the error
            intent = new Intent("NETWORK_ERROR");
            if(mPort == 48184){
                mData = mPort + ":EXCHANGE_SERVER: Error while creating socket input: /" + mAddress;
            }
            else{
                mData = mPort + ":EXCHANGE_SERVER: Error while creating socket input: /" + mName;
            }
            intent.putExtra("message", mData);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }

        /*
        mLANKeepAliveThread = new LANKeepAliveThread(mContext, tmpOut, mSocket);
        mLANKeepAliveThread.start();

        try{
            mSocket.setSoTimeout(32000);
        }
        catch(SocketException se){
            try{
                if(!mSocket.isClosed()) mSocket.close();
            }
            catch(IOException ioe){
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "EXCHANGE_SERVER: Error while closing socket after an error setting its timeout: " + mAddress);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            //Information about the error
            intent = new Intent("NETWORK_ERROR");
            intent.putExtra("message", "EXCHANGE_SERVER: Error while setting socket timeout: " + mAddress);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;

        }

        */

        byte [] reply = new byte[1024];
        int bytes = 0;
        byte [] word;

        while(!mFinish){

            try{
                bytes = tmpIn.read(reply);
                Log.d("Logging", "" + bytes);
            } /*catch(SocketTimeoutException te){
                Log.d("Logging", "Waiting timeout");
                try{
                    if(!mSocket.isClosed()) mSocket.close();
                } catch(IOException e1){
                    //Information about the error
                    intent = new Intent("NETWORK_ERROR");
                    intent.putExtra("message", "EXCHANGE_SERVER: Error closing socket after timeout");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;
                }
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "EXCHANGE_SERVER: Timeout");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }*/
            catch(IOException ioe){
                finish();
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                if(mPort == 48184){
                    mData = mPort + ":EXCHANGE_SERVER: Error while reading input bytes /" + mAddress;
                }
                else{
                    mData = mPort + ":EXCHANGE_SERVER: Error while reading input bytes: /" + mName;
                }
                intent.putExtra("message", mData);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

            if(bytes != -1){
                word = Arrays.copyOfRange(reply, 0, bytes);
            }
            else{
                finish();
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                if(mPort == 48184){
                    mData = mPort + ":EXCHANGE_SERVER: Disconnection: /" + mAddress;
                }
                else{
                    mData = mPort + ":EXCHANGE_SERVER: Disconnection: /" + mName;
                }
                intent.putExtra("message", mData);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

            //Enviar a la actividad que se ha recibido el mensaje.

            String data = new String(word);

            String command = data.substring(0, data.indexOf(" "));
            String value = data.substring(data.indexOf(" ") + 1);

            if(mPort == 48186){
                if(mNames.contains(value)){
                    Log.d("Logging", "Client: Name already exists");
                    finish();
                    //Information about the error
                    intent = new Intent("NETWORK_ERROR");
                    mData = mPort + ":EXCHANGE_SERVER: Name exists: /" + mName;
                    intent.putExtra("message", mData);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;
                }
                else{
                    Log.d("Logging", "Client: Adding name..");
                    mNames.add(value);
                    mName = value;
                }
            }

            intent = new Intent("LAN_RECEIVEDMSG");

            intent.putExtra("message", mPort + ":" + data);

            //Guardar datos del controlador
            if(command.equals("NAME:")) mData = mPort + ":" + data;

            intent.putExtra("address", mAddress);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

    private void sendMessage(){

        OutputStream tmpOut;

        Intent intent = new Intent("NETWORK_ERROR");

        try{
            tmpOut = mSocket.getOutputStream();
            tmpOut.write(mMessage.getBytes());
        } catch(IOException e){
            try{
                if(!mSocket.isClosed()) mSocket.close();
            }
            catch(IOException ioe){
                //Information about the error
                intent.putExtra("message", "EXCHANGE_CLIENT: Error while closing socket after an error");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            //Information about the error
            intent.putExtra("message", "EXCHANGE_CLIENT: Error while creating socket output/writing output");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }

    }

    private void runClient(){

        /*
        mFinish = false;

        Intent intent = new Intent("NETWORK_ERROR");

        while(!mFinish){
            try{
                Log.d("Logging", "Writing KeepAlive message");
                mOutputStream.write("CONTROLLER ALIVE".getBytes());
            } catch(IOException e){
                try{
                    if(!mSocket.isClosed()) mSocket.close();
                }
                catch(IOException ioe){
                    //Information about the error
                    intent.putExtra("message", "EXCHANGE_CLIENT: Error while closing socket after an error trying to send a KeepAlive");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;
                }
                //Information about the error
                intent.putExtra("message", "EXCHANGE_CLIENT: Error while creating socket output/writing KeepAlive message");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            try{
                Thread.sleep(30000);
            } catch(InterruptedException iex){
                continue;
            }

        }
        */
    }

    public void finish(){

        //if(mLANKeepAliveThread != null) mLANKeepAliveThread.finish();

        mFinish = true;

        Intent intent = new Intent("NETWORK_ERROR");

        if(mSocket != null){
            if(!mSocket.isClosed()){
                try{
                    Log.d("Logging", "Closing socket");
                    mSocket.close();
                }
                catch(IOException ioe){
                    //Information about the error
                    if(mCode == 0){
                        mData = mPort + ":EXCHANGE_SERVER: Error while closing socket at exit";
                    }
                    else{
                        mData = "EXCHANGE_CLIENT: Error while closing socket at exit";
                    }
                    intent.putExtra("message", mData);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;
                }
            }
        }
    }

    public String getData(){
        return mData;
    }

    public String getClientName() {
        return mName;
    }

    public String getAddress(){
        return mAddress;
    }

}