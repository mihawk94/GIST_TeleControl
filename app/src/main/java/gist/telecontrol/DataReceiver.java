package gist.telecontrol;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashSet;

public class DataReceiver extends BroadcastReceiver{

    private Context mContext;
    private AdapterLANDevice mDevices;
    private HashSet<String> mLANDeviceHashSet;

    public DataReceiver(Context context){
        mContext = context;
    }

    public DataReceiver(Context context, AdapterLANDevice devices, HashSet<String> lanDeviceHashSet){
        mContext = context;
        mDevices = devices;
        mLANDeviceHashSet = lanDeviceHashSet;
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
            case "LAN_RECEIVEDMSG":
                lan_receivedMsg(context, intent);
                break;
            case "ACTIVITY_CONTROL":
                activity_control(context, intent);
                break;
            default:
                break;
        }
    }

    private void lan_deviceReply(Context context, Intent intent){
        if(!mLANDeviceHashSet.contains(intent.getStringExtra("address"))){
            mDevices.add(new LANDevice(intent.getStringExtra("name"), intent.getStringExtra("address")));
            mLANDeviceHashSet.add(intent.getStringExtra("address"));
        }
        mDevices.notifyDataSetChanged();
        Toast.makeText(mContext, intent.getStringExtra("name") + "\n" + intent.getStringExtra("address"), Toast.LENGTH_LONG).show();
    }

    private void enable_tvButton(Context context, Intent intent){

        Button tvButton = (Button)((Activity)mContext).findViewById(R.id.tv_btn);
        Button phoneButton = (Button)((Activity)mContext).findViewById(R.id.phone_btn);

        tvButton.setBackgroundResource(R.drawable.custom_button);
        phoneButton.setBackgroundResource(R.drawable.custom_button);

    }

    private void lan_receivedMsg(Context context, Intent intent){

        Toast.makeText(mContext, intent.getStringExtra("name"), Toast.LENGTH_LONG).show();

    }

    private void activity_control(Context context, Intent intent){

        //Detener hilo que controla el mensaje de la UI "Connecting"

        if(((SearchActivity)mContext).getDynamicUIThread() != null){

            ((SearchActivity)mContext).getDynamicUIThread().getHandler().setConnectionMessaging(false);
            ((SearchActivity)mContext).getDynamicUIThread().finish();

        }

        //Iniciar nueva actividad

        /*
        Intent i = new Intent(mContext, ControlActivity.class);
        i.putExtra("name", ((EditText)(((Activity)mContext).findViewById(R.id.tv_name))).getText().toString());
        ((Activity)mContext).startActivityForResult(i, ((MainActivity) mContext).REQUEST_TV);
        */

        //Falta: onDestroy() de SearchActivityctivity: verificar si la conexion esta cerrada.
        //       en activityForResult realizar desconexion al volver hacia atrás. (Necesario crear un RESULT_CONNECTION)
        //       Definir la nueva actividad
    }
}