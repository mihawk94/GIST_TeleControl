package gist.telecontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {

    private ButtonListener mButtonListener;
    private Button mTvButton, mPhoneButton;
    private IntentFilter mConnectionFilter;
    private DataReceiver mReceiver;
    private boolean mRegisteredReceiver;

    public static final int REQUEST_PHONE = 0;
    public static final int REQUEST_TV = 1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFonts();

        setReceiver();

        setButtons();

    }

    public void setFonts(){
        TextView tv=(TextView)findViewById(R.id.main_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/orange_juice_2.ttf");
        tv.setTypeface(face);
    }

    public void setReceiver(){

        mConnectionFilter = new IntentFilter();
        mConnectionFilter.addAction("ENABLE_TVBUTTON");

        mReceiver = new DataReceiver(this);
    }

    public void setButtons(){

        mButtonListener = new ButtonListener(this);

        mTvButton = (Button)findViewById(R.id.tv_btn);
        mPhoneButton = (Button)findViewById(R.id.phone_btn);

        mTvButton.setOnClickListener(mButtonListener);
        mPhoneButton.setOnClickListener(mButtonListener);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_TV){
            mButtonListener.disable();
            disableButtonColor();
            Intent i = new Intent(this, ConnectionService.class);
            i.setAction("StopReplying");
            startService(i);
        }
    }

    protected void onDestroy(){
        super.onDestroy();
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

    protected void onPause(){
        super.onPause();
        if (mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mRegisteredReceiver = false;
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

    public void disableButtonColor(){
        mTvButton.setBackgroundResource(R.drawable.disabled_custom_button);
        mPhoneButton.setBackgroundResource(R.drawable.disabled_custom_button);
    }

}
