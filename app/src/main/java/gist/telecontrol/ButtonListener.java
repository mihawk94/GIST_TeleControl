package gist.telecontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;

public class ButtonListener implements View.OnClickListener, View.OnTouchListener{

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


        if(mContext instanceof MainActivity){
            if(((MainActivity)mContext).isConnected()){
                Log.d("Logging", "Socket connected in MainActivity");
                return;
            }
        }

        if(mContext instanceof SearchActivity){
            if(((SearchActivity)mContext).isConnected()){
                Log.d("Logging", "Socket connected in SearchActivity");
                return;
            }
        }

        Intent i;

        switch(v.getId()){
            case R.id.tv_btn:
                if(!((MainActivity)mContext).getIfDeviceIsTv()) return;

                if(((EditText)(((Activity)mContext).findViewById(R.id.tv_name))).getText().toString().equals("") ||
                        ((EditText)(((Activity)mContext).findViewById(R.id.tv_name))).getText().toString() == null){
                    Toast.makeText(mContext.getApplicationContext(), "Insert your device name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(((EditText)(((Activity)mContext).findViewById(R.id.tv_name))).getText().toString().toCharArray().length > 16){
                    Toast.makeText(mContext.getApplicationContext(), "Maximum size of name: 16 symbols", Toast.LENGTH_SHORT).show();
                    return;
                }
                ((MainActivity)mContext).setConnection(true);
                i = new Intent(mContext, ServerActivity.class);
                i.putExtra("name", ((EditText)(((Activity)mContext).findViewById(R.id.tv_name))).getText().toString());
                ((Activity)mContext).startActivityForResult(i, ((MainActivity) mContext).REQUEST_TV);
                break;

            case R.id.phone_btn:
                if(((MainActivity)mContext).getIfDeviceIsTv()) return;

                if(((EditText)(((Activity)mContext).findViewById(R.id.phone_name))).getText().toString().equals("") ||
                        ((EditText)(((Activity)mContext).findViewById(R.id.phone_name))).getText().toString() == null){
                    Toast.makeText(mContext.getApplicationContext(), "Insert your device name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(((EditText)(((Activity)mContext).findViewById(R.id.phone_name))).getText().toString().toCharArray().length > 16){
                    Toast.makeText(mContext.getApplicationContext(), "Maximum size of name: 16 symbols", Toast.LENGTH_SHORT).show();
                    return;
                }
                ((MainActivity)mContext).setConnection(true);
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
                    ((SearchActivity)mContext).setDynamicUIThread(mDynamicUIThread);
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
                    ((SearchActivity)mContext).getDynamicUIThread().finish();
                    //Stop search
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("StopRequesting");
                    mContext.startService(i);
                    //change button text
                    ((Button)v).setText("SEARCH");
                    ((TextView) ((Activity)mContext).findViewById(R.id.lan_devices_text)).setText("Touch the button to start searching");
                }
                break;

            default:
                break;
        }
    }

    public boolean onTouch(View v, MotionEvent event){

        /*
        if(!((ControlActivity)mContext).getSent()){
            if(!(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)){
                return true;
            }
        }
        */

        Intent i;

        switch(v.getId()){

            case R.id.ch_up:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#FF4A148C"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "PRESS: CH_UP");
                    mContext.startService(i);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#616161"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "RELEASE: CH_UP");
                    mContext.startService(i);
                }
                else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#616161"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "RELEASE: CH_UP");
                    mContext.startService(i);
                }
                break;
            case R.id.vol_down:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#FF4A148C"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "PRESS: VOL_DOWN");
                    mContext.startService(i);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#616161"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "RELEASE: VOL_DOWN");
                    mContext.startService(i);
                }
                else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#616161"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "RELEASE: VOL_DOWN");
                    mContext.startService(i);
                }
                break;
            case R.id.vol_up:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#FF4A148C"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "PRESS: VOL_UP");
                    mContext.startService(i);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#616161"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "RELEASE: VOL_UP");
                    mContext.startService(i);
                }
                else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#616161"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "RELEASE: VOL_UP");
                    mContext.startService(i);
                }
                break;
            case R.id.ch_down:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#FF4A148C"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "PRESS: CH_DOWN");
                    mContext.startService(i);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#616161"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "RELEASE: CH_DOWN");
                    mContext.startService(i);
                }
                else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    ((GradientDrawable)v.getBackground()).setColor(Color.parseColor("#616161"));
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "RELEASE: CH_DOWN");
                    mContext.startService(i);
                }
                break;
            case R.id.touch:

                Log.d("Logging", "Dimensions: " + v.getWidth() + "x" + v.getHeight());
                float x, y;

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("Logging", "X: " + event.getX() + " Y: " + event.getY());
                    x = event.getX()/v.getWidth();
                    y = event.getY()/v.getHeight();
                    if(x > 0.90) x = 0.90F;
                    if(x < 0) x = 0.10F;
                    if(y > 1) y = 0.90F;
                    if(y < 0) y = 0.10F;
                    Log.d("Logging", "%x: " + x + " %y: " + y);
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "TOUCH_DOWN: X:" + x + " Y:" + y);
                    mContext.startService(i);
                    return true;
                }
                /*
                else if(event.getAction() == MotionEvent.ACTION_MOVE){
                    Log.d("Logging", "X: " + event.getX() + " Y: " + event.getY());
                    x = event.getX()/v.getWidth();
                    y = event.getY()/v.getHeight();
                    if(x > 1) x = 1;
                    if(x < 0) x = 0;
                    if(y > 1) y = 1;
                    if(y < 0) y = 0;
                    Log.d("Logging", "%x: " + x + " %y: " + y);
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "TOUCH_MOVE: X:" + x + " Y:" + y);
                    mContext.startService(i);
                    return true;
                }
                */
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    Log.d("Logging", "Touch released");
                    i = new Intent(mContext, ConnectionService.class);
                    i.setAction("SendMessage");
                    i.putExtra("message", "TOUCH_RELEASE: ");
                    mContext.startService(i);
                    return false;
                }

        }
        return false;
    }

    public DynamicUIThread getDynamicUIThread(){
        return mDynamicUIThread;
    }

}