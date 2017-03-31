package gist.telecontrol;

import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;

public class Searching implements Runnable{

    private Handler handler;
    private TextView textDevices;
    private LinearLayout devices;
    private final static String LAN = "Searching LAN devices";
    private int count = 0;

    public Searching(Handler handler, TextView textDevices, LinearLayout devices){
        this.handler = handler;
        this.textDevices = textDevices;
        this.devices = devices;
    }

    public void run(){
        if(count == 0){
            textDevices.setText(LAN + ".");
            count++;
        }
        else if(count == 1){
            textDevices.setText(LAN + "..");
            count++;
        }
        else{
            textDevices.setText(LAN + "...");
            count = 0;
        }
        handler.postDelayed(this, 500);
    }
}