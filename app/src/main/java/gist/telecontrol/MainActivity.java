package gist.telecontrol;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    public void onSensorChanged(SensorEvent sensor){
        Sensor lastSensor = sensor.sensor;

        if(lastSensor.getType() == Sensor.TYPE_ACCELEROMETER){
            ((TextView)findViewById(R.id.accelxvalue)).setText("" + sensor.values[0]);
            ((TextView)findViewById(R.id.accelyvalue)).setText("" + sensor.values[1]);
            ((TextView)findViewById(R.id.accelzvalue)).setText("" + sensor.values[2]);
        }
    }
}
