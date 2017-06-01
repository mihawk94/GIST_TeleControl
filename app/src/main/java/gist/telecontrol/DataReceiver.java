package gist.telecontrol;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class DataReceiver extends BroadcastReceiver{

    private Context mContext;
    private AdapterLANDevice mDevices, mClients;
    private ArrayList<LANDevice> mDeviceArrayList, mClientArrayList;
    private HashSet<String> mLANDeviceHashSet, mLANClientHashSet;
    private FragmentManager mFragmentManager;

    public DataReceiver(Context context){
        mContext = context;
    }

    public DataReceiver(Context context, AdapterLANDevice devices, ArrayList<LANDevice> deviceArrayList, HashSet<String> lanDeviceHashSet, AdapterLANDevice clients, ArrayList<LANDevice> clientArrayList, HashSet<String> lanClientHashSet){
        mContext = context;
        mDevices = devices;
        mDeviceArrayList = deviceArrayList;
        mLANDeviceHashSet = lanDeviceHashSet;
        mClients = clients;
        mClientArrayList = clientArrayList;
        mLANClientHashSet = lanClientHashSet;
        mFragmentManager = ((ServerActivity)mContext).getSupportFragmentManager();
    }

    public DataReceiver(Context context, AdapterLANDevice devices, ArrayList<LANDevice> deviceArrayList, HashSet<String> lanDeviceHashSet){
        mContext = context;
        mDevices = devices;
        mDeviceArrayList = deviceArrayList;
        mLANDeviceHashSet = lanDeviceHashSet;
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("Logging", "Receiver has received a new message: " + intent.getAction());

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
            case "STOP_CONNECTION":
                stop_connection(context, intent);
                break;
            case "NETWORK_ERROR":
                network_error(context, intent);
                break;
            default:
                break;
        }
    }

    private void lan_deviceReply(Context context, Intent intent){
        if(!(mContext instanceof SearchActivity)) return;

        if(!mLANDeviceHashSet.contains(intent.getStringExtra("address"))){
            mDeviceArrayList.add(new LANDevice(intent.getStringExtra("name"), intent.getStringExtra("address")));
            mLANDeviceHashSet.add(intent.getStringExtra("address"));
            mDevices.notifyDataSetChanged();
            Toast.makeText(mContext, intent.getStringExtra("name") + "\n" + intent.getStringExtra("address"), Toast.LENGTH_SHORT).show();
        }
    }

    private void enable_tvButton(Context context, Intent intent){

        if(!(mContext instanceof MainActivity)) return;

        Log.d("Logging", "Enabling main buttons..");

        Button tvButton = (Button)((Activity)mContext).findViewById(R.id.tv_btn);
        Button phoneButton = (Button)((Activity)mContext).findViewById(R.id.phone_btn);

        if(((MainActivity)mContext).getIfDeviceIsTv()) {
            tvButton.setBackgroundResource(R.drawable.custom_button);
            phoneButton.setBackgroundResource(R.drawable.disabled_custom_button);
        }
        else{
            tvButton.setBackgroundResource(R.drawable.disabled_custom_button);
            phoneButton.setBackgroundResource(R.drawable.custom_button);
        }

        ((MainActivity)mContext).setConnection(false);

    }

    private void lan_receivedMsg(Context context, Intent intent){

        if(!(mContext instanceof ServerActivity)) return;

        Log.d("Logging", "Received new message: " + intent.getStringExtra("message"));

        String data = intent.getStringExtra("message");
        String address = intent.getStringExtra("address");

        String command = data.substring(0, data.indexOf(" "));
        String value = data.substring(data.indexOf(" ") + 1);

        switch(command){
            case "PRESS:":
                press(value, address);
                break;
            case "RELEASE:":
                release(value, address);
                break;
            case "48184:NAME:":
                Log.d("Logging", "New connection..");
                if(mLANDeviceHashSet.contains(address)) return;
                mLANDeviceHashSet.add(address);
                mDeviceArrayList.add(new LANDevice(value, address));
                mDevices.notifyDataSetChanged();

                /*
                Log.d("Logging", "New fragment..");

                ControlFragment fragment = ControlFragment.newInstance(value);
                mFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, fragment, address)
                        .addToBackStack(null)
                        .commit();
                */

                Toast.makeText(mContext, "'" + value + "' is connected", Toast.LENGTH_SHORT).show();
                break;
            case "48186:NAME:":
                Log.d("Logging", "New connection: " + value);
                if(mLANClientHashSet.contains(value)) return;
                mLANClientHashSet.add(value);
                mClientArrayList.add(new LANDevice(value, address));
                mClients.notifyDataSetChanged();

                /*
                Log.d("Logging", "New fragment..");

                ControlFragment fragment = ControlFragment.newInstance(value);
                mFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, fragment, address)
                        .addToBackStack(null)
                        .commit();

                Toast.makeText(mContext, "'" + value + "' is connected", Toast.LENGTH_SHORT).show();
                break;
                */
            default:
                break;
        }

    }

    private void press(String value, String address){
        if(mFragmentManager.findFragmentByTag(address) == null) return;
        switch (value){
            case "CH_UP":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.ch_up)
                        .getBackground()).setColor(Color.parseColor("#FF4A148C"));
                break;
            case "VOL_DOWN":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.vol_down)
                        .getBackground()).setColor(Color.parseColor("#FF4A148C"));
                break;
            case "VOL_UP":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.vol_up)
                        .getBackground()).setColor(Color.parseColor("#FF4A148C"));
                break;
            case "CH_DOWN":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.ch_down)
                        .getBackground()).setColor(Color.parseColor("#FF4A148C"));
                break;
            default:
                break;
        }
    }

    private void release(String value, String address){
        if(mFragmentManager.findFragmentByTag(address) == null) return;
        switch (value){
            case "CH_UP":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.ch_up)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            case "VOL_DOWN":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.vol_down)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            case "VOL_UP":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.vol_up)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            case "CH_DOWN":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.ch_down)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            default:
                break;
        }
    }

    private void activity_control(Context context, Intent intent){

        //Detener hilo que controla el mensaje de la UI "Connecting"
        if(!(mContext instanceof SearchActivity)) return;

        Log.d("Logging", "Connecting socket");

        if(((SearchActivity)mContext).getDynamicUIThread() != null){

            ((SearchActivity)mContext).getDynamicUIThread().getHandler().setConnectionMessaging(false);
            ((SearchActivity)mContext).getDynamicUIThread().finish();

        }

        ((TextView)((Activity)mContext).findViewById(R.id.lan_devices_text)).setText("Touch the button to start searching");

        ((TextView)((Activity)mContext).findViewById(R.id.lan_btn)).setText("SEARCH");


        //Iniciar nueva actividad

        Intent i = new Intent(mContext, ControlActivity.class);
        //put the name of the Server.
        i.putExtra("name", intent.getStringExtra("name"));
        i.putExtra("localName", intent.getStringExtra("localName"));
        ((Activity)mContext).startActivityForResult(i, ((SearchActivity) mContext).REQUEST_CONNECTION);


        //Falta: onDestroy() de SearchActivityctivity: verificar si la conexion esta cerrada.
        //onPressedBackButton(): ver los botones.

        //       en activityForResult realizar desconexion al volver hacia atrás. (Necesario crear un RESULT_CONNECTION)
        //       Definir la nueva actividad
    }

    private void stop_connection(Context context, Intent intent){

        if(!(mContext instanceof SearchActivity)) return;

        Button lanButton = (Button)((Activity)mContext).findViewById(R.id.lan_btn);

        lanButton.setBackgroundResource(R.drawable.scanning_button);

        ((SearchActivity)mContext).setConnection(false);
    }

    private void network_error(Context context, Intent intent){

        String data = intent.getStringExtra("message");

        String command = data.substring(0, data.indexOf(" "));
        String value = data.substring(data.indexOf(" ") + 1);

        Intent removingThread;

        switch(command){
            case "REQUEST:":
                if(!(mContext instanceof SearchActivity)) return;
                Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
                ((SearchActivity)mContext).getHandler().setLANMessaging(false);
                stop_connection(context, intent);
                ((SearchActivity)mContext).getDynamicUIThread().finish();
                ((Button)((SearchActivity)mContext).findViewById(R.id.lan_btn)).setText("SEARCH");
                ((TextView)((SearchActivity)mContext).findViewById(R.id.lan_devices_text)).setText("Touch the button to start searching");
                break;
            case "REPLY:":
                if((mContext instanceof SearchActivity)){
                    Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
                    ((SearchActivity)mContext).getHandler().setConnectionMessaging(false);
                    stop_connection(context, intent);
                    ((SearchActivity)mContext).getDynamicUIThread().finish();
                    ((Button)((SearchActivity)mContext).findViewById(R.id.lan_btn)).setText("SEARCH");
                    ((TextView)((SearchActivity)mContext).findViewById(R.id.lan_devices_text)).setText("Touch the button to start searching");
                }
                else if(mContext instanceof ServerActivity){
                    Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
                    ((ServerActivity)mContext).finish();
                }
                break;
            case "CONNECT:":
                if(mContext instanceof SearchActivity) {
                    Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
                    ((SearchActivity)mContext).getHandler().setConnectionMessaging(false);
                    stop_connection(context, intent);
                    ((SearchActivity) mContext).getDynamicUIThread().finish();
                    ((Button) ((SearchActivity) mContext).findViewById(R.id.lan_btn)).setText("SEARCH");
                    ((TextView) ((SearchActivity) mContext).findViewById(R.id.lan_devices_text)).setText("Touch the button to start searching");
                }
                else if(mContext instanceof ServerActivity){
                Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
                ((ServerActivity)mContext).finish();
                }
                break;
            case "48184:EXCHANGE_SERVER:":
                Log.d("Logging", "48184:EXCHANGE_SERVER error called!");
                if(!(mContext instanceof ServerActivity)) return;
                String address = value.substring(value.indexOf("/") + 1);
                if(!mLANDeviceHashSet.contains(address)){
                    Log.d("Logging", "Device isn't connected");
                    return;
                }

                /*
                Log.d("Logging", "Proceeding to remove the fragment and the item");

                Fragment fragment = mFragmentManager.findFragmentByTag(address);
                if(fragment != null){
                    Log.d("Logging", "Removing fragment");
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                */

                mLANDeviceHashSet.remove(address);

                for(int i = 0; i < mDeviceArrayList.size(); i++){
                    if(mDeviceArrayList.get(i).getAddress().equals(address)){
                        mDeviceArrayList.remove(i);
                        break;
                    }
                }

                /*
                Fragment fragment1;

                FragmentTransaction transaction = mFragmentManager.beginTransaction();

                for (Fragment currentFragment : mFragmentManager.getFragments()) {
                    transaction.hide(currentFragment);
                }

                if(!mDeviceArrayList.isEmpty()){
                    fragment1 = mFragmentManager.findFragmentByTag(mDeviceArrayList.get(mDeviceArrayList.size()-1).getAddress());
                    transaction.show(fragment1);
                }
                transaction.addToBackStack(null);
                transaction.commit();


                */
                mDevices.notifyDataSetChanged();
                Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();

                removingThread = new Intent(mContext, ConnectionService.class);
                removingThread.setAction("RemoveThreadServer");
                removingThread.putExtra("address", address);
                mContext.startService(removingThread);
                break;
            case "48186:EXCHANGE_SERVER:":
                Log.d("Logging", "48186:EXCHANGE_SERVER error called!");
                if(!(mContext instanceof ServerActivity)) return;
                String name = value.substring(value.indexOf("/") + 1);
                if(!mLANClientHashSet.contains(name)){
                    Log.d("Logging", "Client " + name + " isn't connected");
                    return;
                }

                mLANClientHashSet.remove(name);

                for(int i = 0; i < mClientArrayList.size(); i++){
                    if(mClientArrayList.get(i).getName().equals(name)){
                        mClientArrayList.remove(i);
                        break;
                    }
                }

                mClients.notifyDataSetChanged();
                Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();

                removingThread = new Intent(mContext, ConnectionService.class);
                removingThread.setAction("RemoveThreadApp");
                removingThread.putExtra("name", name);
                mContext.startService(removingThread);


                break;
            case "EXCHANGE_CLIENT:":
                if(!(mContext instanceof ControlActivity)) return;
                Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
                ((ControlActivity)mContext).finish();
                break;
            default:
                break;
        }
    }
}