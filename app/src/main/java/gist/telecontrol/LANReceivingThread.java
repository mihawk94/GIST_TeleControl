package gist.telecontrol;

import android.app.Service;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LANReceivingThread extends Thread{

    private DatagramSocket mSocket;
    private Service mService;
    private boolean mFinish;
    private int mCode;

    public LANReceivingThread(Service service, DatagramSocket socket, int code){
        mService = service;
        mSocket = socket;
        mCode = code;
    }

    public void run(){

        /* Implement this
        switch(mCode){
            case 0: CLIENT
                break;
            case 1: SERVER
                break;
            default: ERROR_MSG
                break;
        }
        */

        mFinish = false;

        while(!mFinish){

            byte [] reply = new byte[100];
            DatagramPacket replyPacket = new DatagramPacket(reply, reply.length);

            try {
                mSocket.receive(replyPacket);
            } catch (IOException e) {
                mSocket.close();
                //Give information about the error.
                return;
            }

            Intent intent = new Intent("LAN_DEVICEREPLY");

            intent.putExtra("address", new String(replyPacket.getAddress().getHostAddress()));
            intent.putExtra("name", new String(replyPacket.getData()));
            LocalBroadcastManager.getInstance(mService).sendBroadcast(intent);

        }


    }

    public void setFinish(boolean finish){
        mFinish = finish;
    }

}