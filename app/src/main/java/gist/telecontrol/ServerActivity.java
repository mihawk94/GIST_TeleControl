package gist.telecontrol;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.ScrollingMovementMethod;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

public class ServerActivity extends FragmentActivity {

    private IntentFilter mConnectionFilter;
    private DataReceiver mReceiver;
    private boolean mRegisteredReceiver;
    private ListView mDevices, mClients;
    private AdapterLANDevice mLANDeviceAdapter, mLANClientAdapter;
    private ConnectionListener mConnectionListener;
    private ArrayList<LANDevice> mDeviceArrayList, mClientArrayList;
    private HashSet<String> mLANDeviceHashSet, mLANClientHashSet;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        setFonts();

        setListing();

        setReceiver();

        setListeners();

        ((TextView)(findViewById(R.id.main_title))).setText(getIntent().getStringExtra("name") + ": attached devices");
        ((TextView)(findViewById(R.id.log))).setMovementMethod(new ScrollingMovementMethod());

        Intent i = new Intent(this, ConnectionService.class);
        i.setAction("Replying");
        i.putExtra("name", getIntent().getStringExtra("name"));
        startService(i);

        setResult(RESULT_OK);

    }

    private void setFonts(){
        TextView tv=(TextView)findViewById(R.id.main_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/orange_juice_2.ttf");
        tv.setTypeface(face);
    }

    private void setListing(){

        mDevices = (ListView) findViewById(R.id.lanDevicesListView);

        mClients = (ListView) findViewById(R.id.lanClientsListView);

        mDeviceArrayList = new ArrayList<LANDevice>();

        mClientArrayList = new ArrayList<LANDevice>();

        mLANDeviceHashSet = new HashSet<String>();

        mLANClientHashSet = new HashSet<String>();

        mLANDeviceAdapter = new AdapterLANDevice(this, mDeviceArrayList);

        mLANClientAdapter = new AdapterLANDevice(this, mClientArrayList);

        mDevices.setAdapter(mLANDeviceAdapter);

        mClients.setAdapter(mLANClientAdapter);

    }

    private void setReceiver(){

        mConnectionFilter = new IntentFilter();

        mConnectionFilter.addAction("LAN_RECEIVEDMSG");
        mConnectionFilter.addAction("NETWORK_ERROR");
        mConnectionFilter.addAction("UPDATE_LOG");
        mConnectionFilter.addAction("UPDATE_ALL_LOG");

        mReceiver = new DataReceiver(this, mLANDeviceAdapter, mDeviceArrayList, mLANDeviceHashSet, mLANClientAdapter, mClientArrayList, mLANClientHashSet);
    }

    private void setListeners(){

        mConnectionListener = new ConnectionListener(this, mLANDeviceAdapter);

        mDevices.setOnItemClickListener(mConnectionListener);

    }

    protected void onDestroy(){
        super.onDestroy();
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

        Intent i = new Intent(this, ConnectionService.class);
        i.setAction("UpdateUI");
        startService(i);
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

    public void onBackPressed(){
        finish();
    }

}