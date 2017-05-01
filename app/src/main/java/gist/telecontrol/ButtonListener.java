package gist.telecontrol;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ButtonListener implements View.OnClickListener{

    Activity mActivity;

    public ButtonListener(Activity activity){
        mActivity = activity;
    }

    public void onClick(View v) {

        Intent i;

        switch(v.getId()){
            case R.id.tv_btn:

                if(((EditText)(mActivity.findViewById(R.id.tv_name))).getText().toString().equals("") ||
                        ((EditText)(mActivity.findViewById(R.id.tv_name))).getText().toString() == null){
                    Toast.makeText(mActivity.getApplicationContext(), "Insert your device name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(((EditText)(mActivity.findViewById(R.id.tv_name))).getText().toString().toCharArray().length > 16){
                    Toast.makeText(mActivity.getApplicationContext(), "Maximum size of name: 16 symbols", Toast.LENGTH_SHORT).show();
                    return;
                }
                i = new Intent(mActivity, ServerActivity.class);
                i.putExtra("name", ((EditText)(mActivity.findViewById(R.id.tv_name))).getText().toString());
                mActivity.startActivityForResult(i, ((MainActivity)mActivity).REQUEST_TV);
                break;


            case R.id.phone_btn:
                if(((EditText)(mActivity.findViewById(R.id.phone_name))).getText().toString().equals("") ||
                        ((EditText)(mActivity.findViewById(R.id.phone_name))).getText().toString() == null){
                    Toast.makeText(mActivity.getApplicationContext(), "Insert your device name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(((EditText)(mActivity.findViewById(R.id.phone_name))).getText().toString().toCharArray().length > 16){
                    Toast.makeText(mActivity.getApplicationContext(), "Maximum size of name: 16 symbols", Toast.LENGTH_SHORT).show();
                    return;
                }
                i = new Intent(mActivity, SearchActivity.class);
                i.putExtra("name", ((EditText)(mActivity.findViewById(R.id.phone_name))).getText().toString());
                mActivity.startActivityForResult(i, ((MainActivity)mActivity).REQUEST_PHONE);
                break;


            default:
                break;
        }
    }
}