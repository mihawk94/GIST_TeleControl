package gist.telecontrol;

import android.app.Activity;
import android.os.Bundle;

public class SearchActivity extends Activity {

    /*
    private Handler handler = new Handler();
    int count = 0;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    */

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        

        /*
        TextView lanDevicesSearch = ((TextView)findViewById(R.id.lan_devices_text));
        LinearLayout lanDevicesView = ((LinearLayout)findViewById(R.id.lan_devices));
        Thread lanDevicesThread = new Thread(new Networking(handler, lanDevicesSearch, lanDevicesView));
        lanDevicesThread.start();
        */
    }

    /*
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    public void setupAccelerometer(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent sensor){
    }
    /*
    /*
    private Runnable broadcasting = new Runnable(){
        @Override
        public void run() {
            count++;
            ((TextView)findViewById(R.id.info)).setText("" + count);
            handler.postDelayed(broadcasting, 500);
        }
    };
    */

}