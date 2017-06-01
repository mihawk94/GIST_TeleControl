package gist.telecontrol;

import java.net.Socket;
import java.util.HashMap;

public class LANDevice{

    String mName;
    String mAddress;

    public LANDevice(String name, String address){
        mName = name;
        mAddress = address;
    }

    public String getName(){
        return mName;
    }

    public String getAddress(){
        return mAddress;
    }
}