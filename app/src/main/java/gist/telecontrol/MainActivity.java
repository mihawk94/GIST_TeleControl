package gist.telecontrol;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;


public class MainActivity extends AppCompatActivity{

    private Handler handler = new Handler();
    int count = 0;

    private SensorManager mSensorManager;
    private Sensor mSensor;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView lanDevicesSearch = ((TextView)findViewById(R.id.lan_devices_text));
        LinearLayout lanDevicesView = ((LinearLayout)findViewById(R.id.lan_devices));
        Thread lanDevicesThread = new Thread(new Networking(handler, lanDevicesSearch, lanDevicesView));
        lanDevicesThread.start();
    }


    protected void onResume(){
        super.onResume();
    }

    protected void onPause(){
        super.onPause();
    }

    /*
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    public void setupAccelerometer(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent sensor){
    }
    /*
    /*
    private Runnable broadcasting = new Runnable(){
        @Override
        public void run() {
            count++;
            ((TextView)findViewById(R.id.info)).setText("" + count);
            handler.postDelayed(broadcasting, 500);
        }
    };
    */
}
