package com.alexander.kozminykh.game.AboutAppClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.alexander.kozminykh.game.SingleFragmentActivity;
import com.alexander.kozminykh.game.sounds.ActivitiesControl;

public class AboutAppActivity extends SingleFragmentActivity {
    public static final String TAG = "AboutAppActivity";

    @Override
    protected Fragment createFragment() {
        return new AboutAppFragment();
    }

    public static Intent newIntent(Context packegeContext){
        return new Intent(packegeContext, AboutAppActivity.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed");
        ActivitiesControl.getInstance().goToTheFirstActivity();
    }
}
