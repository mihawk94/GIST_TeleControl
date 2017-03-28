package gist.telecontrol;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Networking implements Runnable{

    int count;
    Handler handler;
    TextView counter;

    public Networking(int count, Handler handler, TextView counter){
        this.count = count;
        this.handler = handler;
        this.counter = counter;
    }
    public void run(){
            count++;
            counter.setText("" + count);
            handler.postDelayed(this, 500);
    }
}