package gist.telecontrol;

import android.app.Activity;
import android.app.UiModeManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {

    private ButtonListener mButtonListener;
    private Button mTvButton, mPhoneButton;
    private IntentFilter mConnectionFilter;
    private DataReceiver mReceiver;
    private boolean mRegisteredReceiver;
    private boolean mIsTV;

    private boolean mConnected;

    public static final int REQUEST_PHONE = 1;
    public static final int REQUEST_TV = 2;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            mIsTV = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Log.d("Logging", "This is an Android TV");
        }
        else {
            mIsTV = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Log.d("Logging", "This is not an Android TV");
        }

        //In case system doesn't detect the TV, we decide whether it's a TV or not obtaining the current orientation

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mIsTV = false;
        }
        else{
            mIsTV = true;
        }

        setContentView(R.layout.activity_main);

        setFonts();

        setReceiver();

        setButtons();

    }

    private void setFonts(){
        TextView tv=(TextView)findViewById(R.id.main_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/orange_juice_2.ttf");
        tv.setTypeface(face);
    }

    private void setReceiver(){

        mConnectionFilter = new IntentFilter();

        mConnectionFilter.addAction("ENABLE_TVBUTTON");

        mReceiver = new DataReceiver(this);
    }

    private void setButtons(){

        mButtonListener = new ButtonListener(this);

        mTvButton = (Button)findViewById(R.id.tv_btn);
        mPhoneButton = (Button)findViewById(R.id.phone_btn);

        mTvButton.setOnClickListener(mButtonListener);
        mPhoneButton.setOnClickListener(mButtonListener);

        if(mIsTV) {
            Log.d("Logging", "Disabling phone button...");
            mTvButton.setBackgroundResource(R.drawable.custom_button);
            mPhoneButton.setBackgroundResource(R.drawable.disabled_custom_button);
        }
        else{
            Log.d("Logging", "Disabling TV button...");
            mTvButton.setBackgroundResource(R.drawable.disabled_custom_button);
            mPhoneButton.setBackgroundResource(R.drawable.custom_button);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_TV){
            disableButtonColor();
            Intent i = new Intent(this, ConnectionService.class);
            i.setAction("StopReplying");
            startService(i);
        }
        if(requestCode == REQUEST_PHONE){
            Log.d("Logging", "Main screen..");
            disableButtonColor();
            Intent i = new Intent(this, ConnectionService.class);
            i.setAction("StopRequesting");
            startService(i);
            stopService(i);
        }
    }

    protected void onDestroy(){
        super.onDestroy();
        if (mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mRegisteredReceiver = false;
        }
        Log.d("Logging", "Quitting the application...");
        stopService(new Intent(this, ConnectionService.class));
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

    public boolean isConnected(){
        return mConnected;
    }

    public void setConnection(boolean status){
        mConnected = status;
    }

    public boolean getIfDeviceIsTv(){
        return mIsTV;
    }

}
