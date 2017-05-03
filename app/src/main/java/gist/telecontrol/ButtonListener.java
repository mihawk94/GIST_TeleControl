package gist.telecontrol;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ButtonListener implements View.OnClickListener{

    Activity mActivity;
    MessageLink mHandler;
    DynamicUIThread mDynamicUIThread;

    public ButtonListener(Activity activity){
        mActivity = activity;
    }

    public ButtonListener(Activity activity, MessageLink handler){
        mActivity = activity;
        mHandler = handler;
    }

    public void onClick(View v) {

        Intent i;

        switch(v.getId()){
            case R.id.tv_btn:

                if(((EditText)(mActivity.findViewById(R.id.tv_name))).getText().toString().equals("") ||
                        ((EditText)(mActivity.findViewById(R.id.tv_name))).getText().toString() == null){
                    Toast.makeText(mActivity.getApplicationContext(), "Insert your device name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(((EditText)(mActivity.findViewById(R.id.tv_name))).getText().toString().toCharArray().length > 16){
                    Toast.makeText(mActivity.getApplicationContext(), "Maximum size of name: 16 symbols", Toast.LENGTH_SHORT).show();
                    return;
                }
                i = new Intent(mActivity, ServerActivity.class);
                i.putExtra("name", ((EditText)(mActivity.findViewById(R.id.tv_name))).getText().toString());
                mActivity.startActivityForResult(i, ((MainActivity)mActivity).REQUEST_TV);
                break;

            case R.id.phone_btn:
                if(((EditText)(mActivity.findViewById(R.id.phone_name))).getText().toString().equals("") ||
                        ((EditText)(mActivity.findViewById(R.id.phone_name))).getText().toString() == null){
                    Toast.makeText(mActivity.getApplicationContext(), "Insert your device name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(((EditText)(mActivity.findViewById(R.id.phone_name))).getText().toString().toCharArray().length > 16){
                    Toast.makeText(mActivity.getApplicationContext(), "Maximum size of name: 16 symbols", Toast.LENGTH_SHORT).show();
                    return;
                }
                i = new Intent(mActivity, SearchActivity.class);
                i.putExtra("name", ((EditText)(mActivity.findViewById(R.id.phone_name))).getText().toString());
                mActivity.startActivityForResult(i, ((MainActivity)mActivity).REQUEST_PHONE);
                break;

            case R.id.lan_btn:
                if(((Button)v).getText().equals("SEARCH")){
                    Log.d("Logging", "LAN: Searching..");

                    mHandler.setLANMessaging(true);
                    mDynamicUIThread = new DynamicUIThread(mHandler);
                    mDynamicUIThread.start();

                    //Call the service to connect
                    i = new Intent(mActivity, ConnectionService.class);
                    i.setAction("Requesting");
                    i.putExtra("name", mActivity.getIntent().getStringExtra("name"));
                    mActivity.startService(i);

                    //change button text
                    ((Button)v).setText("STOP");
                }
                else{
                    Log.d("Logging", "LAN: Search finished");
                    mHandler.setLANMessaging(false);
                    if(!mHandler.getBluetoothMessaging()){
                        mDynamicUIThread.finish();
                    }
                    //Stop search
                    i = new Intent(mActivity, ConnectionService.class);
                    i.setAction("StopRequesting");
                    i.putExtra("name", mActivity.getIntent().getStringExtra("name"));
                    mActivity.startService(i);
                    //change button text
                    ((Button)v).setText("SEARCH");
                    ((TextView)mActivity.findViewById(R.id.lan_devices_text)).setText("Touch the button to start searching");
                }
                break;

            case R.id.bluetooth_btn:
                if(((Button)v).getText().equals("SCAN")){
                    Log.d("Logging", "Bluetooth: Scanning..");

                    mHandler.setBluetoothMessaging(true);
                    mDynamicUIThread = new DynamicUIThread(mHandler);
                    mDynamicUIThread.start();
                    //Call the service to connect
                    //change button text
                }
                else{
                    Log.d("Logging", "Bluetooth: Scan finished");
                    mHandler.setBluetoothMessaging(false);
                    if(!mHandler.getLANMessaging()){
                        mDynamicUIThread.finish();
                    }
                    //Stop search
                    //change button text
                }
                break;


            default:
                break;
        }
    }
}