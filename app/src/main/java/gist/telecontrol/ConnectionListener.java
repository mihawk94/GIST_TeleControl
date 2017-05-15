package gist.telecontrol;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

public class ConnectionListener implements AdapterView.OnItemClickListener{

    private Context mContext;
    private AdapterLANDevice mAdapterLANDevice;

    public ConnectionListener(Context context, AdapterLANDevice adapterLANDevice){
        mContext = context;
        mAdapterLANDevice = adapterLANDevice;
    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Continuar aquí.
        //Crear nueva actividad, conectarse al servicio e iniciarla. DynamicUIThread con Connecting, y no habilitar botones
        //hasta detener el intento de conexión.
        mAdapterLANDevice.getItem(position).getAddress();

    }

}