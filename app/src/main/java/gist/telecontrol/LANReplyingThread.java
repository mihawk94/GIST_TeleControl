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
import java.util.Arrays;

public class LANReplyingThread extends Thread{

    private DatagramSocket mSocket;
    private Context mContext;
    private boolean mFinish;
    private String mName;
    private int mCode;
    private LANConnectionThread mLANConnectionThread;


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

        Intent intent;

        mFinish = false;

        while(!mFinish){

            byte [] reply = new byte[100];
            DatagramPacket replyPacket = new DatagramPacket(reply, reply.length);

            try {
                Log.d("Logging", "Waiting for replies...");
                Log.d("Logging", mSocket.getLocalAddress().getHostAddress() + " " + mSocket.getLocalPort());
                mSocket.receive(replyPacket);
                Log.d("Logging", "Reply received from one device!");
            } catch (IOException e) {
                if(!mSocket.isClosed()) mSocket.close();
                Log.d("Logging", "Error receiving packet/Search stopped");
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "REPLY: Search stopped");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

            byte [] word = Arrays.copyOfRange(reply, 0, replyPacket.getLength());

            String data = new String(word);

            intent = new Intent("LAN_DEVICEREPLY");

            String command = data.substring(0, data.indexOf(" "));
            String value = data.substring(data.indexOf(" ") + 1);

            intent.putExtra("address", new String(replyPacket.getAddress().getHostAddress()));
            intent.putExtra("name", value);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        }

    }

    private void runServer(){

        Intent intent = new Intent("NETWORK_ERROR");

        mLANConnectionThread = new LANConnectionThread(mContext);
        mLANConnectionThread.start();

        try {
            mSocket = new DatagramSocket(48182);
        } catch (SocketException e) {
            Log.d("Logging", "Error creating socket");
            //Information about the error
            intent.putExtra("message", "REPLY: Error creating socket");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
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
                if(!mSocket.isClosed()) mSocket.close();
                //Information about the error
                intent.putExtra("message", "REPLY: Error receiving request packet");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

            byte [] word = Arrays.copyOfRange(reply, 0, replyPacket.getLength());

            String requestName = new String(word);

            replyPacket.setPort(48181);

            replyPacket.setData(new String("REPLY: " + mName).getBytes());

            try {
                Log.d("Logging", "Sending info to " + replyPacket.getAddress().getHostAddress() + ":" + replyPacket.getPort() + " " + requestName);
                mSocket.send(replyPacket);
            } catch (IOException e) {
                if(!mSocket.isClosed()) mSocket.close();
                //Information about the error.
                intent.putExtra("message", "REPLY: Error sending reply packet");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

        }

    }

    public void finish(){

        mFinish = true;

        if(!mSocket.isClosed()) mSocket.close();

        if(mLANConnectionThread != null) mLANConnectionThread.finish();
    }

}