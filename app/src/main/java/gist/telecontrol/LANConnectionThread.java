package gist.telecontrol;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LANConnectionThread extends Thread{

    private Context mContext;
    private InetAddress mAddress;
    private int mCode;
    private boolean mFinish;
    private HashMap<String,LANDevice> mLANDeviceHashMap;
    private ServerSocket mServerSocket;
    private Socket mSocket;
    private String mName, mLocalName;
    private ArrayList<LANExchangerThread> mLANExchangerThreads;


    public LANConnectionThread(Context context){
        mLANDeviceHashMap = new HashMap<String, LANDevice>();
        mLANExchangerThreads = new ArrayList<LANExchangerThread>();
        mContext = context;
        mCode = 0;
    }

    public LANConnectionThread(Context context, InetAddress address, String localName, String name){
        mContext = context;
        mAddress = address;
        mName = name;
        mLocalName = localName;
        mCode = 1;
    }

    public void run(){

        switch(mCode){
            case 0:
                runServer();
                break;
            case 1:
                runClient();
                break;
            default:
                break;
        }

    }

    private void runServer(){

        mFinish = false;

        mServerSocket = null;

        try{
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(48184));
        }
        catch(IOException ioe){
            try{
                mServerSocket.close();
            }
            catch(IOException ioe2){
                Log.d("Logging", "Error closing the serverSocket");
                //Give information about the error;
                return;
            }
            Log.d("Logging", "Error creating the serverSocket");
            Log.d("Logging", ioe.toString());
            //Give information about the error;
            return;
        }

        while(!mFinish){

            try{
                Log.d("Logging", "Listening for connections..");
                mSocket = mServerSocket.accept();
            }
            catch(IOException ioe3){
                try{
                    Log.d("Logging", "Listening has ended");
                    mServerSocket.close();
                }
                catch(IOException ioe2){
                    //Give information about the error;
                    return;
                }
                //Give information about the error;
                return;
            }

            LANExchangerThread listenerThread = new LANExchangerThread(mContext, mSocket, mLANDeviceHashMap);
            listenerThread.start();

            mLANExchangerThreads.add(listenerThread);

        }
    }

    private void runClient(){

        Intent intent = new Intent("ACTIVITY_CONTROL");

        Log.d("Logging", "Connecting socket");
        try {
            mSocket = new Socket();
            mSocket.setReuseAddress(true);
            mSocket.bind(new InetSocketAddress(LANRequestingThread.getMainAddress(LANRequestingThread.getMainInterface().getInetAddresses()), 48183));
            mSocket.connect(new InetSocketAddress(mAddress, 48184));
            //mSocket = new Socket(mAddress, 48184, LANRequestingThread.getMainAddress(LANRequestingThread.getMainInterface().getInetAddresses()), 48183);
        } catch (IOException e) {
            Log.d("Logging", "Connecting socket error");
            //Give information about the error
            Log.d("Logging", e.toString());
            return;
        }

        intent.putExtra("name", mName);
        intent.putExtra("localName", mLocalName);

        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        OutputStream tmpOut;

        try {
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
        try{
            tmpOut.write(new String("NAME: " + mName).getBytes());
        }
        catch(IOException ioe){
            //Give information about the error
        }


    }

    public void finish(){

        mFinish = true;

        if(mServerSocket != null){
            if(!mServerSocket.isClosed()){
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    //Give information about the error
                    return;
                }
            }
        }

        if(mLANExchangerThreads != null){
            if(mLANExchangerThreads.size() > 0){
                for(LANExchangerThread thread : mLANExchangerThreads) {
                    thread.finish();
                }
            }
        }

        if(mSocket != null){
            if(!mSocket.isClosed()){
                try {
                    mSocket.close();
                } catch (IOException e) {
                    //Give information about the error
                    return;
                }
            }
        }

    }

    public Socket getSocket(){
        return mSocket;
    }

}