package gist.telecontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

public class ConnectionListener implements AdapterView.OnItemClickListener{

    private Context mContext;
    private AdapterLANDevice mAdapterLANDevice;
    private MessageLink mHandler;

    public ConnectionListener(Context context, AdapterLANDevice adapterLANDevice, MessageLink handler){
        mContext = context;
        mAdapterLANDevice = adapterLANDevice;
        mHandler = handler;
    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Continuar aquí.
        //Ver si esta conectandose

        if(((SearchActivity)mContext).isConnected()) return;

        ((SearchActivity)mContext).setConnection(true);

        //Deshabilitar botones

        ((SearchActivity)mContext).findViewById(R.id.lan_btn).setBackgroundResource(R.drawable.disabled_scanning_button);
        ((SearchActivity)mContext).findViewById(R.id.bluetooth_btn).setBackgroundResource(R.drawable.disabled_scanning_button);

        //Stop searching and scanning

        Intent i_stop = new Intent(mContext, ConnectionService.class);
        i_stop.setAction("StopRequesting");
        mContext.startService(i_stop);

        //Stop UI current messages

        if(((SearchActivity)mContext).getDynamicUIThread() != null){

            mHandler.setLANMessaging(false);
            mHandler.setBluetoothMessaging(false);

            ((SearchActivity)mContext).getDynamicUIThread().finish();

        }

        //UI message: connecting

        mHandler.setConnectionMessaging(true);

        DynamicUIThread connectingThread = new DynamicUIThread(mHandler);
        ((SearchActivity)mContext).setDynamicUIThread(connectingThread);
        connectingThread.start();

        //Start connection

        Intent i = new Intent(mContext, ConnectionService.class);
        i.putExtra("localName", ((Activity)mContext).getIntent().getStringExtra("name"));
        i.putExtra("name", mAdapterLANDevice.getItem(position).getName());
        i.putExtra("address", mAdapterLANDevice.getItem(position).getAddress());
        i.setAction("Connection");
        mContext.startService(i);

    }

}