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
                Message msg = Message.obtain();
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


                msg.what = mHandler.LAN;
                sendMessage(msg, message);

                msg.what = mHandler.BLUETOOTH;
                sendMessage(msg, message);


                try{
                    Thread.sleep(300);
                } catch (InterruptedException iex){
                    mFinish = true;
                }
            }
        }
    }

    public void sendMessage(Message msg, String value){
        msg.obj = value;
        msg.setTarget(mHandler);
        msg.sendToTarget();
    }

    public void setFinish(boolean finish){
        mFinish = finish;
    }
}