package gist.telecontrol;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    private String mName;
    private ArrayList<LANExchangerThread> mLANExchangerThreads;

    public LANConnectionThread(Context context){
        mLANDeviceHashMap = new HashMap<String, LANDevice>();
        mLANExchangerThreads = new ArrayList<LANExchangerThread>();
        mContext = context;
        mCode = 0;
    }

    public LANConnectionThread(Context context, InetAddress address, String name){
        mContext = context;
        mAddress = address;
        mName = name;
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
            mServerSocket = new ServerSocket(48184);
        }
        catch(IOException ioe){
            try{
                mServerSocket.close();
            }
            catch(IOException ioe2){
                //Give information about the error;
                return;
            }
            //Give information about the error;
            return;
        }

        while(!mFinish){

            try{
                mSocket = mServerSocket.accept();
            }
            catch(IOException ioe3){
                try{
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

        try {
            mSocket = new Socket(mAddress, 48184, InetAddress.getLocalHost(), 48183);
        } catch (IOException e) {
            //Give information about the error
            return;
        }

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

        if(mLANExchangerThreads.size() > 0){
            for(LANExchangerThread thread : mLANExchangerThreads) {
                thread.finish();
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