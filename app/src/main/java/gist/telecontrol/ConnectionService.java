package gist.telecontrol;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ConnectionService extends Service {

    public int onStartCommand(Intent intent, int flags, int startId){

        switch(intent.getAction()){
            case "Requesting":
                LANBroadcastingThread lanBroadcastingThread = new LANBroadcastingThread(this, intent.getStringExtra("name"), 0);
                lanBroadcastingThread.start();
                break;
            default:
                break;
        }

        return START_NOT_STICKY;
    }

    public IBinder onBind(Intent intent){
        return null;
    }
}