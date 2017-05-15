package gist.telecontrol;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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
        OutputStream tmpOut;

        try {
            tmpIn = mSocket.getInputStream();
            tmpOut = mSocket.getOutputStream();
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

        byte [] reply = new byte[100];
        int bytes = 0;

        while(!mFinish){

            try{
                bytes = tmpIn.read(reply);
            }
            catch(IOException ioe){
                //Give information about the error
            }

            byte [] word = Arrays.copyOfRange(reply, 0, bytes);

            //Enviar a la actividad que se ha recibido el mensaje.

            String data = new String(word);

            Intent intent = new Intent("LAN_RECEIVEDMSG");

            String command = data.substring(0, data.indexOf(" "));
            String value = data.substring(data.indexOf(" ") + 1);

            //Implementar switch con cada comando.

            intent.putExtra("name", value);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

    private void sendMessage(){

    }

    public void finish(){

        mFinish = true;

        if(!mSocket.isClosed()){
            try{
                mSocket.close();
            }
            catch(IOException ioe){
                //Give information about the error
                return;
            }
        }
    }

}