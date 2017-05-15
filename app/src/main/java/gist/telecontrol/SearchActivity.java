package gist.telecontrol;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class SearchActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;
    private IntentFilter mConnectionFilter;
    private DataReceiver mReceiver;
    private boolean mRegisteredReceiver;

    private MessageLink mHandler;

    private Button mScanButton, mSearchButton;
    private ButtonListener mButtonListener;
    private ConnectionListener mConnectionListener;
    private ListView mDevices;
    private AdapterLANDevice mLANDeviceAdapter;
    private HashSet<String> mLANDeviceHashSet;

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_DEVICE_CONNECTION = 2;
    private final static int REQUEST_PERMISSIONS = 3;

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setFonts();

        setListing();

        setReceiver();

        setButtons();

        setBluetoothPermissions();

        setListeners();

    }

    private void setFonts(){
        TextView tv=(TextView)findViewById(R.id.main_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/orange_juice_2.ttf");
        tv.setTypeface(face);
    }

    private void setListing(){

        mDevices = (ListView) findViewById(R.id.lanDevicesListView);

        ArrayList<LANDevice> deviceList = new ArrayList<LANDevice>();

        mLANDeviceHashSet = new HashSet<String>();

        mLANDeviceAdapter = new AdapterLANDevice(this, deviceList);

        mDevices.setAdapter(mLANDeviceAdapter);

    }

    private void setReceiver(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mConnectionFilter = new IntentFilter();
        mConnectionFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mConnectionFilter.addAction("LAN_DEVICEREPLY");

        mReceiver = new DataReceiver(this, mLANDeviceAdapter, mLANDeviceHashSet);
    }

    private void setButtons(){

        mHandler = new MessageLink(this);

        mButtonListener = new ButtonListener(this, mHandler, mLANDeviceAdapter, mLANDeviceHashSet);

        mSearchButton = (Button)findViewById(R.id.bluetooth_btn);
        mScanButton = (Button)findViewById(R.id.lan_btn);

        mSearchButton.setOnClickListener(mButtonListener);
        mScanButton.setOnClickListener(mButtonListener);
    }

    private void setBluetoothPermissions(){

        String[] permissionsToRequest =
                {
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                };

        boolean allPermissionsGranted = true;

        for(String permission : permissionsToRequest) {
            allPermissionsGranted = allPermissionsGranted && (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
        }

        if(!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, REQUEST_PERMISSIONS);
        }
    }

    private void setListeners(){

        mConnectionListener = new ConnectionListener();

        mDevices.setOnItemClickListener(mConnectionListener);
    }

    protected void onDestroy(){

        super.onDestroy();

        mHandler.setBluetoothMessaging(false);
        mHandler.setLANMessaging(false);

        if(mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mRegisteredReceiver = false;
        }

    }

    protected void onPause(){
        super.onPause();
        if (mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mRegisteredReceiver = false;
        }
    }

    protected void onResume(){
        super.onResume();
        if (!mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mConnectionFilter);
            mRegisteredReceiver = true;
        }
    }

    protected void onStart(){
        super.onStart();
        if (!mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mConnectionFilter);
            mRegisteredReceiver = true;
        }
    }

    protected void onStop(){
        super.onStop();
        if (mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mRegisteredReceiver = false;
        }
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