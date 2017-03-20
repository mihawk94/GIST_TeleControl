import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class Addressing{
	public static void main (String [] args){
		if(getMainInterface() == null){
				System.out.println("No se pudo obtener la interfaz principal.");
                //Toast.makeText(MainActivity.this, "Couldn't get any network interface.", Toast.LENGTH_SHORT);
        }
        else{
            InetAddress ipaddr = getMainAddress(getMainInterface().getInetAddresses());
            if(ipaddr == null){
            	System.out.println("No se pudo obtener la direccción IP");
                //Toast.makeText(MainActivity.this, "Couldn't get local IP address.", Toast.LENGTH_SHORT);
            }
            else{
            	System.out.println("Dirección IP: " + ipaddr.getHostAddress());
            	InetAddress braddr = getBroadcastAddress(getMainInterface().getInterfaceAddresses(), ipaddr);
            	if(braddr == null){
            		System.out.println("No se pudo obtener la dirección broadcast");
            	}
            	else{
            		System.out.println("Dirección broadcast: " + braddr.getHostAddress());
            	}
            }
        }
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
            //Toast.makeText(MainActivity.this, "Couldn't get socket info.", Toast.LENGTH_SHORT);
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