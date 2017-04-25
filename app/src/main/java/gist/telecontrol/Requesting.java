/*
package gist.telecontrol;

import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class Requesting implements Runnable{

    private Handler handler;
    private TextView textDevices;
    private LinearLayout devices;

    public Requesting(Handler handler, TextView textDevices, LinearLayout devices){
        this.handler = handler;
        this.textDevices = textDevices;
        this.devices = devices;
    }

    public void run(){

    }

    public static NetworkInterface getMainInterface(){
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface interf : Collections.list(interfaces)){
                if(getMainAddress(interf.getInetAddresses()) != null &&
                        !getMainAddress(interf.getInetAddresses()).getHostAddress().equals("127.0.0.1"))
                    return interf;
            }
        } catch (SocketException se) {
            se.printStackTrace();
        }
        return null;
    }

    public static InetAddress getMainAddress(Enumeration<InetAddress> addresses){
        for(InetAddress address : Collections.list(addresses)){
            if(address instanceof Inet4Address){
                return address;
            }
        }
        return null;
    }

    public static InetAddress getBroadcastAddress(List<InterfaceAddress> iaddresses, InetAddress ipaddr){
        for(InterfaceAddress iaddress : iaddresses){
            if(iaddress.getAddress().equals(ipaddr)) return iaddress.getBroadcast();
        }
        return null;
    }
}
*/