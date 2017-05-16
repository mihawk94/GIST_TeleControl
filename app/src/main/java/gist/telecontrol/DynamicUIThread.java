package gist.telecontrol;

import android.app.Activity;
import android.os.Message;
import android.util.Log;

public class DynamicUIThread extends Thread{

    private boolean mFinish;
    private MessageLink mHandler;

    public DynamicUIThread(MessageLink handler){
        mHandler = handler;
    }

    public void run(){

        mFinish = false;

        while(!mFinish){

            for (int i = 0; i < 3; i++) {
                Message msg;
                String message = "";
                switch (i) {
                    case 0:
                        message = ".";
                        break;
                    case 1:
                        message = "..";
                        break;
                    case 2:
                        message = "...";
                        break;
                }

                msg = Message.obtain();
                msg.what = mHandler.LAN;
                sendMessage(msg, message);

                msg = Message.obtain();
                msg.what = mHandler.BLUETOOTH;
                sendMessage(msg, message);

                msg = Message.obtain();
                msg.what = mHandler.CONNECTION;
                sendMessage(msg, message);


                try{
                    Thread.sleep(300);
                } catch (InterruptedException iex){
                    mFinish = true;
                }
            }
        }
    }

    private void sendMessage(Message msg, String value){
        msg.obj = value;
        msg.setTarget(mHandler);
        msg.sendToTarget();
    }

    public MessageLink getHandler(){
        return mHandler;
    }

    public void finish(){
        mFinish = true;
    }
}