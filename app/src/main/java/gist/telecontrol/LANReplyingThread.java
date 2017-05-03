package gist.telecontrol;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class LANReplyingThread extends Thread{

    private DatagramSocket mSocket;
    private Context mContext;
    private boolean mFinish;
    private String mName;
    private int mCode;

    public LANReplyingThread(Context context, DatagramSocket socket){
        mContext = context;
        mSocket = socket;
        mCode = 0;
    }

    public LANReplyingThread(Context context, String name){
        mContext = context;
        mName = name;
        mCode = 1;
    }

    public void run(){

        switch(mCode){
            case 0:
                runClient();
                break;
            case 1:
                runServer();
                break;
            default:
                //Give information about the error
                return;
        }

    }

    private void runClient(){


        mFinish = false;

        while(!mFinish){

            byte [] reply = new byte[100];
            DatagramPacket replyPacket = new DatagramPacket(reply, reply.length);

            try {
                Log.d("Logging", "Waiting for replies...");
                mSocket.receive(replyPacket);
                Log.d("Logging", "Reply received from one device!");
            } catch (IOException e) {
                mSocket.close();
                Log.d("Logging", "Problem with client receiving socket");
                //Give information about the error.
                return;
            }

            Intent intent = new Intent("LAN_DEVICEREPLY");

            intent.putExtra("address", new String(replyPacket.getAddress().getHostAddress()));
            intent.putExtra("name", new String(replyPacket.getData()).split(" ")[1]);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        }

    }

    private void runServer(){

        try {
            mSocket = new DatagramSocket(48182);
        } catch (SocketException e) {
            Log.d("Logging", "Maybe port is already used");
            //Give information about the error
            return;
        }

        mFinish = false;

        while(!mFinish){

            byte [] reply = new byte[100];
            DatagramPacket replyPacket = new DatagramPacket(reply, reply.length);

            try {
                Log.d("Logging", "Listening to requests..");
                mSocket.receive(replyPacket);
            } catch (IOException e) {
                mSocket.close();
                //Give information about the error.
                continue;
            }

            String requestName = new String(replyPacket.getData());

            replyPacket.setPort(48181);

            //mSocket.connect(replyPacket.getAddress(), 48181);

            replyPacket.setData(new String("REPLY: " + mName).getBytes());

            try {
                Log.d("Logging", "Sending info to " + replyPacket.getAddress().getHostAddress() + ":" + replyPacket.getPort() + ": " + requestName);
                mSocket.send(replyPacket);
            } catch (IOException e) {
                mSocket.close();
                //Give information about the error.
                continue;
            }

        }

    }

    public void finish(){
        if(!mSocket.isClosed()) mSocket.close();
        mFinish = true;
    }

}