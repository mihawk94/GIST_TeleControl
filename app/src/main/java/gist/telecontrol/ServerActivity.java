package gist.telecontrol;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ServerActivity extends Activity{

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        setFonts();

        ((TextView)(findViewById(R.id.main_title))).setText(getIntent().getStringExtra("name") + ": attached devices");

        Intent i = new Intent(this, ConnectionService.class);
        i.setAction("Replying");
        i.putExtra("name", getIntent().getStringExtra("name"));
        startService(i);

        setResult(RESULT_OK);

    }

    protected void onResume(){
        super.onResume();
        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("name"), Toast.LENGTH_SHORT);
    }

    public void setFonts(){
        TextView tv=(TextView)findViewById(R.id.main_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/orange_juice_2.ttf");
        tv.setTypeface(face);
    }


}