package gist.telecontrol;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class ButtonListener implements View.OnClickListener{

    Activity mActivity;

    public ButtonListener(Activity activity){
        mActivity = activity;
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_btn:
                //ServerActivity
                break;
            case R.id.phone_btn:
                Intent i = new Intent(mActivity, SearchActivity.class);
                mActivity.startActivityForResult(i, ((MainActivity)mActivity).REQUEST_PHONE);
                break;
            default:
                break;
        }
    }
}