/*
package gist.telecontrol;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class LANKeepAliveThread extends Thread{

    private Context mContext;
    private OutputStream mOutputStream;
    private Socket mSocket;

    private boolean mFinish;

    public LANKeepAliveThread(Context context, OutputStream outputStream, Socket socket){
        mContext = context;
        mOutputStream = outputStream;
        mSocket = socket;
    }

    public void run(){

        mFinish = false;

        Intent intent = new Intent("NETWORK_ERROR");

        while(!mFinish){
            try{
                Log.d("Logging", "Writing KeepAlive message");
                mOutputStream.write("BROKER ALIVE".getBytes());
            } catch(IOException e){
                try{
                    if(!mSocket.isClosed()) mSocket.close();
                }
                catch(IOException ioe){
                    //Information about the error
                    intent.putExtra("message", "EXCHANGE_SERVER: Error while closing socket after an error trying to send a KeepAlive");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;
                }
                //Information about the error
                intent.putExtra("message", "EXCHANGE_SERVER: Error while creating socket output/writing KeepAlive message");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            try{
                Thread.sleep(4000);
            } catch(InterruptedException iex){
                continue;
            }

        }
    }

    public void finish() {
        mFinish = true;
    }
}
*/