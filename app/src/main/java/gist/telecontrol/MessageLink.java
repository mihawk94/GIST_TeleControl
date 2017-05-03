package gist.telecontrol;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class MessageLink extends Handler{

    private Context mContext;
    public static final int LAN = 0;
    public static final int BLUETOOTH = 1;
    public static final String LAN_TEXT = "Searching";
    public static final String BLUETOOTH_TEXT = "Scanning";
    public boolean mLANMessaging = false;
    public boolean mBluetoothMessaging = false;

    public MessageLink(Context context){
        mContext = context;
    }

    public void handleMessage(Message msg){
        switch(msg.what){
            case LAN:
                if(mLANMessaging) ((TextView)((Activity)mContext).findViewById(R.id.lan_devices_text)).setText("" + LAN_TEXT + msg.obj);
                break;
            case BLUETOOTH:
                if(mBluetoothMessaging) ((TextView)((Activity)mContext).findViewById(R.id.bt_devices_text)).setText("" + BLUETOOTH_TEXT + msg.obj);
                break;
            default:
                break;
        }
    }

    public boolean getLANMessaging(){
        return mLANMessaging;
    }

    public boolean getBluetoothMessaging(){
        return mBluetoothMessaging;
    }

    public void setLANMessaging(boolean lanMessaging){
        mLANMessaging = lanMessaging;
    }

    public void setBluetoothMessaging(boolean bluetoothMessaging){
        mBluetoothMessaging = bluetoothMessaging;
    }
}