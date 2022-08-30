package com.alexander.kozminykh.game.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Александр on 08.06.2018.
 */

public class Settings {
    private static final String TAG = "Settings";

    private static final String SETTINGS = "settings";
    private static final String IS_SOUND_ON = "isSoundsOn";

    private static SharedPreferences mSettings;

    public Settings(Activity activity){
        mSettings = activity.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    }

    public static void setSound(Boolean set){
        try {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(IS_SOUND_ON, set);
            editor.apply();
        } catch (Exception e){
            Log.e(TAG, String.valueOf(e));
        }
    }

    public static boolean isSoundsOn(){
        try {
            if (mSettings.contains(IS_SOUND_ON)) {
                return mSettings.getBoolean(IS_SOUND_ON, true);
            }
        } catch (Exception e){
            Log.e(TAG, String.valueOf(e));
        }
        setSound(true);
        return true;
    }

}
