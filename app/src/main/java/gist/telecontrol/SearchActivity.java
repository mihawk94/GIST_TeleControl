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

    private IntentFilter mConnectionFilter;
    private DataReceiver mReceiver;
    private boolean mRegisteredReceiver;

    private MessageLink mHandler;

    private Button mSearchButton;
    private ButtonListener mButtonListener;
    private ConnectionListener mConnectionListener;
    private ListView mDevices;
    private DynamicUIThread mDynamicUIThread;
    private AdapterLANDevice mLANDeviceAdapter;
    private HashSet<String> mLANDeviceHashSet;
    private ArrayList<LANDevice> mDeviceArrayList;

    private boolean mConnected;

    public final static int REQUEST_CONNECTION = 1;

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setFonts();

        setListing();

        setReceiver();

        setButtons();

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

        mDeviceArrayList = new ArrayList<LANDevice>();

        mLANDeviceHashSet = new HashSet<String>();

        mLANDeviceAdapter = new AdapterLANDevice(this, mDeviceArrayList);

        mDevices.setAdapter(mLANDeviceAdapter);

    }

    private void setReceiver(){

        mConnectionFilter = new IntentFilter();
        mConnectionFilter.addAction("LAN_DEVICEREPLY");
        mConnectionFilter.addAction("ACTIVITY_CONTROL");
        mConnectionFilter.addAction("STOP_CONNECTION");
        mConnectionFilter.addAction("NETWORK_ERROR");

        mReceiver = new DataReceiver(this, mLANDeviceAdapter, mDeviceArrayList, mLANDeviceHashSet);
    }

    private void setButtons(){

        mHandler = new MessageLink(this);

        mButtonListener = new ButtonListener(this, mHandler, mLANDeviceAdapter, mLANDeviceHashSet);

        mSearchButton = (Button)findViewById(R.id.lan_btn);

        mSearchButton.setOnClickListener(mButtonListener);
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

        mHandler.setLANMessaging(false);
        if(mDynamicUIThread != null)mDynamicUIThread.finish();

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

        if(mDynamicUIThread != null){

            mDynamicUIThread.getHandler().setConnectionMessaging(false);
            mDynamicUIThread.getHandler().setLANMessaging(false);
            mDynamicUIThread.finish();

        }

        ((TextView)findViewById(R.id.lan_devices_text)).setText("Touch the button to start searching");

        ((TextView)findViewById(R.id.lan_btn)).setText("SEARCH");
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

    public MessageLink getHandler(){
        return mHandler;
    }
}
