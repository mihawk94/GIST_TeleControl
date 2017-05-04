package gist.telecontrol;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class DataReceiver extends BroadcastReceiver{

    private Context mContext;

    public DataReceiver(Context context){
        mContext = context;
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("Logging", "Receiver has received a new message");

        switch(intent.getAction()){
            case "LAN_DEVICEREPLY":
                lan_deviceReply(context, intent);
                break;
            case "ENABLE_TVBUTTON":
                enable_tvButton(context, intent);
                break;
            default:
                break;
        }
    }

    public void lan_deviceReply(Context context, Intent intent){

        Toast.makeText(mContext, "" + intent.getStringExtra("name") + ": " + intent.getStringExtra("address"), Toast.LENGTH_LONG).show();


    }

    public void enable_tvButton(Context context, Intent intent){


        Button tvButton = (Button)((Activity)mContext).findViewById(R.id.tv_btn);
        Button phoneButton = (Button)((Activity)mContext).findViewById(R.id.phone_btn);

        tvButton.setBackgroundResource(R.drawable.custom_button);
        phoneButton.setBackgroundResource(R.drawable.custom_button);

    }
}