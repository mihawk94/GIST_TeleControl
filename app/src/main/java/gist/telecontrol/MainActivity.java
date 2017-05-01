package gist.telecontrol;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity{

    private ButtonListener mButtonListener;
    private Button mTvButton, mPhoneButton;

    public static final int REQUEST_PHONE = 0;
    public static final int REQUEST_TV = 1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFonts();

        setButtons();

    }

    public void setFonts(){
        TextView tv=(TextView)findViewById(R.id.main_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/orange_juice_2.ttf");
        tv.setTypeface(face);
    }

    public void setButtons(){

        mButtonListener = new ButtonListener(this);

        mTvButton = (Button)findViewById(R.id.tv_btn);
        mPhoneButton = (Button)findViewById(R.id.phone_btn);

        mTvButton.setOnClickListener(mButtonListener);
        mPhoneButton.setOnClickListener(mButtonListener);

    }


    protected void onResume(){
        super.onResume();
    }

    protected void onPause(){
        super.onPause();
    }

}
