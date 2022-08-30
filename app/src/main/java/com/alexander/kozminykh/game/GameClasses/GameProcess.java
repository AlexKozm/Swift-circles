package com.alexander.kozminykh.game.GameClasses;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.alexander.kozminykh.game.animation.AnimsControl;
import com.alexander.kozminykh.game.animation.ScaleAnimation;
import com.alexander.kozminykh.game.gameObject.AnimatingObject;
import com.alexander.kozminykh.game.gameObject.GameObject;
import com.alexander.kozminykh.game.gameObject.NotTouch;
import com.alexander.kozminykh.game.gameObject.OnDefeat;
import com.alexander.kozminykh.game.player.Player;
import com.alexander.kozminykh.game.player.PlayerLab;
import com.alexander.kozminykh.game.animation.OnEndAnimationListener;
import com.alexander.kozminykh.game.sounds.BeatBox;

import java.util.List;
import java.util.Random;

/**
 * Created by kozmi on 2/6/2018.
 */

public class GameProcess {

    private static final String TAG = "GameProcess";

    private int counter = 0;
    private long mTimeStart;
    private long mTimeStart2;
    private boolean start2 = false;
    private static PlayerLab sPlayerLab;
    private static GameProcess sGameProcess;
    private static ViewGroup containerForObject;
    private static boolean mRunProcess;
    private static boolean mLastAnimation;
    private long mTimeWait;
    private long mTimeAnimation;
    private static Activity mActivity;
    private static List<ViewGroup> mViewList;
    private static Context mContext;
    private static int mCounter; // counter of animations that have been closed
    private static Runnable mRunnable;
    private static Thread mThread;
    private static Player sPlayer;
    private AnimatingObject mAnimatingObject;
    private static AnimatingObject sLastAnimatingObject;
    private boolean mIsFirstOnDefeat = true;
    private static GameFragment mGameFragment;

    public static boolean checkGameProcess(){
        if(sGameProcess == null){
            return false;
        } else {
            return true;
        }
    }

    public static GameProcess get(
            List<ViewGroup> viewList, Context context, Player player, GameFragment gameFragment){
        if(sGameProcess == null){
            sGameProcess = new GameProcess();
            sPlayerLab = PlayerLab.get(context);//---------------------------------------------------------database
            mViewList = viewList;
            mContext = context;
            mActivity = (Activity) context;
            sPlayer = player;
            mGameFragment = gameFragment;
        }
        return sGameProcess;

    }

    private GameProcess(){

    }

    public void start(){
        setCounterAndUpdateCountView(0);
        mTimeStart = System.currentTimeMillis();
        mTimeWait = 2000;
        mTimeAnimation = 1500;
        //startAnimation(mViewList, mContext, mTimeAnimation);
        mRunProcess = true;
        mLastAnimation = false;


        mRunnable = new Runnable() {
            @Override
            public void run() {
                //Log.i(TAG, "new runnable");

                for(int i = 1; mRunProcess; i++){
                    /*Log.i(TAG, "run loop");*/
                    if(mRunProcess == true){
                        //Log.i(TAG, "Start animation "
                        //       + String.valueOf(mCounterOfAnims));
                        counter = i;
                        if (counter == 64){
                            int a = 1;
                        }
                        //GameFragment.TestText.setTestText(String.valueOf("not UI: " + counter));
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GameFragment.TestText.setTestText(String.valueOf("UI: " + counter));
                                startAnimation(mViewList, mContext, mTimeAnimation);
                            }
                        });
                    }

                    setWaitAndAnimationTime();
                    synchronized(this){
                        try {
                            wait(mTimeWait);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }


                }
                mLastAnimation = true;

            }
        };

        mThread = new Thread(mRunnable);
        mThread.start();
    }


    //set and change time of thread wait and time of animations
    private void setWaitAndAnimationTime(){
        /*if(mTimeWait > 250){
            mTimeWait = mTimeWait - 100;
        } else if(mTimeAnimation > 1500 && mCountOfAnimationsOnMaxSpeed == 20){
            mTimeAnimation = mTimeAnimation - 100;
            mCountOfAnimationsOnMaxSpeed = 0;
        } else {
            mCountOfAnimationsOnMaxSpeed++;
        }*/

        if (mTimeWait > 200) {
            float timeNow = System.currentTimeMillis() - mTimeStart;
            float timeWait = (timeNow / 3000) * 100;
            float i = 2000 - timeWait;
            mTimeWait = (long) i;
            if (mTimeWait < 200){
                mTimeWait = 200;
            }
        } else if (mTimeAnimation > 1300){
            if (!start2){
                start2 = true;
                mTimeStart2 = System.currentTimeMillis();
                //GameFragment.TestText.setTestText("Start minimaze time of animation, count: " + counter);
            }
            float timeNow1 = System.currentTimeMillis() - mTimeStart2;
            float timeWait1 = (timeNow1 / 5000) * 100;
            float i = 1500 - timeWait1;
            mTimeAnimation = (long) i;
            //GameFragment.TestText.setTestText("long time of anim: " + mTimeAnimation + " on count: " + counter
            //+ "; " + timeNow1 + "; " + timeWait1);
            if (mTimeAnimation < 1000){
                mTimeAnimation = 1000;
            }
        }

    }


    public static void cleanGameProcess(){
        mRunProcess = false;
        //Log.i("GameProcess", "mProcess = false" );
        try{
            if(sPlayer.getRecord() < mCounter) {
                sPlayer.setRecord(mCounter);
                sPlayerLab.updatePlayer(sPlayer);
                mGameFragment.setRecordViewText(String.valueOf(mCounter));
            }
        } catch(Exception e){
            Log.e(TAG, "Error in cleanGameProcess: " + e);
        }
        sGameProcess = null;

    }


    // beginning of metods for countView
    private static void setCounterAndUpdateCountView(int counter){
        //Log.i(TAG, "Set count " + String.valueOf(counter));
        mCounter = counter;
        updateCountText(String.valueOf(mCounter));
    }

    private static void updateCountText(String string){
        mGameFragment.setCountViewText(string);
    }
    // cleanAnims of metods for count view


    // beginning of get animation code
    private void startAnimation(final List<ViewGroup> viewList, final Context context, long time){
        containerForObject = getRandomContainerFromList(viewList, 9);

        for(;containerForObject.getChildAt(0) != null;){
            containerForObject = getRandomContainerFromList(viewList, 9);
            if(hasFreeContainer(viewList) == false){
                return;
            }
        }

        GameObject gameObject = getGameObject(context, containerForObject, time);
        //mAnimatingObject = new AnimatingObject(context, containerForObject, time);

        //setListeners
        if(gameObject.getClass().getName().equals(AnimatingObject.class.getName())){
            AnimatingObject gameObject1 = (AnimatingObject) gameObject;

            gameObject1.setAnimationsStop(ScaleAnimation.Do.NOTHING);
            gameObject1.setOnFirstAnimationClickListener(
                    new AnimatingObject.OnFirstAnimationClickListener() {
                        @Override
                        public void onClick() {
                            if(sGameProcess != null) {
                                mCounter++;
                                setCounterAndUpdateCountView(mCounter);
                            }
                        }
                    });
        }

        gameObject.setOnDefeatListener(new OnDefeat() {
            @Override
            public void onDefeat(String type) {
                if (mIsFirstOnDefeat && type.equals(AnimatingObject.ON_DEFEAT)){
                    BeatBox.get(context).play(BeatBox.LOSE);
                    mIsFirstOnDefeat = false;
                }
                cleanGameProcess();
                mLastAnimation = true;
                counter = 0;
                //Log.i(TAG, "onDefeat");
            }
        });

        gameObject.setOnEndAnimationListener(new OnEndAnimationListener() {
            @Override
            public void onEnd() {
                if(mLastAnimation == true){
                    for(int i = 0; mViewList.size() - 1 >= i ;i++){
                        int c = mViewList.get(i).getChildCount();
                        if(c != 0){
                            return;
                        }
                    }
                    AnimsControl.get().cleanAnims();
                    GameMenu.create(viewList, sPlayer, mActivity, mGameFragment).attachMenuViews(GameFragment.timeOfMenuOpenAnimations);
                }
            }
        });

        AnimsControl.get().add(gameObject);
        gameObject.start();

        //test
        /*BangAnimation ba = new BangAnimation(containerForObject, mActivity);
        ba.start();*/
        /*NotTouch nt = new NotTouch(containerForObject, mActivity, time);
        AnimsControl.get(mActivity).add(nt);
        nt.start();*/
    }


    private GameObject getGameObject(Context context, ViewGroup container, long time){
        Random random = new Random();
        int randomNum = random.nextInt(5) + 1;
        if((1 <= randomNum) && randomNum <= 4){
            return new AnimatingObject(context, container, time, counter);
        } else {
            return new NotTouch(container, (Activity) context, time);
        }
    }


    private ViewGroup getRandomContainerFromList(List<ViewGroup> viewsList, int randomNumFromNullTo){
        Random random = new Random();
        int randomNum = random.nextInt(randomNumFromNullTo);
        ViewGroup container = viewsList.get(randomNum);
        return container;
    }


    private boolean hasFreeContainer(List<ViewGroup> viewList){
        for(int count = 0; count <= (viewList.size() - 1); count++){
            if(viewList.get(count).getChildAt(0) == null){
                return true;
            }
        }
        return false;
    }
    //cleanAnims of get animation code

}
