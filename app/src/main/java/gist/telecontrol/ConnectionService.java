package gist.telecontrol;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ConnectionService extends Service {

    public int onStartCommand(Intent intent, int flags, int startId){

        

        return START_NOT_STICKY;
    }

    public IBinder onBind(Intent intent){
        return null;
    }
}