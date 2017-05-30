package gist.telecontrol;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ConnectionService extends Service {

    private LANRequestingThread mLANRequestingThread;
    private LANReplyingThread mLANReplyingThread;
    private LANConnectionThread mLANConnectionThread, mLANConnectionClientThread;
    private LANExchangerThread mLANExchangerThread;

    public int onStartCommand(Intent intent, int flags, int startId){

        Log.d("Logging", "Service called. Action: " + intent.getAction());

        if(mLANReplyingThread != null){
            if(mLANReplyingThread.isAlive()) Log.d("Logging", "Replying is alive");
        }

        if(mLANConnectionClientThread != null){
            if(mLANConnectionClientThread.isAlive()) Log.d("Logging", "Client server socket is alive");
        }

        if(mLANConnectionThread != null){
            if(mLANConnectionThread.isAlive()) Log.d("Logging", "Connection thread is alive");
        }

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
                Log.d("Logging", "Creating clients server socket...");
                mLANConnectionClientThread = new LANConnectionThread(this, 48186);
                mLANConnectionClientThread.start();
                break;
            case "Connection":
                Log.d("Logging", "Connection called");
                try{
                    mLANConnectionThread = new LANConnectionThread(this,
                            InetAddress.getByName(intent.getStringExtra("address")),
                            intent.getStringExtra("localName"), intent.getStringExtra("name"));
                    mLANConnectionThread.start();
                    Log.d("Logging", "Connection started!");
                }
                catch(UnknownHostException uhe){
                    Log.d("Logging", "Unknown host exception");
                }
                break;
            case "SendMessage":
                Log.d("Logging", "Sending message..");
                mLANExchangerThread = new LANExchangerThread(this, mLANConnectionThread.getSocket(), intent.getStringExtra("message"));
                mLANExchangerThread.start();
                break;
            case "StopRequesting":
                Log.d("Logging", "Stopping requesting..");

                if(mLANRequestingThread != null) mLANRequestingThread.finish();

                Intent enableButton_req = new Intent("ENABLE_TVBUTTON");
                LocalBroadcastManager.getInstance(this).sendBroadcast(enableButton_req);

                break;
            case "StopReplying":

                Log.d("Logging", "Stopping replying...");
                if(mLANReplyingThread != null) mLANReplyingThread.finish();
                if(mLANConnectionClientThread != null) mLANConnectionClientThread.finish();

                Intent enableButton_rep = new Intent("ENABLE_TVBUTTON");
                LocalBroadcastManager.getInstance(this).sendBroadcast(enableButton_rep);
                stopSelf();

                break;
            case "StopConnection":
                mLANConnectionThread.finish();
                Log.d("Logging", "Disconnected");

                Intent finishConnection = new Intent("STOP_CONNECTION");
                LocalBroadcastManager.getInstance(this).sendBroadcast(finishConnection);
                stopSelf();

                break;
            case "UpdateServerUI":
                Log.d("Logging", "Checking for updates..");

                Intent updateUI;

                if(mLANConnectionClientThread != null){
                    if(mLANConnectionClientThread.getLANExchangerThreads().size() > 0){
                        for (int i = 0; i < mLANConnectionClientThread.getLANExchangerThreads().size(); i++){
                            if(mLANConnectionClientThread.getLANExchangerThreads().get(i) == null) continue;
                            Log.d("Logging", "Getting client: " + mLANConnectionClientThread.getLANExchangerThreads().get(i).getClientName());
                            String data = mLANConnectionClientThread.getLANExchangerThreads().get(i).getData();
                            String command = data.substring(data.indexOf(":") + 1, data.indexOf(" "));

                            if(command.equals("NAME:")) updateUI = new Intent("LAN_RECEIVEDMSG");
                            else if(command.equals("EXCHANGE_SERVER:")){
                                updateUI = new Intent("NETWORK_ERROR");
                            }
                            else break;
                            updateUI.putExtra("message", data);
                            updateUI.putExtra("address", mLANConnectionClientThread.getLANExchangerThreads().get(i).getAddress());
                            LocalBroadcastManager.getInstance(this).sendBroadcast(updateUI);
                        }
                    }
                }

                if(mLANReplyingThread != null){
                    if(mLANReplyingThread.getLANConnectionThread() != null){
                        if(mLANReplyingThread.getLANConnectionThread().getLANExchangerThreads().size() > 0){
                            for(int i = 0; i < mLANReplyingThread.getLANConnectionThread().getLANExchangerThreads().size(); i++){
                                if(mLANReplyingThread.getLANConnectionThread().getLANExchangerThreads().get(i) == null) continue;
                                Log.d("Logging", "Getting device: " + mLANReplyingThread.getLANConnectionThread().getLANExchangerThreads().get(i).getAddress());
                                String data = mLANReplyingThread.getLANConnectionThread().getLANExchangerThreads().get(i).getData();
                                String command = data.substring(data.indexOf(":") + 1, data.indexOf(" "));
                                //Hacer esto como dios manda.
                                if(command.equals("NAME:")) updateUI = new Intent("LAN_RECEIVEDMSG");
                                else if(command.equals("EXCHANGE_SERVER:")){
                                    updateUI = new Intent("NETWORK_ERROR");
                                }
                                else break;
                                updateUI.putExtra("message", data);
                                updateUI.putExtra("address", mLANReplyingThread.getLANConnectionThread().getLANExchangerThreads().get(i).getAddress());
                                LocalBroadcastManager.getInstance(this).sendBroadcast(updateUI);
                            }
                        }
                    }
                }
                break;
            case "RemoveThreadServer":
                if(mLANReplyingThread != null){
                    if(mLANReplyingThread.getLANConnectionThread() != null){
                        if(mLANReplyingThread.getLANConnectionThread().getLANExchangerThreads().size() > 0){
                            for(int i = 0; i < mLANReplyingThread.getLANConnectionThread().getLANExchangerThreads().size(); i++){
                                if(mLANReplyingThread.getLANConnectionThread().getLANExchangerThreads()
                                        .get(i).getAddress().equals(intent.getStringExtra("address"))){
                                    mLANReplyingThread.getLANConnectionThread().getLANExchangerThreads()
                                            .remove(i);
                                }
                            }
                        }
                    }
                }
                break;
            case "RemoveThreadApp":
                if(mLANConnectionClientThread != null){
                    if(mLANConnectionClientThread.getLANExchangerThreads().size() > 0){
                        for (int i = 0; i < mLANConnectionClientThread.getLANExchangerThreads().size(); i++){
                            if(mLANConnectionClientThread.getLANExchangerThreads().get(i).getClientName()
                                    .equals(intent.getStringExtra("name"))){
                                mLANConnectionClientThread.getLANExchangerThreads().remove(i);
                            }
                        }
                    }
                }
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