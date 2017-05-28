package gist.telecontrol;

import java.io.OutputStream;

public class LANKeepAliveThread extends Thread{

    private OutputStream mOutputStream;
    private boolean mFinish;

    public LANKeepAliveThread(OutputStream outputStream){
        mOutputStream = outputStream;
    }

    public void run(){

        mFinish = true;

        while(!mFinish){


        }
    }

    public void finish() {
        mFinish = true;
    }
}