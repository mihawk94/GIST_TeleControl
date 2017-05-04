package gist.telecontrol;

import android.app.Service;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class LANRequestingThread extends Thread{

    private boolean mFinish;
    private String mName;
    private Context mContext;
    private DatagramSocket mSocket;
    private LANReplyingThread mLANReplyingThread;

    public LANRequestingThread(Context context, String name){
        mContext = context;
        mName = name;
    }

    public void run(){

        if(getMainInterface() == null){
            Log.d("Logging", "Error getting main interface");
            //Give information about the error
            return;
        }

        InetAddress ipaddr = getMainAddress(getMainInterface().getInetAddresses());

        if(ipaddr == null){
            Log.d("Logging", "Error getting main address");
            //Give information about the error
            return;
        }

        InetAddress braddr = getBroadcastAddress(getMainInterface().getInterfaceAddresses(), ipaddr);

        if(braddr == null){
            Log.d("Logging", "Error getting broadcast address");
            //Give information about the error
            return;
        }

        byte [] request = new String("REQUEST: " + mName).getBytes();

        DatagramPacket requestPacket = new DatagramPacket(request, request.length);


        requestPacket.setAddress(braddr);

        requestPacket.setPort(48182);


        try{
            mSocket = new DatagramSocket(48181);
        }
        catch(SocketException se){
            //Give information about the error
            return;
        }

        mFinish = false;

        mLANReplyingThread = new LANReplyingThread(mContext, mSocket);
        mLANReplyingThread.start();

        while(!mFinish){

            try{
                Log.d("Logging", "Broadcasting...");
                mSocket.setBroadcast(true);
                mSocket.send(requestPacket);
            }
            catch(IOException ioe){
                mSocket.close();
                //Give information about the error
                return;
            }

            try{
                Thread.sleep(1000);
            } catch (InterruptedException iex){
                mFinish = true;
            }

        }

    }

    public NetworkInterface getMainInterface(){
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface interf : Collections.list(interfaces)){
                if(getMainAddress(interf.getInetAddresses()) != null &&
                        !getMainAddress(interf.getInetAddresses()).getHostAddress().equals("127.0.0.1"))
                    return interf;
            }
        } catch (SocketException se) {
            return null;
        }
        return null;
    }

    public InetAddress getMainAddress(Enumeration<InetAddress> addresses){
        for(InetAddress address : Collections.list(addresses)){
            if(address instanceof Inet4Address){
                return address;
            }
        }
        return null;
    }

    public InetAddress getBroadcastAddress(List<InterfaceAddress> iaddresses, InetAddress ipaddr){
        for(InterfaceAddress iaddress : iaddresses){
            if(iaddress.getAddress().equals(ipaddr)) return iaddress.getBroadcast();
        }
        return null;
    }

    public void finish(){
        if(!mSocket.isClosed()) mSocket.close();
        mFinish = true;
        mLANReplyingThread.finish();
    }

}