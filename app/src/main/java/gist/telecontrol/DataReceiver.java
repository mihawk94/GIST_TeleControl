package gist.telecontrol;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DataReceiver extends BroadcastReceiver{


    public void onReceive(Context context, Intent intent) {
        Log.d("Logging", "Receiver has received a new message");

        switch(intent.getAction()){
            case "LAN_DEVICEREPLY":
                lan_deviceReply(context, intent);
                break;
            default:
                break;
        }
    }

    public void lan_deviceReply(Context context, Intent intent){
        Toast.makeText(context, "" + intent.getStringExtra("name") + ": " + intent.getStringExtra("address"), Toast.LENGTH_LONG).show();
    }
}