package com.alexander.kozminykh.game.GameClasses;

import android.support.v4.app.Fragment;

import com.alexander.kozminykh.game.SingleFragmentActivity;

/**
 * Created by kozmi on 12/30/2017.
 */

public class GameActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new GameFragment();
    }
}
