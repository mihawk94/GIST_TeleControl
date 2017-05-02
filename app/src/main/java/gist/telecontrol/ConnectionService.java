package gist.telecontrol;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ConnectionService extends Service {

    public int onStartCommand(Intent intent, int flags, int startId){

        switch(intent.getAction()){
            case "Requesting":
                LANRequestingThread lanRequestingThread = new LANRequestingThread(this, intent.getStringExtra("name"));
                lanRequestingThread.start();
                break;
            case "Replying":
                LANReplyingThread lanReplyingThread = new LANReplyingThread(this, intent.getStringExtra("name"));
                lanReplyingThread.start();
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