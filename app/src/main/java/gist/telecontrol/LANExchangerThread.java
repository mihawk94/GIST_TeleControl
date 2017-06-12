package gist.telecontrol;

import android.app.Service;
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
    private String mAction;

    private HashSet<String> mNames;

    //Realizar cambios en EXCHANGE_SERVER para diferenciar Cliente de Control

    public LANExchangerThread(Context context, Socket socket, int port, HashSet<String> names){
        mContext = context;
        mSocket = socket;
        mAddress = mSocket.getInetAddress().getHostAddress();
        mPort = port;
        mNames = names;
        mCode = 0;
        mAction = "EXCHANGE_SERVER";
    }

    public LANExchangerThread(Context context, Socket socket, int port){
        mContext = context;
        mSocket = socket;
        mAddress = mSocket.getInetAddress().getHostAddress();
        mPort = port;
        mCode = 0;
        mAction = "EXCHANGE_SERVER";
    }

    public LANExchangerThread(Context context, Socket socket, String message){
        mContext = context;
        mSocket = socket;
        mMessage = message;
        mCode = 1;
        mAction = "EXCHANGE_CLIENT";
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
                sendMessage(mMessage);
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
                finish();
                //Information about the error
                if(mPort == 48184){
                    mData = mPort + ":EXCHANGE_SERVER: Error while closing socket after an error: /" + mAddress;

                    ((ConnectionService)mContext).addMessage("Device disconnected: " + mAddress);

                    intent = new Intent("UPDATE_LOG");
                    intent.putExtra("message", "Device disconnected: " + mAddress);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
                else{
                    mData = mPort + ":EXCHANGE_SERVER: Error while closing socket after an error: /";

                    ((ConnectionService)mContext).addMessage("Client disconnected: " + mName);

                    intent = new Intent("UPDATE_LOG");
                    intent.putExtra("message", "Client disconnected: " + mName);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", mData);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                if(mPort == 48184) disconnectClient();
                return;
            }
            finish();
            //Information about the error
            if(mPort == 48184){
                mData = mPort + ":EXCHANGE_SERVER: Error while creating socket input: /" + mAddress;

                ((ConnectionService)mContext).addMessage("Device disconnected: " + mAddress);

                intent = new Intent("UPDATE_LOG");
                intent.putExtra("message", "Device disconnected: " + mAddress);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            else{
                mData = mPort + ":EXCHANGE_SERVER: Error while creating socket input: /";

                ((ConnectionService)mContext).addMessage("Client disconnected: " + mName);

                intent = new Intent("UPDATE_LOG");
                intent.putExtra("message", "Client disconnected: " + mName);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            intent = new Intent("NETWORK_ERROR");
            intent.putExtra("message", mData);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            if(mPort == 48184) disconnectClient();
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

        mName = "";

        byte [] reply = new byte[512];
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
                if(mPort == 48184){
                    mData = mPort + ":EXCHANGE_SERVER: Error while reading input bytes /" + mAddress;
                    ((ConnectionService)mContext).addMessage("Device disconnected: " + mAddress);

                    intent = new Intent("UPDATE_LOG");
                    intent.putExtra("message", "Device disconnected: " + mAddress);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
                else{
                    mData = mPort + ":EXCHANGE_SERVER: Error while reading input bytes: /" + mName;

                    mNames.remove(mName);
                    Log.d("Logging", mName + " has been removed");

                    ((ConnectionService)mContext).addMessage("Client disconnected: " + mName);

                    intent = new Intent("UPDATE_LOG");
                    intent.putExtra("message", "Client disconnected: " + mName);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", mData);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                if(mPort == 48184) disconnectClient();
                return;
            }

            if(bytes != -1){
                word = Arrays.copyOfRange(reply, 0, bytes);
            }
            else{
                finish();
                //Information about the error
                if(mPort == 48184){
                    mData = mPort + ":EXCHANGE_SERVER: Disconnection: /" + mAddress;
                    ((ConnectionService)mContext).addMessage("Device disconnected: " + mAddress);

                    intent = new Intent("UPDATE_LOG");
                    intent.putExtra("message", "Device disconnected: " + mAddress);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
                else{
                    mNames.remove(mName);
                    Log.d("Logging", mName + " has been removed");
                    mData = mPort + ":EXCHANGE_SERVER: Disconnection: /" + mName;
                    ((ConnectionService)mContext).addMessage("Client disconnected: " + mName);

                    intent = new Intent("UPDATE_LOG");
                    intent.putExtra("message", "Client disconnected: " + mName);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", mData);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                if(mPort == 48184) disconnectClient();
                return;
            }

            //Enviar a la actividad que se ha recibido el mensaje.
            //Echarle un ojo al borrar cliente, ya que envia un LAN_RECEIVEDMSG (debido a que en la aplicación cliente no se cierra la conexión)

            String data = new String(word);

            String command = data.substring(0, data.indexOf(" "));
            String value = data.substring(data.indexOf(" ") + 1);
            //String value = data.substring(data.indexOf(" ") + 1, data.indexOf("|"));

            Log.d("Logging", "Value: " + value);

            if(mPort == 48186){
                if(command.equals("NAME:")){
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
                        ((ConnectionService)mContext).addMessage("A new client has been connected: " + mName);

                        intent = new Intent("UPDATE_LOG");
                        intent.putExtra("message", "A new client has been connected: " + mName);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    }
                }
            }
            else{
                if(command.equals("NAME:")){
                    ((ConnectionService)mContext).addMessage("A new device has been connected: " + mAddress);

                    intent = new Intent("UPDATE_LOG");
                    intent.putExtra("message", "A new device has been connected: " + mAddress);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            }

            if(command.equals("PRESS:")){

                ((ConnectionService)mContext).addMessage(mAddress + " pressed " + value);

                intent = new Intent("UPDATE_LOG");
                intent.putExtra("message", mAddress + " pressed " + value);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                mMessage = "PRESS_Address:" + mAddress + "/ " + value;

                if(((ConnectionService)mContext).getClientThreads().size() > 0){
                    for (int i = 0; i < ((ConnectionService)mContext).getClientThreads().size(); i++){
                        ((ConnectionService)mContext).getClientThreads()
                                .get(i).sendMessage(mMessage);
                    }
                }

            }
            else if(command.equals("RELEASE:")){

                ((ConnectionService)mContext).addMessage(mAddress + " released " + value);

                intent = new Intent("UPDATE_LOG");
                intent.putExtra("message", mAddress + " released " + value);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                mMessage = "RELEASE_Address:" + mAddress + "/ " + value;

                if(((ConnectionService)mContext).getClientThreads().size() > 0){
                    for (int i = 0; i < ((ConnectionService)mContext).getClientThreads().size(); i++){
                        ((ConnectionService)mContext).getClientThreads()
                                .get(i).sendMessage(mMessage);
                    }
                }

            }
            else if(command.equals("TOUCH_DOWN:")){
                ((ConnectionService)mContext).addMessage(mAddress + " is touching the screen");

                intent = new Intent("UPDATE_LOG");
                intent.putExtra("message", mAddress + " is touching the screen");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                mMessage = "TOUCH_Address:" + mAddress + "/ " + value;

                if(((ConnectionService)mContext).getClientThreads().size() > 0){
                    for (int i = 0; i < ((ConnectionService)mContext).getClientThreads().size(); i++){
                        ((ConnectionService)mContext).getClientThreads()
                                .get(i).sendMessage(mMessage);
                    }
                }
            }
            /*
            else if(command.equals("TOUCH_MOVE:")){

                mMessage = "TOUCH_Address:" + mAddress + "/ " + value;

                if(((ConnectionService)mContext).getClientThreads().size() > 0){
                    for (int i = 0; i < ((ConnectionService)mContext).getClientThreads().size(); i++){
                        ((ConnectionService)mContext).getClientThreads()
                                .get(i).sendMessage(mMessage);
                    }
                }
            }
            */
            else if(command.equals("TOUCH_RELEASE:")){
                ((ConnectionService)mContext).addMessage(mAddress + " released the screen");

                intent = new Intent("UPDATE_LOG");
                intent.putExtra("message", mAddress + " released");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                mMessage = "TOUCHRELEASE_Address:" + mAddress + "/ " + value;

                if(((ConnectionService)mContext).getClientThreads().size() > 0){
                    for (int i = 0; i < ((ConnectionService)mContext).getClientThreads().size(); i++){
                        ((ConnectionService)mContext).getClientThreads()
                                .get(i).sendMessage(mMessage);
                    }
                }
            }

            //Guardar datos del controlador
            if(command.equals("NAME:")){

                mName = value;

                mData = mPort + ":" + data;
                intent = new Intent("LAN_RECEIVEDMSG");

                intent.putExtra("message", mData);

                intent.putExtra("address", mAddress);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                if(mPort == 48184){

                    mMessage = "CONNECTION_Address:" + mAddress + "/NAME: " + value;

                    Log.d("Logging", "New device connection at broker " + ((ConnectionService)mContext).getClientThreads().size());

                    if(((ConnectionService)mContext).getClientThreads().size() > 0){
                        for (int i = 0; i < ((ConnectionService)mContext).getClientThreads().size(); i++){
                            ((ConnectionService)mContext).getClientThreads()
                                    .get(i).sendMessage(mMessage);
                        }
                    }

                }
                else{
                    if(((ConnectionService)mContext).getDeviceThreads().size() > 0){
                        for (int i = 0; i < ((ConnectionService)mContext).getDeviceThreads().size(); i++){
                            mMessage = "CONNECTION_Address:" + ((ConnectionService)mContext).getDeviceThreads().get(i).getAddress()
                                    + "/NAME: " + ((ConnectionService)mContext).getDeviceThreads().get(i).getAddressName();
                            sendMessage(mMessage);
                        }
                    }
                }
            }

        }
    }

    public void sendMessage(String message){

        OutputStream tmpOut;

        Intent intent = new Intent("NETWORK_ERROR");

        try{
            tmpOut = mSocket.getOutputStream();
            Log.d("Logging", "Sending message: " + message);
            tmpOut.write(message.getBytes());
            tmpOut.flush();
        } catch(IOException e){
            try{
                if(!mSocket.isClosed()) mSocket.close();
            }
            catch(IOException ioe){
                //Information about the error
                intent.putExtra("message", mAction + ": Error while closing socket after an error");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            //Information about the error
            intent.putExtra("message", mAction + ": Error while creating socket output/writing output");
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

    private void disconnectClient(){

        Log.d("Logging", "New device disconnection at broker " + ((ConnectionService)mContext).getClientThreads().size());

        mMessage = "DISCONNECTION_Address:" + mAddress + "/";

        if(((ConnectionService)mContext).getClientThreads().size() > 0){
            for (int i = 0; i < ((ConnectionService)mContext).getClientThreads().size(); i++){
                ((ConnectionService)mContext).getClientThreads()
                        .get(i).sendMessage(mMessage);
            }
        }
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

    public String getAddressName() {
        return mName;
    }

    public String getAddress(){
        return mAddress;
    }

}