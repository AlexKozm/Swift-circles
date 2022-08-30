package com.alexander.kozminykh.game.sounds;

import android.content.Context;

/**
 * Created by Александр on 10.06.2018.
 */

public class ActivitiesControl {
    private boolean mIsFirstActivityRun = false;
    private boolean mIsSecondActivityRun = false;
    private boolean mIsServiseRun = false;
    private boolean mIsGoingFromOneToAnother = false;

    private static final ActivitiesControl ourInstance = new ActivitiesControl();

    public static ActivitiesControl getInstance() {
        return ourInstance;
    }

    private ActivitiesControl() {
    }

    public void firstActivityResume(Context context){
        work1(context);
        mIsFirstActivityRun = true;
    }

    public void firstActyvityPause(Context context){
        mIsFirstActivityRun = false;
        if (!mIsGoingFromOneToAnother){
            BeatBox.get(context).endBackgroundMusic(context);
            mIsServiseRun = false;
        }
        mIsGoingFromOneToAnother = false;
    }

    public void goToTheSecondAcivity(){
        mIsFirstActivityRun = false;
        mIsSecondActivityRun = true;
        mIsGoingFromOneToAnother = true;
    }

    public void goToTheFirstActivity(){
        mIsFirstActivityRun = true;
        mIsSecondActivityRun = false;
        mIsGoingFromOneToAnother = true;
    }

    public void secondActivityResume(Context context){
        work1(context);
        mIsSecondActivityRun = true;
    }

    public void secondActivityPause(Context context){
        mIsSecondActivityRun = false;
        if (!mIsGoingFromOneToAnother){
            BeatBox.get(context).endBackgroundMusic(context);
            mIsServiseRun = false;
        }
        mIsGoingFromOneToAnother = false;
    }

    private void work1(Context context){
        if (!mIsFirstActivityRun && !mIsSecondActivityRun && !mIsServiseRun){
            BeatBox.get(context).playBackground(context);
        }
    }

    public void setServiseRun(boolean b){
        mIsServiseRun = b;
    }
}
