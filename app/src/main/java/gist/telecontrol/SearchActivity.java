package gist.telecontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class SearchActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;
    private IntentFilter mBluetoothFilter;
    private MessageLink mHandler;

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mHandler = new MessageLink(this);

        setFonts();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        DynamicUIThread dynamicUIThread = new DynamicUIThread(mHandler);
        dynamicUIThread.start();

        Intent i = new Intent(this, ConnectionService.class);
        i.setAction("Requesting");
        i.putExtra("name", getIntent().getStringExtra("name"));
        startService(i);

    }

    public void setFonts(){
        TextView tv=(TextView)findViewById(R.id.main_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/orange_juice_2.ttf");
        tv.setTypeface(face);
    }



}

/*
    private Handler handler = new Handler();
    int count = 0;

    private SensorManager mSensorManager;
    private Sensor mSensor;

/*
        TextView lanDevicesSearch = ((TextView)findViewById(R.id.lan_devices_text));
        LinearLayout lanDevicesView = ((LinearLayout)findViewById(R.id.lan_devices));
        Thread lanDevicesThread = new Thread(new Networking(handler, lanDevicesSearch, lanDevicesView));
        lanDevicesThread.start();
        */

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