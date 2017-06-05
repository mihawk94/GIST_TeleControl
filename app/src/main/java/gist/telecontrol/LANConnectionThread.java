package gist.telecontrol;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.ConnectionPendingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

import static android.R.attr.port;

public class LANConnectionThread extends Thread{

    private Context mContext;
    private InetAddress mAddress;
    private int mCode;
    private boolean mFinish;
    private ServerSocket mServerSocket;
    private Socket mSocket;
    private String mName, mLocalName;
    private int mPort;
    private HashSet<String> mNames;
    private LANExchangerThread mLANExchangerThread;
    private ArrayList<LANExchangerThread> mLANExchangerThreads;

    public LANConnectionThread(Context context, int port){
        mLANExchangerThreads = new ArrayList<LANExchangerThread>();
        mContext = context;
        mPort = port;
        if(mPort == 48186) mNames = new HashSet<String>();
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

        Intent intent = new Intent("NETWORK_ERROR");

        mFinish = false;

        mServerSocket = null;

        try{
            Log.d("Logging", "Creating serverSocket..");
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(mPort));
        }
        catch(IOException ioe){
            Log.d("Logging", "Error creating the serverSocket");
            Log.d("Logging", ioe.toString());
            //Information about the error
            intent.putExtra("message", "CONNECT: Error creating the serverSocket");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
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
                    if(!mServerSocket.isClosed()) mServerSocket.close();
                }
                catch(IOException ioe2){
                    //Information about the error
                    intent.putExtra("message", "CONNECT: Error closing the serverSocket");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;
                }
                //Information about the error
                intent.putExtra("message", "CONNECT: Error accepting new connection");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

            LANExchangerThread listenerThread;

            if(mPort == 48186){
                listenerThread = new LANExchangerThread(mContext, mSocket, mPort, mNames);
            }
            else{
                listenerThread = new LANExchangerThread(mContext, mSocket, mPort);
            }

            listenerThread.start();

            mLANExchangerThreads.add(listenerThread);

            Log.d("Logging", mPort + ": Exchanger threads: " + mLANExchangerThreads.size());

        }
    }

    private void runClient(){

        Intent intent;

        Log.d("Logging", "Connecting socket");
        try {
            mSocket = new Socket();
            mSocket.setReuseAddress(true);
            mSocket.bind(new InetSocketAddress(LANRequestingThread.getMainAddress(LANRequestingThread.getMainInterface().getInetAddresses()), 48183));
        }
        catch (IOException e) {
            Log.d("Logging", "Creating socket error");
            //Information about the error
            intent = new Intent("NETWORK_ERROR");
            intent.putExtra("message", "CONNECT: Error while creating socket after an error");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }

        try{
            Log.d("Logging", "Trying to connect..");
            mSocket.connect(new InetSocketAddress(mAddress, 48184), 3000);
            Log.d("Logging", "Connected succesfully!");

        } catch(SocketTimeoutException te){
            Log.d("Logging", "Connection timeout");
            try{
                if(!mSocket.isClosed()) mSocket.close();
            } catch(IOException e1){
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "CONNECT: Error closing socket after timeout");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            //Information about the error
            intent = new Intent("NETWORK_ERROR");
            intent.putExtra("message", "CONNECT: Timeout");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        } catch(IOException e){
            try{
                if(!mSocket.isClosed()) mSocket.close();
            } catch(IOException e1){
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "CONNECT: Error closing socket after an error");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            Log.d("Logging", e.toString());
            //Information about the error
            intent = new Intent("NETWORK_ERROR");
            intent.putExtra("message", "CONNECT: Error while connecting socket to broker");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }

        intent = new Intent("ACTIVITY_CONTROL");

        intent.putExtra("name", mName);
        intent.putExtra("localName", mLocalName);

        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        mLANExchangerThread = new LANExchangerThread(mContext, mSocket, "NAME: " + mLocalName);
        mLANExchangerThread.start();

        InputStream tmpIn;
        //OutputStream tmpOut;

        try {
            tmpIn = mSocket.getInputStream();
            //tmpOut = mSocket.getOutputStream();
        } catch (IOException e) {
            try{
                if(!mSocket.isClosed()) mSocket.close();
            }
            catch(IOException ioe){
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "EXCHANGE_CLIENT: Error while closing socket after an error: " + mAddress);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            //Information about the error
            intent = new Intent("NETWORK_ERROR");
            intent.putExtra("message", "EXCHANGE_CLIENT: Error while creating socket input: " + mAddress);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }

        byte [] reply = new byte[32];
        int bytes = 0;
        byte [] word;

        /*
        try{
            mSocket.setSoTimeout(5000);
        }
        catch(SocketException se){
            try{
                if(!mSocket.isClosed()) mSocket.close();
            }
            catch(IOException ioe){
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "EXCHANGE_CLIENT: Error while closing socket after an error setting its timeout: " + mAddress);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            //Information about the error
            intent = new Intent("NETWORK_ERROR");
            intent.putExtra("message", "EXCHANGE_CLIENT: Error while setting socket timeout: " + mAddress);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;

        }
        */

        /*
        mLANExchangerThread = new LANExchangerThread(mContext, mSocket, tmpOut);
        mLANExchangerThread.start();
        */


        mFinish = false;

        while(!mFinish){

            try{
                bytes = tmpIn.read(reply);
                Log.d("Logging", "" + bytes);
            }/*
            catch(SocketTimeoutException te){
                Log.d("Logging", "Waiting timeout");
                try{
                    if(!mSocket.isClosed()) mSocket.close();
                } catch(IOException e1){
                    //Information about the error
                    intent = new Intent("NETWORK_ERROR");
                    intent.putExtra("message", "EXCHANGE_CLIENT: Error closing socket after timeout");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;
                }
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "EXCHANGE_CLIENT: Timeout");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }*/
            catch(IOException ioe){
                finish();
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "EXCHANGE_CLIENT: Error while reading input bytes: " + mAddress);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

            if(bytes != -1){
                word = Arrays.copyOfRange(reply, 0, bytes);

                Log.d("Logging", new String(word));
                continue;
            }
            else{
                finish();
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "EXCHANGE_CLIENT: The broker has been disconnected");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
        }

    }

    public void finish(){

        Log.d("Logging", "Finishing thread..");
        mFinish = true;

        Intent intent = new Intent("NETWORK_ERROR");

        if(mServerSocket != null){
            if(!mServerSocket.isClosed()){
                try {
                    Log.d("Logging", "Closing serverSocket..");
                    mServerSocket.close();
                } catch (IOException e) {
                    //Information about the error

                    intent.putExtra("message", "CONNECT: Error while closing socket at exit");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
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
                    //Information about the error

                    intent.putExtra("message", "CONNECT: Error while closing socket at exit");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;

                }
            }
        }

    }

    public Socket getSocket(){
        return mSocket;
    }

    public ArrayList<LANExchangerThread> getLANExchangerThreads(){
        return mLANExchangerThreads;
    }

    public LANExchangerThread getLANExchangerThread(){
        return mLANExchangerThread;
    }

}