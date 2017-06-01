package gist.telecontrol;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ControlActivity extends Activity{

    private IntentFilter mConnectionFilter;
    private DataReceiver mReceiver;
    private boolean mRegisteredReceiver;
    private ButtonListener mButtonListener;
    private Button mChannelUp;
    private Button mVolDown;
    private Button mVolUp;
    private Button mChannelDown;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        setFonts();

        setReceiver();

        setButtons();

        ((TextView)(findViewById(R.id.main_title))).setText(getIntent().getStringExtra("localName"));
        ((TextView)(findViewById(R.id.local_name))).setText("Controlling broker: " + getIntent().getStringExtra("name"));

        setResult(RESULT_OK);

    }

    private void setFonts(){
        TextView tv=(TextView)findViewById(R.id.main_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/orange_juice_2.ttf");
        tv.setTypeface(face);
    }

    private void setReceiver(){

        mConnectionFilter = new IntentFilter();

        mConnectionFilter.addAction("NETWORK_ERROR");

        mReceiver = new DataReceiver(this);
    }

    private void setButtons(){

        mButtonListener = new ButtonListener(this);

        mChannelUp = (Button)findViewById(R.id.ch_up);
        mVolDown = (Button)findViewById(R.id.vol_down);
        mVolUp = (Button)findViewById(R.id.vol_up);
        mChannelDown = (Button)findViewById(R.id.ch_down);

        mChannelUp.setOnTouchListener(mButtonListener);
        mVolDown.setOnTouchListener(mButtonListener);
        mVolUp.setOnTouchListener(mButtonListener);
        mChannelDown.setOnTouchListener(mButtonListener);

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