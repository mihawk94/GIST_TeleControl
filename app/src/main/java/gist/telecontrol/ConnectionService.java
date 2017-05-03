package gist.telecontrol;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ConnectionService extends Service {

    private LANRequestingThread mLANRequestingThread;
    private LANReplyingThread mLANReplyingThread;

    public int onStartCommand(Intent intent, int flags, int startId){

        Log.d("Logging", "Service called");

        switch(intent.getAction()){
            case "Requesting":
                Log.d("Logging", "Requesting called");
                mLANRequestingThread = new LANRequestingThread(this, intent.getStringExtra("name"));
                mLANRequestingThread.start();
                break;
            case "Replying":
                Log.d("Logging", "Replying called");
                mLANReplyingThread = new LANReplyingThread(this, intent.getStringExtra("name"));
                mLANReplyingThread.start();
                break;
            case "StopRequesting":
                mLANRequestingThread.finish();
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