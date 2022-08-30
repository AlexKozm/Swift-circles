package com.alexander.kozminykh.game.GameClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexander.kozminykh.game.AboutAppClasses.AboutAppActivity;
import com.alexander.kozminykh.game.sounds.ActivitiesControl;
import com.alexander.kozminykh.game.R;
import com.alexander.kozminykh.game.animation.AnimsControl;
import com.alexander.kozminykh.game.animation.OnEndAnimationListener;
import com.alexander.kozminykh.game.animation.ScaleAnimation;
import com.alexander.kozminykh.game.convertations.Convert;
import com.alexander.kozminykh.game.player.Player;
import com.alexander.kozminykh.game.player.PlayerLab;
import com.alexander.kozminykh.game.sounds.BeatBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kozmi on 2/16/2018.
 */

public class GameMenu {

    public long timeOfChangeAnimations = 250;

    private static GameMenu sGameMenu;
    private PlayerLab sPlayerLab;
    private List<ViewGroup> mViewList;
    private List<View> mViews = new ArrayList<>();
    private List<Float> mSizes = new ArrayList<>();
    private List<View> mClicableViews = new ArrayList<>();
    private View mView;
    private ViewGroup mFragmentContainer;
    private Context mContext;
    private Activity mActivity;
    private GameProcess sGameProcess;
    private Player mPlayer;
    private GameFragment mGameFragment;


    private Button sStartButton;
    private Button sSettingsButton;
    private Button sChangePlayerButton;

    private int mStartScale = 0;
    private int mEndScale = 0;

    private static OnAllViewsRemove mOnAllViewsRemove;


    public static GameMenu create(List<ViewGroup> viewList, Player player, Activity activity, GameFragment gameFragment) {
        sGameMenu = new GameMenu(viewList, player, activity, gameFragment);
        return sGameMenu;
    }

    public static boolean check(){
        if(sGameMenu == null){
            return false;
        } else {
            return true;
        }
    }

    public static void clear(){
        sGameMenu = null;
    }


    private GameMenu(List<ViewGroup> viewList, Player player, Activity activity, GameFragment gameFragment){
        mViewList = viewList;
        mContext = activity;
        mPlayer = player;
        mActivity = activity;
        sPlayerLab = PlayerLab.get(mContext);
        mGameFragment = gameFragment;
    }


    /**
     * get relative size for different sizes of screen
     * @param sizeInDp
     * @return relative size in px
     */
    private int getSize(float sizeInDp){


        float size1 = GameFragment.getSize(sizeInDp);
        //GameFragment.TestText.setTestText("getSize.sizeInDp = " + size1);
        int size2 = Convert.dpToPixel(size1, mContext);
        return size2;
    }

    private FrameLayout.LayoutParams getLayoutWithSize(int sizeInPx){
        //int size2 = getSize(sizeInDp);
        FrameLayout.LayoutParams viewParams = new FrameLayout.LayoutParams(sizeInPx, sizeInPx);
        viewParams.gravity = Gravity.CENTER;
        return viewParams;
    }


    @SuppressLint("ClickableViewAccessibility")
    public void setMenuItemsOnClickListeners(){
        sStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeatBox.get(mActivity).playBulk();

                removeAllViews(mViewList);
                setOnViewsRemoveListener(new OnAllViewsRemove() {
                    @Override
                    public void onEnd() {
                        sGameMenu = null;
                        sGameProcess = GameProcess.get(mViewList, mContext, mPlayer, mGameFragment);
                        sGameProcess.start();
                    }
                });


            }
        });

        sSettingsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN ){
                    BeatBox.get(mActivity).playBulk();

                    RotateAnimation rotate = new RotateAnimation(
                            0,
                            360,
                            Animation.RELATIVE_TO_SELF,
                            0.5f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f);
                    rotate.setDuration(1000);
                    rotate.setInterpolator(new LinearInterpolator());
                    sSettingsButton.startAnimation(rotate);

                    settingsDiaolog(mActivity).show();
                } else if(event.getAction() == MotionEvent.ACTION_UP){


                }
                return false;
            }
        });

        sChangePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeatBox.get(mActivity).playBulk();

                removeAllViews(mViewList);
                mGameFragment.hideRecordView();
                mGameFragment.hideCountView();
                setOnViewsRemoveListener(new OnAllViewsRemove() {
                    @Override
                    public void onEnd() {
                        setChangePlayerView(mViewList);
                    }
                });

            }
        });
    }

    public void attachMenuViews(long timeOfAllAnimations){
        List<View> views = new ArrayList<>();
        List<Float> sizes = new ArrayList<>();
        int pxSize;
        float scaleSize;

        mView = View.inflate(mContext, R.layout.menu_buttons, null);


        sStartButton = (Button) mView.findViewById(R.id.start_button);
        pxSize = getSize(100);
        ViewGroup parent1 = (ViewGroup) sStartButton.getParent();
        parent1.removeView(sStartButton);
        mViewList.get(4).addView(sStartButton);

        views.add(sStartButton);
        sizes.add((float) pxSize);


        sSettingsButton = (Button) mView.findViewById(R.id.settings_button);
        pxSize = getSize(100);
        ViewGroup parent2 = (ViewGroup) sSettingsButton.getParent();
        parent2.removeView(sSettingsButton);
        mViewList.get(3).addView(sSettingsButton);

        views.add(sSettingsButton);
        sizes.add((float) getSize(100));


        sChangePlayerButton = (Button) mView.findViewById(R.id.change_player);
        ViewGroup parent3 = (ViewGroup) sChangePlayerButton.getParent();
        parent3.removeView(sChangePlayerButton);
        mViewList.get(5).addView(sChangePlayerButton);

        views.add(sChangePlayerButton);
        sizes.add((float) getSize(90));

        setIncreaseAnim(timeOfAllAnimations, views, sizes, IncreaseMenuView.MENU_BUTTONS);
    }

    private void setIncreaseAnim(long timeOfAllAnimations, List<View> views, List<Float> sizes,
                                 String cons){
        if(timeOfAllAnimations == 0){
            for(int i = 0; i <= views.size() - 1; i++){
                View v = views.get(i);
                float size = sizes.get(i);
                v.setLayoutParams(getLayoutWithSize((int)size));
            }
            setMenuItemsOnClickListeners();
        } else {
            new IncreaseMenuView().increaseViews(views, sizes, timeOfAllAnimations, cons);
        }
    }


    private void setChangeViewsEnable(){
        for(int count = 0; count <= (mClicableViews.size() - 1); count++){
            mClicableViews.get(count).setEnabled(true);
        }
        mClicableViews.clear();
        mViews.clear();
        mSizes.clear();
    }

    private void setChangePlayerView(final List<ViewGroup> containers){
        mGameFragment.setPlayerNameViewText("");
        final List<Player> players = sPlayerLab.getPlayers();
        int c;
        for(c = 0;(containers.size() - 1 >= c) && (players.size() - 1 >= c); c++){
            final Player player = players.get(c);
            final ViewGroup container = containers.get(c);


            View view = View.inflate(mContext, R.layout.select_player_button, container);

            FrameLayout v = view.findViewById(R.id.select_player_layout);
            float size = getSize(100);
            //v.setLayoutParams(getLayoutWithSize((int) size));

            final float sizeOfpicture = (getSize(100) / 100) * 90;

            final ImageView image = view.findViewById(R.id.image);
            //height = convertPixelsToDp(height, mContext);
            FrameLayout.LayoutParams lp = getLayoutWithSize((int)sizeOfpicture);
            image.setLayoutParams(lp);

            TextView textView = view.findViewById(R.id.select_palyer);
            textView.setText(GameFragment.stringToCharSequence(player.getName()));

            ImageView imageView = view.findViewById(R.id.select_image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //float i = convertPixelsToDp((float) image.getHeight(), mContext);
                    removeAllViews(containers);
                    v.setOnClickListener(null);

                    setOnViewsRemoveListener(new OnAllViewsRemove() {
                        @Override
                        public void onEnd() {
                            attachMenuViews(GameFragment.timeOfMenuOpenAnimations);
                        }
                    });

                    mPlayer = player;
                    player.setActive(true);
                    mGameFragment.updateUI(player);
                    PlayerLab.get(mContext).setPlayerActive(player);
                }
            });
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(MotionEvent.ACTION_DOWN == event.getAction()){
                        BeatBox.get(mActivity).playBulk();
                    }
                    return false;
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mPlayer = player;
                    alertConfirmDialog(mContext, player);
                    return true;
                }
            });

            imageView.setEnabled(false);
            mClicableViews.add(imageView);
            mViews.add(v);
            mSizes.add(size);
        }

        if(players.size() - 1 < 8 ){
            ViewGroup container = containers.get(c);

            Button button = new Button(mContext);
            float size = getSize(100);
            button.setLayoutParams(getLayoutWithSize(1));
            Drawable plus =
                    mContext.getResources().getDrawable(R.drawable.gm_add_circle_blue_100dp);
            button.setBackground(plus);
            container.addView(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BeatBox.get(mActivity).playBulk();

                    v.setOnClickListener(null);
                    Player player = new Player();
                    player.setName("player");
                    //mPlayer.setActive(true);
                    player.setRecord(0);
                    //PlayerLab.get(mContext).addPlayer(player);
                    mGameFragment.alertDialog(mContext, player);
                    mGameFragment.setOnNegativeClick(new GameFragment.OnNegativeClick() {
                        @Override
                        public void onClick() {
                            removeAllViews(containers);
                            setOnViewsRemoveListener(new OnAllViewsRemove() {
                                @Override
                                public void onEnd() {
                                    setChangePlayerView(containers);
                                }
                            });
                        }
                    });
                    mGameFragment.setOnEndListener(new GameFragment.OnEndListener() {
                        @Override
                        public void onEnd(Player player1) {
                            PlayerLab.get(mContext).addPlayer(player1);
                            mPlayer = player1;

                            mPlayer.setActive(true);
                            mGameFragment.updateUI(mPlayer);
                            PlayerLab.get(mContext).setPlayerActive(mPlayer);

                            removeAllViews(containers);
                            setOnViewsRemoveListener(new OnAllViewsRemove() {
                                @Override
                                public void onEnd() {
                                    //setChangePlayerView(containers);
                                    attachMenuViews(GameFragment.timeOfMenuOpenAnimations);
                                }
                            });

                            //GameFragment.updateUI(mPlayer);
                            //GameFragment.setPlayerNameViewText("");
                        }
                    });

                }
            });

            button.setEnabled(false);
            mClicableViews.add(button);
            mViews.add(button);
            mSizes.add(size);
        }

        setIncreaseAnim(GameFragment.timeOfMenuOpenAnimations, mViews, mSizes, IncreaseMenuView.CHANGE_BUTTONS);
    }


    public class IncreaseMenuView {
        public static final String MENU_BUTTONS = "menuButtons";
        public static final String CHANGE_BUTTONS = "changeButtons";

        private int mCounterOfAnimations = 0;
        private int mCountOfViews;


        public void increaseViews(final List<View> viewList,
                                  final List<Float> sizesOfViews,
                                  long time, final String constant) {

            mCountOfViews = viewList.size();

            for(int i = 0; i < (mCountOfViews); i++) {
                View v = viewList.get(i);
                float size = sizesOfViews.get(i);
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        v,
                        1,
                        size,
                        time,
                        (Activity) mContext
                );
                scaleAnimation.setWhatDoOnEnd(ScaleAnimation.Do.SET_SCALE);
                scaleAnimation.setTypeOfAnimation(ScaleAnimation.Type.SECOND);
                scaleAnimation.
                        setOnEndAnimationListener(new OnEndAnimationListener() {
                            @Override
                            public void onEnd() {
                                mCounterOfAnimations++;
                                if(mCounterOfAnimations <= mCountOfViews){
                                    AnimsControl.get().cleanAnims();
                                    if(constant.equals(MENU_BUTTONS)) {
                                        setMenuItemsOnClickListeners();
                                    } else if(constant.equals(CHANGE_BUTTONS)){
                                        setChangeViewsEnable();
                                    }
                                }
                            }
                        });
                AnimsControl.get().add(scaleAnimation);
                scaleAnimation.start();
            }

        }
    }




    interface OnAllViewsRemove{
        void onEnd();
    }

    public void setOnViewsRemoveListener(OnAllViewsRemove removeListener){
        mOnAllViewsRemove = removeListener;
    }

    public void removeAllViews(final List<ViewGroup> viewList){
        /*if(!hasChild(viewList)){
            mOnAllViewsRemove.onEnd();
        }*/

        for(int i = 0; i <= (viewList.size() - 1); i++){
            //viewList.get(i).removeAllViews();
            int count = viewList.get(i).getChildCount();
            if(count >= 1) {
                View v = viewList.get(i).getChildAt(0);
                v.setOnClickListener(null);
                v.setOnTouchListener(null);
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        v,
                        100,
                        1,
                        GameFragment.timeOfMenuCloseAnimations,
                        (Activity) mContext
                );
                //scaleAnimation.setTypeOfAnimation(ScaleAnimation.Type.FIRST);
                scaleAnimation.setOnEndAnimationListener(
                        new OnEndAnimationListener() {
                            @Override
                            public void onEnd() {
                                mEndScale++;
                                if(mStartScale <= mEndScale) {
                                    for(int i = 0; i <= (viewList.size() - 1); i++) {
                                        viewList.get(i).removeAllViews();
                                    }
                                    AnimsControl.get().cleanAnims();
                                    mOnAllViewsRemove.onEnd();
                                }
                            }
                        });
                AnimsControl.get().add(scaleAnimation);
                scaleAnimation.start();
                mStartScale++;
            }
        }
    }

    private static boolean hasChild(List<ViewGroup> viewList){
        for(int count = 1; count <= (viewList.size() - 1); count++){
            if(viewList.get(count).getChildAt(0) != null){
                return true;
            }
        }
        return false;
    }


    private AlertDialog.Builder settingsDiaolog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        final View dialogLayout = inflater.inflate(R.layout.settings_dialog, null);
        builder.setView(dialogLayout);

        Button delRecordView = dialogLayout.findViewById(R.id.del_record);
        final View confirm = dialogLayout.findViewById(R.id.confirm);
        final ViewGroup scene = dialogLayout.findViewById(R.id.scene);
        delRecordView.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View v) {
                BeatBox.get(mActivity).playBulk();
                TransitionManager.beginDelayedTransition(scene);
                visible = !visible;
                confirm.setVisibility(visible ? View.VISIBLE : View.GONE);

                Button yes = dialogLayout.findViewById(R.id.rec_yes);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BeatBox.get(mActivity).play(BeatBox.GOOD);
                        mPlayer.setRecord(0);
                        sPlayerLab.updatePlayer(mPlayer);
                        mGameFragment.setRecordViewText("0");
                        TransitionManager.beginDelayedTransition(scene);
                        visible = !visible;
                        confirm.setVisibility(visible ? View.VISIBLE : View.GONE);
                    }
                });

                Button no = dialogLayout.findViewById(R.id.rec_no);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BeatBox.get(mActivity).play(BeatBox.GOOD);
                        TransitionManager.beginDelayedTransition(scene);
                        visible = !visible;
                        confirm.setVisibility(visible ? View.VISIBLE : View.GONE);
                    }
                });

            }
        });

        final View prohibition = dialogLayout.findViewById(R.id.prohibition);
        if (BeatBox.get(mActivity).isSoundsOn()){
            prohibition.setVisibility(View.INVISIBLE);
        }

        Button sound = dialogLayout.findViewById(R.id.sound);
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSoundsOn = BeatBox.get(mActivity).isSoundsOn();
                if (isSoundsOn){
                    prohibition.setVisibility(View.VISIBLE);
                    BeatBox.get(mActivity).ChangeSoundOnOff(mActivity);
                } else {
                    prohibition.setVisibility(View.INVISIBLE);
                    BeatBox.get(mActivity).ChangeSoundOnOff(mActivity);
                }
            }
        });

        Button about = dialogLayout.findViewById(R.id.about_app);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeatBox.get(mActivity).playBulk();
                ActivitiesControl.getInstance().goToTheSecondAcivity();
                Intent intent = AboutAppActivity.newIntent(mActivity);
                mGameFragment.startActivity(intent);
            }
        });

        /*Button crash = dialogLayout.findViewById(R.id.crash_app);
        crash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new NullPointerException("Click on crash");
            }
        });*/

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BeatBox.get(mActivity).play(BeatBox.GOOD);

            }
        });

        return builder;
    }

    private void alertConfirmDialog(final Context context, final Player player){
        final EditText editText = new EditText(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Do you want to remove " + player.getName())
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BeatBox.get(mActivity).play(BeatBox.GOOD);
                        sPlayerLab.deletePlayer(player);
                        removeAllViews(mViewList);
                        setOnViewsRemoveListener(new OnAllViewsRemove() {
                            @Override
                            public void onEnd() {
                                setChangePlayerView(mViewList);
                            }
                        });

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                BeatBox.get(mActivity).play(BeatBox.GOOD);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}








