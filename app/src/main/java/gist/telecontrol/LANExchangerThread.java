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
import java.util.Arrays;
import java.util.HashMap;

public class LANExchangerThread extends Thread{

    private Context mContext;
    private Socket mSocket;
    private String mMessage;
    private String mAddress;
    private String mData;
    private LANKeepAliveThread mLANKeepAliveThread;
    private boolean mFinish;
    private int mCode;

    public LANExchangerThread(Context context, Socket socket){
        mContext = context;
        mSocket = socket;
        mAddress = mSocket.getInetAddress().getHostAddress();
        mCode = 0;
    }

    public LANExchangerThread(Context context, Socket socket, String message){
        mContext = context;
        mSocket = socket;
        mMessage = message;
        mCode = 1;
    }

    public void run(){

        switch(mCode){
            case 0:
                runServer();
                break;
            case 1:
                sendMessage();
                break;
            default:
                break;
        }

    }

    private void runServer(){

        mFinish = false;

        InputStream tmpIn;
        OutputStream tmpOut;

        Intent intent;

        try {
            tmpIn = mSocket.getInputStream();
            tmpOut = mSocket.getOutputStream();
        } catch (IOException e) {
            try{
                if(!mSocket.isClosed()) mSocket.close();
            }
            catch(IOException ioe){
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                mData = "EXCHANGE_SERVER: Error while closing socket after an error: " + mAddress;
                intent.putExtra("message", mData);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            //Information about the error
            intent = new Intent("NETWORK_ERROR");
            mData = "EXCHANGE_SERVER: Error while creating socket input: " + mAddress;
            intent.putExtra("message", mData);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }

        mLANKeepAliveThread = new LANKeepAliveThread(tmpOut);
        mLANKeepAliveThread.start();

        byte [] reply = new byte[1024];
        int bytes = 0;
        byte [] word;

        while(!mFinish){

            try{
                bytes = tmpIn.read(reply);
                Log.d("Logging", "" + bytes);
            }
            catch(IOException ioe){
                finish();
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                mData = "EXCHANGE_SERVER: Error while reading input bytes: " + mAddress;
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
                mData = "EXCHANGE_SERVER: Disconnection: " + mAddress;
                intent.putExtra("message", mData);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

            //Enviar a la actividad que se ha recibido el mensaje.

            String data = new String(word);

            intent = new Intent("LAN_RECEIVEDMSG");

            intent.putExtra("message", data);

            String command = data.substring(0, data.indexOf(" "));

            //Guardar datos del controlador
            if(command.equals("NAME:")) mData = data;

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

    public void finish(){

        mLANKeepAliveThread.finish();

        mFinish = true;

        Intent intent = new Intent("NETWORK_ERROR");

        if(!mSocket.isClosed()){
            try{
                Log.d("Logging", "Closing socket");
                mSocket.close();
            }
            catch(IOException ioe){
                //Information about the error
                intent.putExtra("message", "EXCHANGE: Error while closing socket at exit");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
        }
    }

    public String getData(){
        return mData;
    }

    public String getAddress(){
        return mAddress;
    }

}