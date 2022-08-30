package com.alexander.kozminykh.game.gameObject;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alexander.kozminykh.game.GameClasses.GameFragment;
import com.alexander.kozminykh.game.R;
import com.alexander.kozminykh.game.animation.Animation;
import com.alexander.kozminykh.game.animation.BangAnimation;
import com.alexander.kozminykh.game.animation.OnEndAnimationListener;
import com.alexander.kozminykh.game.animation.ScaleAnimation;
import com.alexander.kozminykh.game.convertations.Convert;
import com.alexander.kozminykh.game.sounds.BeatBox;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kozmi on 12/30/2017.
 */
public class AnimatingObject implements GameObject {
    private static final String TAG = "AnimatingObject";

    public static final String ON_DEFEAT = "AnimatingObject.defeat";
    public static final String ON_DEFEAT_BANG = "AnimatingObject.defeat.bang";

    //test
    int counter;

    private View animatingObject;
    private View firstRound;
    private View secondRound;
    private boolean itWasClick = false;
    private Context mContext;
    private ScaleAnimation mFirstAnimationTest;
    private OnFirstAnimationClickListener mOnFirstAnimationClickListener;
    private OnDefeat mOnDefeat;
    private OnEndAnimationListener mOnAnimationEnd;
    private boolean wasClick = false;

    private ScaleAnimation mIncreasingAnimation;
    private ScaleAnimation mDecreasingAnimation;
    private ScaleAnimation mFirstAnimation;
    private BangAnimation mBangAnimation;


    public static float getContainerHeight(Context context, ViewGroup container){
        //finding size of padding is unfinished
        float padding = 4;
        float height = container.getHeight();
        padding = Convert.dpToPixel(padding, context);
        padding = padding * 2;
        height = height - padding;

        //height = convertPixelsToDp(height, context);

        return height;
    }

    @Override
    public void end() {
        List<Animation> anims = new ArrayList<>();
        anims.add(mIncreasingAnimation);
        anims.add(mDecreasingAnimation);
        anims.add(mFirstAnimation);
        anims.add(mBangAnimation);

        for (int i = 0; i < anims.size(); i++){
            try{
                Animation anim = anims.get(i);
                anim.setOnEndAnimationListener(null);
                anim.end();
            }catch(Exception e){

            }
        }
    }

    public void setAnimationsStop(String s){

        mIncreasingAnimation.setWhatDoOnStop(s);
        //mDecreasingAnimation.setWhatDoOnStop(s);
        mFirstAnimation.setWhatDoOnStop(s);
    }

    //begin of constructors ||
    //                      \/

    public AnimatingObject(Context context, @NonNull ViewGroup container){
        long firstAnimationTime = 2000;
        long delayTime = 0;
        long secondAnimationTime = 2000;
        float size;

        size = getContainerHeight(context, container) - 2;

        setAnimation(
                context, container, firstAnimationTime, delayTime, secondAnimationTime, size);
    }

    public AnimatingObject(Context context, @NonNull ViewGroup container, long allTime, int i){
        long firstAnimationTime = (allTime / 3) * 2;
        long delayTime = 0;
        long secondAnimationTime = (allTime / 3);
        float size; // dp

        //test
        counter = i;
        if (i == 64){
            GameFragment.TestText.setTestText("Circle 64, first time: "
                    + firstAnimationTime + "; second time: " + secondAnimationTime);
        }

        size = getContainerHeight(context, container);

        setAnimation(
                context, container, firstAnimationTime, delayTime, secondAnimationTime, size);
    }

    public AnimatingObject(Context context, @NonNull ViewGroup container, long allTime, float fsize){
        long firstAnimationTime = allTime / 2;
        long delayTime = 0;
        long secondAnimationTime = allTime / 2;
        float size = fsize; // dp


        setAnimation(
                context, container, firstAnimationTime, delayTime, secondAnimationTime, size);
    }

    //                    /\
    //cleanAnims of constructors ||


    private void addAndFindObject(Context context, ViewGroup container){

        animatingObject = View.inflate(context, R.layout.animating_object, container);
        //or
        //View.inflate(context, R.layout.animating_object, container);

        firstRound = animatingObject.findViewById(R.id.animating_object);
        secondRound = animatingObject.findViewById(R.id.animating_object1);
        //or
        //firstRound = container.findViewById(R.id.animating_object);
        //secondRound = container.findViewById(R.id.animating_object1);
        //animatingContainer = container.findViewById(R.id.animating_container);

    }

    //test-------------------------------------------------------------------------------------------

    // get animation of this class
    @Override
    public void start(){
        mFirstAnimationTest.start();
    }


    private void setAnimation(final Context context,
                              final ViewGroup container,
                              final long firstAnimationTime,
                              final long delayTime,
                              final long secondAnimationTime,
                              final float size){
        mContext = context;
        addAndFindObject(context, container);

        //set parameters for all animations
        mIncreasingAnimation = increasingAnimation(secondAnimationTime, size);
        //mDecreasingAnimation = decreasingAnimation(secondAnimationTime, size);
        mFirstAnimation = firstAnimation(firstAnimationTime, size);

        mFirstAnimationTest = mFirstAnimation;

        //add listeners for all animations
        mFirstAnimation.
                setOnEndAnimationListener(new OnEndAnimationListener() {
            @Override
            public void onEnd() {
                firstRound.setOnTouchListener(null);
                if(!itWasClick) {
                    secondRound.setAlpha(1);
                    mOnDefeat.onDefeat(ON_DEFEAT);

                    //test
                    GameFragment.TestText.setTestText("defeat: " + String.valueOf(counter
                    ));

                    //BeatBox.get(mContext).play(BeatBox.LOSE);
                    mIncreasingAnimation.start();
                } else {
                    firstRound.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN){
                                mOnDefeat.onDefeat(ON_DEFEAT_BANG);
                                v.setOnTouchListener(null);
                                ((FrameLayout)v.getParent()).removeView(v);
                                end();
                                mBangAnimation = new BangAnimation(container, (Activity)mContext);
                                mBangAnimation.setOnEndAnimationListener(new OnEndAnimationListener() {
                                    @Override
                                    public void onEnd() {
                                        mOnAnimationEnd.onEnd();
                                    }
                                });
                                mBangAnimation.start();
                            }
                            return false;
                        }
                    });
                }
            }
        });


        firstRound.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            if(!itWasClick) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    itWasClick = true;
                    firstRound.setBackgroundResource(R.drawable.oval_red);

                    mFirstAnimation.setWhatDoOnStop(ScaleAnimation.Do.SET_SCALE);
                    mFirstAnimation.end();
                    float scale = mFirstAnimation.getScale();

                    mDecreasingAnimation = decreasingAnimation(secondAnimationTime, scale);
                    mDecreasingAnimation.
                            setOnEndAnimationListener(new OnEndAnimationListener() {
                                @Override
                                public void onEnd() {
                                    /*mOnAnimationEnd.onDefeat();*/
                                    container.removeAllViews();
                                    mOnAnimationEnd.onEnd();

                                    /*container.removeAllViews();*/
                                }
                            });
                    mDecreasingAnimation.setWhatDoOnStop(ScaleAnimation.Do.NOTHING);
                    mDecreasingAnimation.setLayoutAndViewScale(false);

                    mOnFirstAnimationClickListener.onClick();

                    BeatBox.get(mContext).playBulk();

                    mDecreasingAnimation.start();
                }
            }
            return false;
            }
        });

        mIncreasingAnimation.
                setOnEndAnimationListener(new OnEndAnimationListener() {
            @Override
            public void onEnd() {
                /*mOnAnimationEnd.onDefeat();*/
                container.removeAllViews();
                mOnAnimationEnd.onEnd();
                /*container.removeAllViews();*/
            }
        });

//        mDecreasingAnimation.
//                setOnEndAnimationListener(new ScaleAnimation.OnEndAnimationListener() {
//            @Override
//            public void onDefeat() {
//                mOnAnimationEnd.onDefeat();
//                Activity a = (Activity)mContext;
//                a.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        container.removeAllViews();
//                        mOnAnimationEnd.onDefeat();
//                    }
//                });
//                container.removeAllViews();
//            }
//        });
        //mFirstAnimation.get();
    }


    //the beginning of interfaces for class that use this animations
    @Override
    public void setOnDefeatListener(OnDefeat onDefeat){
        mOnDefeat = onDefeat;
    }


    public interface OnFirstAnimationClickListener{
        void onClick();
    }

    public void setOnFirstAnimationClickListener(
            OnFirstAnimationClickListener onFirstAnimationClickListener) {

        mOnFirstAnimationClickListener = onFirstAnimationClickListener;
    }

    @Override
    public void setOnEndAnimationListener(OnEndAnimationListener onAnimationEnd){
        mOnAnimationEnd = onAnimationEnd;
    }
    //cleanAnims of interfaces


    private ScaleAnimation firstAnimation(long time, float size){

        firstRound.setAlpha(1f);

        ScaleAnimation firstAnimation = new ScaleAnimation(firstRound,
                        1,
                        size,
                        time,
                        (Activity) mContext );
        firstAnimation.setWhatDoOnEnd(ScaleAnimation.Do.NOTHING);

        return firstAnimation;

    }

    private ScaleAnimation increasingAnimation(long time, float size){
        time = time - 1;

        ScaleAnimation increasingAnimation = new ScaleAnimation(secondRound,
                1,
                size,
                time,
                (Activity) mContext);

        return increasingAnimation;
    }

    private ScaleAnimation decreasingAnimation(long time, float size){
        ScaleAnimation decreasingAnimation = new ScaleAnimation(firstRound,
                size,
                1,
                time,
                (Activity) mContext);
        decreasingAnimation.setTypeOfAnimation(ScaleAnimation.Type.SECOND);

        return decreasingAnimation;
    }


}
