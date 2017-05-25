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
    private LANExchangerThread mLANExchangerThread;

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
                            intent.getStringExtra("localName"), intent.getStringExtra("name"));
                    mLANConnectionThread.start();
                }
                catch(UnknownHostException uhe){
                    //Give information about the error
                }
                break;
            case "SendMessage":
                Log.d("Logging", "Sending message..");
                mLANExchangerThread = new LANExchangerThread(this, mLANConnectionThread.getSocket(), intent.getStringExtra("message"));
                mLANExchangerThread.start();
                break;
            case "StopRequesting":
                Log.d("Logging", "Stopping requesting..");

                mLANRequestingThread.finish();

                Intent enableButton_req = new Intent("ENABLE_TVBUTTON");
                LocalBroadcastManager.getInstance(this).sendBroadcast(enableButton_req);

                break;
            case "StopReplying":
                mLANReplyingThread.finish();


                Intent enableButton_rep = new Intent("ENABLE_TVBUTTON");
                LocalBroadcastManager.getInstance(this).sendBroadcast(enableButton_rep);

                break;
            case "StopConnection":
                mLANConnectionThread.finish();
                Log.d("Logging", "Disconnected");

                Intent finishConnection = new Intent("STOP_CONNECTION");
                LocalBroadcastManager.getInstance(this).sendBroadcast(finishConnection);

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