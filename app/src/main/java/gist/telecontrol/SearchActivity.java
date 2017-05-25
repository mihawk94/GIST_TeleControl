package gist.telecontrol;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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
    private DynamicUIThread mDynamicUIThread;
    private AdapterLANDevice mLANDeviceAdapter;
    private HashSet<String> mLANDeviceHashSet;

    private boolean mConnected;

    public final static int REQUEST_ENABLE_BT = 1;
    public final static int REQUEST_CONNECTION = 2;
    public final static int REQUEST_PERMISSIONS = 3;

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setFonts();

        setListing();

        setReceiver();

        setButtons();

        setBluetoothPermissions();

        setListeners();

        setResult(RESULT_OK);

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
        mConnectionFilter.addAction("ACTIVITY_CONTROL");
        mConnectionFilter.addAction("STOP_CONNECTION");

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

        mConnectionListener = new ConnectionListener(this, mLANDeviceAdapter, mHandler);

        mDevices.setOnItemClickListener(mConnectionListener);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("Logging", "Now searching again...");
        if (resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_CONNECTION){
            Log.d("Logging", "Stopping connection..");
            mLANDeviceAdapter.clear();
            Intent i = new Intent(this, ConnectionService.class);
            i.setAction("StopConnection");
            startService(i);
        }
    }

    //Cambiar a protected, no funciona bien, ver por qué
    public void onBackPressed()
    {
        Log.d("Logging", "Going to main screen...");

        if(mConnected) return;

        super.onBackPressed();
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

    public DynamicUIThread getDynamicUIThread(){
        return mDynamicUIThread;
    }

    public boolean isConnected(){
        return mConnected;
    }

    public void setConnection(boolean status){
        mConnected = status;
    }

    public void setDynamicUIThread(DynamicUIThread dynamicUIThread){
        mDynamicUIThread = dynamicUIThread;
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