package com.alexander.kozminykh.game.gameObject;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alexander.kozminykh.game.animation.Animation;
import com.alexander.kozminykh.game.animation.ScaleAnimation;
import com.alexander.kozminykh.game.convertations.Convert;
import com.alexander.kozminykh.game.R;
import com.alexander.kozminykh.game.animation.BangAnimation;
import com.alexander.kozminykh.game.animation.OnEndAnimationListener;

import java.util.ArrayList;
import java.util.List;

public class NotTouch implements GameObject {
    public static final String ON_DEFEAT = "GameObject.onDefeat";

    private Activity mActivity;
    private ViewGroup mContainer;
    private long mTime;

    private View mAnimatingObject;
    private View mCircle;

    private ScaleAnimation mFirstAnim;
    private ScaleAnimation mSecondAnim;
    private BangAnimation mBangAnimation;

    private OnEndAnimationListener mOnEndAnimation;
    private OnDefeat mOnDefeat;


    public NotTouch(ViewGroup container, Activity activity, long time){
        mActivity = activity;
        mContainer = container;
        mTime = time;

        mAnimatingObject = View.inflate(activity, R.layout.not_touch, container);
        mCircle = mAnimatingObject.findViewById(R.id.not_touch);
        //mCircle.setLayoutParams(getLayoutWithSize(1, Gravity.CENTER));
        mFirstAnim = firstAnim(time / 2, getContainerHeight(activity, mContainer));
        setOnClick();
    }


    private void setOnClick(){
     mCircle.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
             if(event.getAction() == MotionEvent.ACTION_DOWN){
                 v.setOnTouchListener(null);
                 try {
                     mOnDefeat.onDefeat(ON_DEFEAT);
                 }catch(Exception e){}
                 ((FrameLayout)v.getParent()).removeView(v);
                 end();
                 mBangAnimation = new BangAnimation(mContainer, mActivity);
                 mBangAnimation.setOnEndAnimationListener(new OnEndAnimationListener() {
                     @Override
                     public void onEnd() {
                         try {
                             mOnEndAnimation.onEnd();
                         }catch(Exception e){}
                     }
                 });
                 mBangAnimation.start();
             }
             return false;
         }
     });
    }


    private ScaleAnimation firstAnim(final long time, final float size){
        mCircle.setAlpha(1f);

        ScaleAnimation firstAnimation = new ScaleAnimation(mCircle,
                1,
                size,
                time,
                mActivity );
        firstAnimation.setWhatDoOnEnd(ScaleAnimation.Do.NOTHING);
        firstAnimation.setOnEndAnimationListener(new OnEndAnimationListener() {
            @Override
            public void onEnd() {
                mSecondAnim = secondAnim(time, size);
                mSecondAnim.start();
            }
        });

        return firstAnimation;
    }

    private ScaleAnimation secondAnim(long time, float size){
        ScaleAnimation secondAnimation = new ScaleAnimation(mCircle,
                size,
                1,
                time,
                mActivity );
        secondAnimation.setTypeOfAnimation(ScaleAnimation.Type.SECOND);
        secondAnimation.setLayoutAndViewScale(false);
        secondAnimation.setWhatDoOnEnd(ScaleAnimation.Do.NOTHING);
        secondAnimation.setOnEndAnimationListener(new OnEndAnimationListener() {
            @Override
            public void onEnd() {
                end();
                mContainer.removeAllViews();
                try{
                    mOnEndAnimation.onEnd();
                }catch(Exception e){}
            }
        });
        return secondAnimation;
    }


    @Override
    public void end() {
        List<Animation> anims = new ArrayList<>();
        anims.add(mFirstAnim);
        anims.add(mSecondAnim);
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

    @Override
    public void start() {
        mFirstAnim.start();
    }

    @Override
    public void setOnEndAnimationListener(OnEndAnimationListener listener) {
        mOnEndAnimation = listener;
    }

    private int getContainerHeight(Context context, ViewGroup container){
        //finding size of padding is unfinished
        float padding = 4;
        float height = container.getHeight();
        padding = Convert.dpToPixel(padding, context);
        padding = padding * 2;
        height = height - padding;
        //height = convertPixelsToDp(height, context);
        return (int) height;
    }

    private FrameLayout.LayoutParams getLayoutWithSize(int sizeInPx, int gravity){
        //int size2 = getSize(sizeInDp);
        FrameLayout.LayoutParams viewParams = new FrameLayout.LayoutParams(sizeInPx, sizeInPx);
        viewParams.gravity = gravity;
        return viewParams;
    }

    @Override
    public void setOnDefeatListener(OnDefeat onDefeat) {
        mOnDefeat = onDefeat;
    }
}
