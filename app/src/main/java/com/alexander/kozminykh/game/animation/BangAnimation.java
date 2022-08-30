package com.alexander.kozminykh.game.animation;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alexander.kozminykh.game.convertations.Convert;
import com.alexander.kozminykh.game.R;
import com.alexander.kozminykh.game.sounds.BeatBox;

import java.util.ArrayList;
import java.util.List;

public class BangAnimation implements Animation {
    private Activity mActivity;
    private ViewGroup mContainer;

    private View mAnimatingObject;
    private View mSmoke;
    private View mRound1;
    private View mRound2;
    private View mRound3;
    private View mRound4;
    private View mRound5;

    private ScaleAnimation mSmokeAnim;
    private ScaleAnimation mRound1Anim;
    private ScaleAnimation mRound2Anim;
    private ScaleAnimation mRound3Anim;
    private ScaleAnimation mRound4Anim;
    private ScaleAnimation mRound5Anim;

    private OnEndAnimationListener mOnAnimationEnd;


    public BangAnimation(ViewGroup container, Activity activity) {
        mContainer = container;
        mActivity = activity;

        mAnimatingObject = View.inflate(activity, R.layout.bang, container);
        mSmoke = mAnimatingObject.findViewById(R.id.bang_first);
        mRound1 = mAnimatingObject.findViewById(R.id.bang_second);
        mRound2 = mAnimatingObject.findViewById(R.id.bang_third);
        mRound3 = mAnimatingObject.findViewById(R.id.bang_forth);
        mRound4 = mAnimatingObject.findViewById(R.id.bang_fifth);
        mRound5 = mAnimatingObject.findViewById(R.id.bang_sixth);

        setRealSizes();
        setAnimations();
    }


    @Override
    public void end() {
        List<ScaleAnimation> anims = new ArrayList<>();
        anims.add(mSmokeAnim);
        anims.add(mRound1Anim);
        anims.add(mRound2Anim);
        anims.add(mRound3Anim);
        anims.add(mRound4Anim);
        anims.add(mRound5Anim);
        for (int i = 0; i < anims.size(); i++){
            try{
                ScaleAnimation anim = anims.get(i);
                anim.setOnEndAnimationListener(null);
                anim.end();
            }catch(Exception e){

            }
        }
    }

    @Override
    public void start() {
        mSmokeAnim.start();
        BeatBox.get(mActivity).play(BeatBox.BANG);
    }

    @Override
    public void setOnEndAnimationListener(OnEndAnimationListener listener) {
        mOnAnimationEnd = listener;
    }


    private void setRealSizes(){
        mSmoke.setLayoutParams(getLayoutWithSize(
                getRealSize(getContainerHeight(mActivity, mContainer), 300),
                Gravity.CENTER)); //test 105
        mRound1.setLayoutParams(getLayoutWithSize(
                getRealSize(getContainerHeight(mActivity, mContainer), 120),
                Gravity.CENTER)); // 42
        mRound2.setLayoutParams(getLayoutWithSize(
                getRealSize(getContainerHeight(mActivity, mContainer), 150),
                Gravity.START)); // 53
        mRound3.setLayoutParams(getLayoutWithSize(
                getRealSize(getContainerHeight(mActivity, mContainer), 180),
                Gravity.END)); // 63
        mRound4.setLayoutParams(getLayoutWithSize(
                getRealSize(getContainerHeight(mActivity, mContainer), 190),
                Gravity.END | Gravity.BOTTOM)); // 67
        mRound5.setLayoutParams(getLayoutWithSize(
                getRealSize(getContainerHeight(mActivity, mContainer), 200),
                Gravity.START | Gravity.BOTTOM)); // 70
    }


    private void setAnimations(){
        //mSmokeAnim = smokeAnimation(300);
        //mRound1Anim = round1Animation(90);
        mRound5Anim = animation(200,
                mRound5,
                100,
                null,
                50);
        mRound4Anim = animation(190,
                mRound4,
                100,
                mRound5Anim,
                50L);
        mRound3Anim = animation(180,
                mRound3,
                100,
                mRound4Anim,
                50L);
        mRound2Anim = animation(150,
                mRound2,
                100,
                mRound3Anim,
                50L);
        mRound1Anim = animation(90,
                mRound1,
                100,
                mRound2Anim,
                50L);
        mSmokeAnim = animation(300,
                mSmoke,
                100,
                mRound1Anim,
                50L);
        mSmokeAnim.setOnEndAnimationListener(new OnEndAnimationListener() {
            @Override
            public void onEnd() {
                SecondPartOfAnim an = new SecondPartOfAnim(){
                    @Override
                    void onEndAnim() {
                        mContainer.removeAllViews();
                        end();
                    }
                };
                an.secondPartOfAnim(mSmoke, 300, 400, 600)
                        .start();

            }
        });
    }

    private ScaleAnimation animation(final float relativeSizeInDp,
                                     final View view,
                                     final long time,
                                     final ScaleAnimation startAnim,
                                     long startAnimBeforeTime){
        ScaleAnimation smAn = new ScaleAnimation(
                view,
                1,
                getRealSize(getContainerHeight(mActivity, mContainer), relativeSizeInDp),
                time,
                mActivity
        );
        smAn.setTypeOfAnimation(ScaleAnimation.Type.SECOND);
        smAn.setLayoutAndViewScale(false);
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(startAnimBeforeTime);
        smAn.setOnTickListener(new ScaleAnimation.OnTickListener() {
            @Override
            public void onTick(float after, int counter) {
                if(counter == 1) {
                    view.setAlpha(1);
                } else {
                    try {
                        startAnim.start();
                    }catch(Exception e){}
                }
            }
        }, list);
        smAn.setOnEndAnimationListener(new OnEndAnimationListener() {
            @Override
            public void onEnd() {
                SecondPartOfAnim an = new SecondPartOfAnim(){
                    @Override
                    void onEndAnim() {}
                };
                an.secondPartOfAnim(view, relativeSizeInDp, 250, 100).start();
            }
        });
        return smAn;
    }


    private abstract class SecondPartOfAnim {
        abstract void onEndAnim();

        public Thread secondPartOfAnim(final View view,
                                        final float relativeSizeInDp,
                                        final long timeOfAnim,
                                        final long timeOfBreak) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized(this) {
                        try {
                            wait(timeOfBreak);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ScaleAnimation anim = new ScaleAnimation(
                                    view,
                                    getRealSize(getContainerHeight(mActivity, mContainer),
                                            relativeSizeInDp),
                                    1,
                                    timeOfAnim,
                                    mActivity
                            );

                            anim.setTypeOfAnimation(ScaleAnimation.Type.FIRST);
                            anim.setWhatDoOnEnd(ScaleAnimation.Do.NOTHING);
                            anim.setOnEndAnimationListener(
                                    new OnEndAnimationListener() {
                                        @Override
                                        public void onEnd() {
                                            view.setAlpha(0);
                                            onEndAnim();
                                            mOnAnimationEnd.onEnd();
                                        }
                                    });
                            anim.start();
                        }
                    });
                }
            });
            return thread;
        }
    }


    private int getRealSize(float containerSizeInPx, float relativeSizeInDp){
        int realSizeInPx;
        relativeSizeInDp = containerSizeInPx * relativeSizeInDp;
        realSizeInPx = (int) relativeSizeInDp / 300;
        return realSizeInPx;
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
}
