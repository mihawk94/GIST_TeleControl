package gist.telecontrol;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class LANCheckStatusThread extends Thread{

    private Context mContext;

    private boolean mFinish;

    public LANCheckStatusThread(Context context){
        mContext = context;
    }

    public void run(){

        mFinish = false;

        Intent intent = new Intent("NETWORK_ERROR");

        while(!mFinish){
            if(LANRequestingThread.getMainInterface() == null){
                intent.putExtra("message", "REPLY: Error getting address");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            try{
                Thread.sleep(5000);
            } catch(InterruptedException iex){
                continue;
            }
        }
    }

    public void finish() {
        mFinish = true;
    }
}