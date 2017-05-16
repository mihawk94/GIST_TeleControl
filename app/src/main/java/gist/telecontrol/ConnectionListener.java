package gist.telecontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
        //Crear nueva actividad, conectarse al servicio e iniciarla. DynamicUIThread con Connecting, y no habilitar botones
        //hasta detener el intento de conexión.

        //Ver si botones están deshabilitados ()

        int LANcolor = Color.TRANSPARENT;
        int BTcolor = Color.TRANSPARENT;

        Button LANbtn = (Button)((Activity)mContext).findViewById(R.id.lan_btn);
        Button BTbtn = (Button)((Activity)mContext).findViewById(R.id.bluetooth_btn);

        Drawable LAN_background = LANbtn.getBackground();
        Drawable BT_background = BTbtn.getBackground();

        if (LAN_background instanceof ColorDrawable)
            LANcolor = ((ColorDrawable) LAN_background).getColor();

        if (BT_background instanceof ColorDrawable)
            BTcolor = ((ColorDrawable) BT_background).getColor();

        if(LANcolor == Color.parseColor("#9ea2a3") || BTcolor == Color.parseColor("#9ea2a3")) return;

        //Deshabilitar botones

        LANbtn.setBackgroundResource(R.drawable.disabled_scanning_button);
        BTbtn.setBackgroundResource(R.drawable.disabled_scanning_button);

        //Stop searching and scanning



        //Stop UI current messages

        if(((SearchActivity)mContext).getDynamicUIThread() != null){

            mHandler.setLANMessaging(false);
            mHandler.setBluetoothMessaging(false);

            ((SearchActivity)mContext).getDynamicUIThread().finish();

        }

        //UI message: connecting

        mHandler.setConnectionMessaging(true);

        DynamicUIThread connectingThread = new DynamicUIThread(mHandler);
        connectingThread.start();

        //Start connection

        Intent i = new Intent(mContext, ConnectionService.class);
        i.putExtra("localName", ((Activity)mContext).getIntent().getStringExtra("name"));
        i.putExtra("address", mAdapterLANDevice.getItem(position).getAddress());
        i.setAction("Connection");
        mContext.startService(i);

    }

}