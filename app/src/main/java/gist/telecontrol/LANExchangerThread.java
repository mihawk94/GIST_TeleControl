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

        Intent intent;

        try {
            tmpIn = mSocket.getInputStream();
        } catch (IOException e) {
            try{
                if(!mSocket.isClosed()) mSocket.close();
            }
            catch(IOException ioe){
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "EXCHANGE_SERVER: Error while closing socket after an error: " + mAddress);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            //Information about the error
            intent = new Intent("NETWORK_ERROR");
            intent.putExtra("message", "EXCHANGE_SERVER: Error while creating socket input: " + mAddress);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }

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
                intent.putExtra("message", "EXCHANGE_SERVER: Error while reading input bytes: " + mAddress);
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
                intent.putExtra("message", "EXCHANGE_SERVER: Disconnection: " + mAddress);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

            //Enviar a la actividad que se ha recibido el mensaje.

            String data = new String(word);

            intent = new Intent("LAN_RECEIVEDMSG");

            //Implementar switch con cada comando.

            intent.putExtra("message", data);
            intent.putExtra("address", mSocket.getInetAddress().getHostAddress());
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

}