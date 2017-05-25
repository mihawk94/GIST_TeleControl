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
    private HashMap<String, LANDevice> mLANDeviceHashMap;
    private boolean mFinish;
    private int mCode;

    public LANExchangerThread(Context context, Socket socket, HashMap<String, LANDevice> lanDeviceHashMap){
        mContext = context;
        mSocket = socket;
        mLANDeviceHashMap = lanDeviceHashMap;
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

        try {
            tmpIn = mSocket.getInputStream();
        } catch (IOException e) {
            try{
                mSocket.close();
            }
            catch(IOException ioe){
                //Give information about the error
                return;
            }
            //Give information about the error
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
                //Give information about the error
                Log.d("Logging", "Error");
            }

            if(bytes != -1){
                word = Arrays.copyOfRange(reply, 0, bytes);
            }
            else{
                finish();
                //Give information about the error
                return;
            }

            //Enviar a la actividad que se ha recibido el mensaje.

            String data = new String(word);

            Intent intent = new Intent("LAN_RECEIVEDMSG");

            //Implementar switch con cada comando.

            intent.putExtra("message", data);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

    private void sendMessage(){

        OutputStream tmpOut;

        try{
            tmpOut = mSocket.getOutputStream();
            tmpOut.write(mMessage.getBytes());
        } catch(IOException e){
            try{
                mSocket.close();
            }
            catch(IOException ioe){
                //Give information about the error
                return;
            }
            //Give information about the error
            return;
        }

    }

    public void finish(){

        mFinish = true;

        if(!mSocket.isClosed()){
            try{
                Log.d("Logging", "Closing socket");
                mSocket.close();
            }
            catch(IOException ioe){
                //Give information about the error
                return;
            }
        }
    }

}