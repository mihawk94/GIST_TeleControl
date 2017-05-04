package gist.telecontrol;

import android.content.Context;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashMap;

public class LANConnectionThread extends Thread{

    private Context mContext;
    private InetAddress mAddress;
    private int mCode;
    private boolean mFinish;
    private HashMap<String,LANDevice> mLANDeviceHashMap;

    public LANConnectionThread(Context context){
        mContext = context;
        mCode = 0;
    }

    public LANConnectionThread(Context context, InetAddress address){
        mContext = context;
        mAddress = address;
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

    public void runServer(){

        mFinish = false;

        int port = 48184;

        ServerSocket socket = null;

        while(!mFinish){

            try{
                socket = new ServerSocket(port);
            }
            catch(IOException ioe){
                try{
                    socket.close();
                }
                catch(IOException ioe2){
                    //Give information about the error;
                    return;
                }
                //Give information about the error;
                return;
            }

            try{
                socket.accept();
            }
            catch(IOException ioe3){
                try{
                    socket.close();
                }
                catch(IOException ioe2){
                    //Give information about the error;
                    return;
                }
                //Give information about the error;
                return;
            }

            port++;

        }
    }

    public void runClient(){

        //en la excepción al realizar connect(), se debe tener en cuenta que en el intervalo de tiempo entre que
        //cliente recibe el mensaje de la TV con el puerto para conectarse y se conecta, otro

        mFinish = false;

        while(!mFinish){

        }
    }

    public void finish(){

    }
}