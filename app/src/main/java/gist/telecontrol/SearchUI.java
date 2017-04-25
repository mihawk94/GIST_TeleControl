/*
package gist.telecontrol;

import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

public class SearchUI implements Runnable{

    private String title;
    private Handler handler;
    private TextView textDevices;
    private LinearLayout devices;
    private int count = 0;

    public SearchUI(String title, Handler handler, TextView textDevices, LinearLayout devices){
        this.title = title;
        this.handler = handler;
        this.textDevices = textDevices;
        this.devices = devices;
    }

    public void run(){
        if(count == 0){
            textDevices.setText(title + ".");
            count++;
        }
        else if(count == 1){
            textDevices.setText(title + "..");
            count++;
        }
        else{
            textDevices.setText(title + "...");
            count = 0;
        }
        handler.postDelayed(this, 500);
    }
}
*/