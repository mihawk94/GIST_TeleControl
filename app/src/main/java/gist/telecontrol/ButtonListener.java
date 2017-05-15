package gist.telecontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;

public class ButtonListener implements View.OnClickListener{

    private Context mContext;
    private MessageLink mHandler;
    private DynamicUIThread mDynamicUIThread;
    private boolean mEnabled = true;
    private AdapterLANDevice mLANDeviceAdapter;
    private HashSet<String> mLANDeviceHashSet;

    public ButtonListener(Context context){
        mContext = context;
    }

    public ButtonListener(Context context, MessageLink handler, AdapterLANDevice lanDeviceAdapter, HashSet<String> lanDeviceHashSet){
        mContext = context;
        mHandler = handler;
        mLANDeviceAdapter = lanDeviceAdapter;
        mLANDeviceHashSet = lanDeviceHashSet;
    }

    public void onClick(View v) {


        int color = Color.TRANSPARENT;
        Drawable background = v.getBackground();

        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();

        if(color == Color.parseColor("#818181")) return;

        Intent i;

        switch(v.getId()){
            case R.id.tv_btn:

                if(((EditText)(((Activity)mContext).findViewById(R.id.tv_name))).getText().toString().equals("") ||
                        ((EditText)(((Activity)mContext).findViewById(R.id.tv_name))).getText().toString() == null){
                    Toast.makeText(mContext.getApplicationContext(), "Insert your device name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(((EditText)(((Activity)mContext).findViewById(R.id.tv_name))).getText().toString().toCharArray().length > 16){
                    Toast.makeText(mContext.getApplicationContext(), "Maximum size of name: 16 symbols", Toast.LENGTH_SHORT).show();
                    return;
                }
                i = new Intent(mContext, ServerActivity.class);
                i.putExtra("name", ((EditText)(((Activity)mContext).findViewById(R.id.tv_name))).getText().toString());
                ((Activity)mContext).startActivityForResult(i, ((MainActivity) mContext).REQUEST_TV);
                break;

            case R.id.phone_btn:
                if(((EditText)(((Activity)mContext).findViewById(R.id.phone_name))).getText().toString().equals("") ||
                        ((EditText)(((Activity)mContext).findViewById(R.id.phone_name))).getText().toString() == null){
                    Toast.makeText(mContext.getApplicationContext(), "Insert your device name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(((EditText)(((Activity)mContext).findViewById(R.id.phone_name))).getText().toString().toCharArray().length > 16){
                    Toast.makeText(mContext.getApplicationContext(), "Maximum size of name: 16 symbols", Toast.LENGTH_SHORT).show();
                    return;
                }
                i = new Intent(mContext, SearchActivity.class);
                i.putExtra("name", ((EditText)(((Activity)mContext).findViewById(R.id.phone_name))).getText().toString());
                ((Activity)mContext).startActivityForResult(i, ((MainActivity) mContext).REQUEST_PHONE);
                break;

            case R.id.lan_btn:
                if(((Button)v).getText().equals("SEARCH")){
                    Log.d("Logging", "LAN: Searching..");

                    mLANDeviceAdapter.clear();
                    mLANDeviceHashSet.clear();

                    mHandler.setLANMessaging(true);
                    mDynamicUIThread = new DynamicUIThread(mHandler);
                    mDynamicUIThread.start();

                    //Call the service to connect
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("Requesting");
                    i.putExtra("name", ((Activity)mContext).getIntent().getStringExtra("name"));
                    mContext.startService(i);

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
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("StopRequesting");
                    mContext.startService(i);
                    //change button text
                    ((Button)v).setText("SEARCH");
                    ((TextView) ((Activity)mContext).findViewById(R.id.lan_devices_text)).setText("Touch the button to start searching");
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