package com.alexander.kozminykh.game.animation;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.alexander.kozminykh.game.GameClasses.GameFragment;

import java.util.List;


/**
 * Created by kozmi on 2/16/2018.
 */

public class ScaleAnimation extends Thread implements Animation {

    private static final String TAG = "ScaleAnimation";

    public static class Do{
        public static final String SET_SCALE = "setScale";
        public static final String SET_LAYOUT = "setLayout";
        public static final String NOTHING = "nothing";
        public static final String SET_SCALE_TO_ONE_AND_SET_LAYOUT = "setScaleToOneAndSetLayout";
    }

    public static class Type{
        /**
         * {@link ScaleAnimation} has two types of animation:
         *
         * 1.Firstly we animate view by scaling and after we set layout params of view.
         *
         * 2.We set layout of view to size, that will be at the cleanAnims of animation,
         *   then we set starting size by set scale,
         *   and we begin to animate view by scaling.
         */
        public static final String FIRST = "first_type";
        public static final String SECOND = "second_type";
    }

    private Activity mActivity;
    private View mView;
    private float mSize;
    private float mStartSize;
    private float mEndSize;
    private float mScale;
    private float coef;              // mStartSize / mEndSize
    private long mDuration;
    private long mCountDownInterval;
    private boolean mIsStop;
    private float mNum;
    private int tickListenerCount = 0;
    private int tickListenerCount1 = 0;
    private int startSizeOfAfterTime;

    private String mWhatDoOnEnd = Do.SET_SCALE_TO_ONE_AND_SET_LAYOUT;
    private String mWhatDoOnStop = Do.SET_SCALE_TO_ONE_AND_SET_LAYOUT;
    private String mSelectedType = Type.FIRST;
    private boolean mUseLayoutAndViewScale = true;

    private long mStartTime;
    private long mCurrentTime;
    private long mMillisUntilFinished;

    private OnEndAnimationListener mOnAnimationEnd;
    private List<Long> mAfterTime;
    private OnTickListener mOnTickListener;

    /**
     * Listener that called after the cleanAnims and the stop of animation.
     */
    /*public interface OnEndAnimationListener {
        void onDefeat();
    }*/

    @Override
    public void setOnEndAnimationListener(OnEndAnimationListener listener){
        mOnAnimationEnd = listener;
    }


    public interface OnTickListener{
        void onTick(float after, int count);
    }
    /**
     *
     * @param listener
     * @param afterTime
     */
    public void setOnTickListener(OnTickListener listener,@NonNull List<Long> afterTime){
        mOnTickListener = listener;
        mAfterTime = afterTime;
    }

    private void mAfterTimeCorrect(){
        List<Long> list;
        list = mAfterTime;
        mAfterTime.clear();
        mAfterTime.add(list.get(0));
        for(int i = 0; list.size() - 1 >= i ;i++){
            long num = list.get(i);
            if(num < mAfterTime.get(0)){
                mAfterTime.add(0, num);
            } else {
                boolean stop = false;
                for(int i1 = 0; (mAfterTime.size() - 1 >= i1) && !stop;i1++){
                    long num1 = mAfterTime.get(i1);
                    if(num >= num1){
                        mAfterTime.add(i1 + 1, num);
                        stop = true;
                    }
                }
            }
        }
    }

    /**
     * @param view that will animate.
     * @param startSize relative.
     * @param endSize relative.
     * @param duration of animation.
     * @param activity
     */
    public ScaleAnimation(View view,
                          final float startSize,
                          final float endSize,
                          final long duration,
                          Activity activity) {
        super();

        mActivity = activity;
        mView = view;
        mIsStop = false;
        mStartSize = startSize;
        mEndSize = endSize;
        mDuration = duration; //1000
        mCountDownInterval = 1;
        mSize = 1;
        coef = 1;


    }


    /**
     * 1.Animate by scaling.
     * 2.Change layout size.
     */
    private void run1(){
        mNum = mEndSize / mStartSize;
        mNum = mNum - 1;
        mNum = mNum / mDuration;

        mStartTime = System.currentTimeMillis();
        mCurrentTime = mStartTime;
        mMillisUntilFinished = mDuration - (mCurrentTime - mStartTime);

        for(; mMillisUntilFinished >= 0;){
            synchronized(this){
                try {
                    wait(mCountDownInterval);

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            if(mIsStop == true) {
                /*Log.i(TAG, "onStop, set scale " + String.valueOf(mSize));*/
                setsOnStop(mView, 1, mSize);
                //setViewScale(mView, 1);
                //setLayoutScale(mView, mSize);
                if(mOnAnimationEnd != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mOnAnimationEnd.onEnd();
                            }catch(Exception e){}
                        }
                    });
                }
                return;
            }

            //test
            long t = mMillisUntilFinished;
            //test

            mCurrentTime = System.currentTimeMillis();
            mMillisUntilFinished = mDuration - (mCurrentTime - mStartTime);

            try {
                if(((tickListenerCount < startSizeOfAfterTime) &&
                        (mCurrentTime - mStartTime) >= mAfterTime.get(0))) {
                    tickListenerCount++;
                    mAfterTime.remove(0);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tickListenerCount1++;
                                mOnTickListener.onTick(mCurrentTime - mStartTime,
                                        tickListenerCount1);
                            }catch(Exception e){}
                        }
                    });
                }
            }catch(Exception e){}

            //test
            t = t - mMillisUntilFinished;
            if(t > 15) {
                Log.i(TAG, "Time between updates of animations:" + t);
                Log.i(TAG, "onTick millis until finished "
                        + String.valueOf(mMillisUntilFinished));
            }
            //test

            float num = mDuration - mMillisUntilFinished;
            mSize = mNum * num;
            mSize++;

            mScale = mSize;

            //test
            //Log.i(TAG, "size :" + String.valueOf(mSize));
            //Log.i(TAG, "size: " + String.valueOf(mSize * coef));
            //test

            setViewScale(mView, mSize);
        }

        /*Log.i(TAG, "onStop, set scale " + String.valueOf(mSize));*/
        setsOnEnd(mView, 1, mSize);
        //setViewScale(mView, 1);
        //setLayoutScale(mView, mSize);
        if(mOnAnimationEnd != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mOnAnimationEnd.onEnd();
                    }catch(Exception e){}
                }
            });
        }
    }

    /**
     * 1.Change layout size.
     * 2.Change scale.
     * 3.Animate by scaling.
     */
    private void run2(){

        mNum = mStartSize / mEndSize;

        if(mUseLayoutAndViewScale == true) {
            setLayoutAndViewScale(mView, mStartSize / mEndSize, mEndSize / mStartSize);
        }

        mNum = 1 - mNum;
        mNum = mNum / mDuration;

        mStartTime = System.currentTimeMillis();
        mCurrentTime = mStartTime;
        long time = (mCurrentTime - mStartTime);

        for(; time < mDuration;){
            synchronized(this){
                try {
                    wait(mCountDownInterval);

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }


            if(mIsStop == true) {
                /*Log.i(TAG, "onStop, set scale " + String.valueOf(mSize));*/
                setsOnStop(mView, 1, mSize);
                mCurrentTime = System.currentTimeMillis();
                mMillisUntilFinished = mDuration - (mCurrentTime - mStartTime);
                if(mOnAnimationEnd != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mOnAnimationEnd.onEnd();
                            }catch(Exception e){}
                        }
                    });
                }

                return;
            }

            mCurrentTime = System.currentTimeMillis();
            time = (mCurrentTime - mStartTime);

            try {
                if((tickListenerCount < startSizeOfAfterTime) && (time >= mAfterTime.get(0))) {
                    tickListenerCount++;
                    mAfterTime.remove(0);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tickListenerCount1++;
                                mOnTickListener.onTick(mCurrentTime - mStartTime,
                                        tickListenerCount1);
                            } catch(Exception e){}
                        }
                    });
                }
            }catch(Exception e){}

            float num = mNum * time;
            mSize = num + (mStartSize / mEndSize);

            mScale = mSize;

            //test
            //Log.i(TAG, "size :" + String.valueOf(mSize));
            //Log.i(TAG, "run2, size: " + String.valueOf(mSize * coef));
            //test

            setViewScale(mView, mSize);
        }

        /*Log.i(TAG, "onStop, set scale " + String.valueOf(mSize));*/
        setsOnEnd(mView, 1, mSize);
        //setViewScale(mView, 1);
        //setLayoutScale(mView, mSize);
        if(mOnAnimationEnd != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mOnAnimationEnd.onEnd();
                    }catch(Exception e){}
                }
            });
        }
    }


    /**
     * Select which of run: {@link ScaleAnimation#run1()} or {@link ScaleAnimation#run2()}
     * use by checking {@link ScaleAnimation#mSelectedType}.
     */
    @Override
    public void run() {
        super.run();
        if(mAfterTime != null){
            startSizeOfAfterTime = mAfterTime.size();
        }
        try {
            if(mSelectedType.equals(Type.SECOND)){
                run2();
            } else {
                run1();
            }
        } catch(final Exception e){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GameFragment.TestText.setTestText("error in anim: " + e);
                }
            });
        }
    }

    /* formula of animation in {@link ScaleAnimation#run1()}
    *  ((  mEndSize     )                                                 )
    *  (( ---------- - 1) / mDuration * ( mDuration - millisUntilFinished ) )+ 1
    *  (( mStartSize    )                                                 )
    */

    /**
     * Set type of animation.
     * @param constant string of class {@link Type}.
     */
    public void setTypeOfAnimation(@NonNull String constant) {
        mSelectedType = constant;
    }
    

    /**
     * Set value of {@link ScaleAnimation#mWhatDoOnEnd}
     * to do something with view after the cleanAnims of scaling.
     *
     * @param constant of class {@link Do}.
     */
    public void setWhatDoOnEnd(@NonNull String constant){
        mWhatDoOnEnd = constant;
    }

    /**
     * Private method, that use variable {@link ScaleAnimation#mWhatDoOnEnd} in this code,
     * to make a choise what to do at the cleanAnims of animation.
     * @param view that will be animated.
     * @param scale uses for set scale when it necessary.
     * @param size in px uses for set layout size when it necessary.
     */
    private void setsOnEnd(final View view, final float scale, final float size){

        if(mWhatDoOnEnd.equals(Do.SET_LAYOUT)){
            setLayoutScale(view, scale);
        } else if(mWhatDoOnEnd.equals(Do.SET_SCALE)) {
            setViewScale(view, scale);
        } else if(mWhatDoOnEnd.equals(Do.NOTHING)){

        } else {
            setLayoutAndViewScale(view, 1, size);
        }
    }


    /**
     * Set value of {@link ScaleAnimation#mWhatDoOnStop}
     * to do something with view after stopping of scaling.
     *
     * @param constant of class {@link Do}.
     */
    public void setWhatDoOnStop(@NonNull String constant){
        mWhatDoOnStop = constant;
    }

    /**
     * Private method, that use variable {@link ScaleAnimation#mWhatDoOnStop} in this code,
     * to make a choise what to do after stopping of animation.
     *
     * @param view that will be animated.
     * @param scale uses for set scale when it necessary.
     * @param size in px uses for set layout size when it necessary.
     */
    private void setsOnStop(final View view, final float scale, final float size){

        if(mWhatDoOnStop.equals(Do.SET_LAYOUT)){
            setLayoutScale(view, scale);
        } else if(mWhatDoOnStop.equals(Do.SET_SCALE)){
            setViewScale(view, scale);
            coef = 0.003f;//mStartSize / mEndSize;
            //test
            Log.i(TAG, "coef: " + String.valueOf(coef));
        } else {
            setLayoutAndViewScale(view, 1, size);
        }
    }

    /**
     * Stop(cleanAnims) animation.
     */
    @Override
    public void end() {
        mIsStop = true;
    }

    /**
     * Start animation
     */
    @Override
    public void start(){
        super.start();
    }


    private void setViewScale(final View view, final float scale){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        });
    }

    private void setLayoutScale(final View view, final float size){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int height = view.getHeight();
                int sizePx = (int) ((float) height * (float) size);

                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = sizePx;
                layoutParams.width = sizePx;
                view.setLayoutParams(layoutParams);
            }
        });
    }

    private void setLayoutAndViewScale(final View view, final float scale, final float size){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //set view scale
                view.setScaleX(scale);
                view.setScaleY(scale);

                //set layuot scale
                int height = view.getHeight();
                int sizePx = (int) ((float) height * size);

                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = sizePx;
                layoutParams.width = sizePx;
                view.setLayoutParams(layoutParams);
            }
        });
    }


    public float getScale() {
        return mScale;
    }


    /**
     * Uses only in the second type of animation.
     * @param use set, will layout changed at the beginning
     *            or animation begin immediately.
     *            You can use it with the second type of animation
     *            when you don't want to change layout in animation.
     */
    public void setLayoutAndViewScale(boolean use){
        mUseLayoutAndViewScale = use;
    }
}
