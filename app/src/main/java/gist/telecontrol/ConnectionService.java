package gist.telecontrol;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectionService extends Service {

    private LANRequestingThread mLANRequestingThread;
    private LANReplyingThread mLANReplyingThread;
    private LANConnectionThread mLANConnectionThread;

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
            case "Connection":
                Log.d("Logging", "Connection called");
                try{
                    mLANConnectionThread = new LANConnectionThread(this,
                            InetAddress.getByName(intent.getStringExtra("address")),
                            intent.getStringExtra("name"));
                }
                catch(UnknownHostException uhe){
                    //Give information about the error
                }
                break;
            case "StopRequesting":
                mLANRequestingThread.finish();
                break;
            case "StopReplying":
                mLANReplyingThread.finish();


                Intent enableButton = new Intent("ENABLE_TVBUTTON");
                LocalBroadcastManager.getInstance(this).sendBroadcast(enableButton);

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