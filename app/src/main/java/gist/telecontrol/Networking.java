package gist.telecontrol;

import android.content.Context;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Networking implements Runnable{

    Handler handler;
    TextView textDevices;
    LinearLayout devices;

    public Networking(Handler handler, TextView textDevices, LinearLayout devices){
        this.handler = handler;
        this.textDevices = textDevices;
        this.devices = devices;
    }
    public void run(){
        handler.post(new Searching(handler, textDevices, devices));
        Thread requestDevices = new Thread(new Requesting());
        Thread discoverDevices = new Thread(new Discovering());
    }
}