package com.alexander.kozminykh.game.GameClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alexander.kozminykh.game.animation.AnimsControl;
import com.alexander.kozminykh.game.convertations.Convert;
import com.alexander.kozminykh.game.player.Player;
import com.alexander.kozminykh.game.player.PlayerLab;
import com.alexander.kozminykh.game.sounds.ActivitiesControl;
import com.alexander.kozminykh.game.R;
import com.alexander.kozminykh.game.database.Settings;
import com.alexander.kozminykh.game.sounds.BeatBox;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kozmi on 12/30/2017.
 */

public class GameFragment extends Fragment{
    private static final String TAG = "GameFragment";

    private final static boolean debug = false;

    public static long timeOfMenuOpenAnimations = 100;
    public static long timeOfMenuCloseAnimations = 500;

    private int mWidth;
    private int mHeight;
    private float mDpWidth;
    private float mDpHeight;

    public static float mContainerCoef;

    private BeatBox sBeatBox;
    private PlayerLab sPlayerLab;
    private TextView countView;
    private TextView recordView;
    private Player mPlayer;
    private TextView mTestView;
    private List<ViewGroup> mViewList;
    private TextView mPlayerNameView;
    private boolean mAttachMenu = false;
    private GameFragment mGameFragment = this;

    private ViewGroup mGameView;

    private OnEndListener sOnEndListener;
    private OnNegativeClick sOnNegativeClick;

    interface OnEndListener{
        void onEnd(Player player);
    }

    public void setOnEndListener(OnEndListener listener){
        sOnEndListener = listener;
    }

    interface OnNegativeClick{
        void onClick();
    }

    public void setOnNegativeClick(OnNegativeClick click){
        sOnNegativeClick = click;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        //Log.i(TAG, "onCreateView");


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x; // 1800 on lenovo
        mHeight = size.y; // 1776 on lenovo
        //Log.i("GameFragment", "Size: " + mWidth + "x" + mHeight );
        //TestText.setTestText("Size" + mWidth + "x" + mHeight);


        final View view = inflater.inflate(R.layout.game_fragment, container, false);


        mGameView = view.findViewById(R.id.game_field);
        mDpWidth  = (float) Convert.pixelsToDp(mWidth, getContext()); // 360 on lenovo
        mDpHeight = (float) Convert.pixelsToDp(mHeight, getContext());// 640 on lenovo
        float width =  mDpWidth;
        width = width - 20;
        mContainerCoef = width / 3; //113
        int iWidth = (int) Convert.dpToPixel(width, getContext());


        FrameLayout.LayoutParams viewParams =
                new FrameLayout.LayoutParams(iWidth, iWidth);
        viewParams.gravity = Gravity.CENTER;
        mGameView.setLayoutParams(viewParams);


        mViewList = new ArrayList<ViewGroup>();

        mViewList.add( (FrameLayout) view.findViewById(R.id.container1));
        mViewList.add( (FrameLayout) view.findViewById(R.id.container2));
        mViewList.add( (FrameLayout) view.findViewById(R.id.container3));
        mViewList.add( (FrameLayout) view.findViewById(R.id.container4));
        mViewList.add( (FrameLayout) view.findViewById(R.id.container5));
        mViewList.add( (FrameLayout) view.findViewById(R.id.container6));
        mViewList.add( (FrameLayout) view.findViewById(R.id.container7));
        mViewList.add( (FrameLayout) view.findViewById(R.id.container8));
        mViewList.add( (FrameLayout) view.findViewById(R.id.container9));


        int height = mHeight/2;
        height = height/3;
        height = height/3;
        //int pxSizeOfText = (120 * mHeight) / 1776;

        countView = (TextView) view.findViewById(R.id.count_view);
        setTextSize(countView, height, 6);

        recordView = (TextView) view.findViewById(R.id.record_view);
        setRecordViewText(String.valueOf(mPlayer.getRecord()));
        setTextSize(recordView, height, 6);

        mPlayerNameView = (TextView) view.findViewById(R.id.player_name);
        setTextSize(mPlayerNameView, height, 7);
        setPlayerNameViewText(mPlayer.getName());
        mPlayerNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeatBox.get(getActivity()).playBulk();
                if(GameProcess.checkGameProcess() == false) {
                    alertDialog(getContext(), mPlayer);
                    setOnEndListener(new OnEndListener() {
                        @Override
                        public void onEnd(Player player) {
                        }
                    });
                    setOnNegativeClick(new OnNegativeClick() {
                        @Override
                        public void onClick() {
                        }
                    });
                }
            }
        });


        mTestView = view.findViewById(R.id.test_view);
        mTestView.setMovementMethod(new ScrollingMovementMethod());
        mTestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TestText.setTestText("click on test");
            }
        });
        /*mTestView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mTestView.setText(null);
                return false;
            }
        });*/
        TestText.setTestText("onCreateView");

        GameMenu gameMenu = GameMenu.create(mViewList, mPlayer, getActivity(), this);
        gameMenu.attachMenuViews(0);

        return view;
    }


    public AlertDialog alertDialog(final Context context, final Player player){
        final EditText editText = new EditText(context);
        int maxLength = 15;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        editText.setFilters(FilterArray);
        //editText.setText(player.getName(), TextView.BufferType.EDITABLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.enter_nickname))
                .setCancelable(false)
                .setView(editText)
                .setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                BeatBox.get(context).play(BeatBox.GOOD);
                                String s = editText.getText().toString();
                                if((s.equals(""))||(s == null) ){
                                    s = "player";
                                }
                                player.setName(s);
                                PlayerLab.get(context).updatePlayer(player);
                                setPlayerNameViewText(s);
                                if(sOnEndListener != null) {
                                    sOnEndListener.onEnd(player);
                                }
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sBeatBox.play(BeatBox.GOOD);
                        dialog.cancel();
                        if (sOnNegativeClick != null){
                            sOnNegativeClick.onClick();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        editText.setText(stringToCharSequence(player.getName()));
        editText.selectAll();
        editText.invalidate();
        return alert;
    }


    @Override
    public void onResume() {
        super.onResume();
        TestText.setTestText("onResume");

        getActivePlayer();

        //BeatBox.updateBeatBox(getActivity());

        if(mAttachMenu){
            mAttachMenu = false;
            cleanField();
            updateUI(mPlayer);
            GameMenu gameMenu = GameMenu.create(mViewList, mPlayer, getActivity(), this);
            gameMenu.attachMenuViews(0);
        }

        ActivitiesControl.getInstance().firstActivityResume(getActivity());

    }


    @Override
    public void onPause() {
        super.onPause();
        ActivitiesControl.getInstance().firstActyvityPause(getContext());
        TestText.setTestText("onPause");

        BeatBox.get(getActivity()).stopSounds();
        mAttachMenu = true;
        if(GameProcess.checkGameProcess()){
            GameProcess.cleanGameProcess();
        }
        AnimsControl.get().cleanAnims();
        AnimsControl.get().nullClass();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sBeatBox.release();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrash.log("Activity created");
        getActivePlayer();
        TestText.setTestText("onCreate");

        setRetainInstance(true);

        new Settings(getActivity());
        sBeatBox = BeatBox.get(getActivity());

    }

    private void getActivePlayer(){
        sPlayerLab = PlayerLab.get(getContext());
        List<Player> players = sPlayerLab.getPlayers();
        Player activePlayer = sPlayerLab.getActivePlayer();
        if(players.size() == 0){
            mPlayer = new Player("Player1", true, 0);
            sPlayerLab.addPlayer(mPlayer);
        } else if (activePlayer != null){
            mPlayer = activePlayer;
        } else {
            mPlayer = players.get(0);
        }
    }


    public static CharSequence stringToCharSequence(final String string){
        CharSequence charSequence = new CharSequence() {
            @Override
            public int length() {
                return 0;
            }

            @Override
            public char charAt(int index) {
                return 0;
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return null;
            }

            @NonNull
            @Override
            public String toString() {
                return string;
            }
        };
        return charSequence;
    }

    public void updateUI(Player player){
        mPlayer = player;
        setRecordViewText(String.valueOf(player.getRecord()));
        setCountViewText("0");
        setPlayerNameViewText(player.getName());
    }

    public void setRecordViewText(String string){
        string = "Record: " + string;
        recordView.setText(stringToCharSequence(string));
    }

    public void hideRecordView(){
        recordView.setText(stringToCharSequence(""));
    }

    public void setCountViewText(String string){
        string = "Now: " + string;
        countView.setText(stringToCharSequence(string));
    }

    public void hideCountView(){
        countView.setText(stringToCharSequence(""));
    }

    public void setPlayerNameViewText(String string){
        mPlayerNameView.setText(stringToCharSequence(string));
    }


    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public float getDpWidth() {
        return mDpWidth;
    }

    public static float getSize(float size){
        float size1 =  (mContainerCoef * size) / 113;
        return size1;
    }


    public void cleanField(){
        for(int i = 0; i <= (mViewList.size() - 1); i++){
            mViewList.get(i).removeAllViews();
        }
    }


    public static class TestText {
        private static String string = "";
        private static boolean isStringHaveUsed = false;
        private static int counter = 0;

        public static void setTestText(String s) {

            /*if(debug) {
                if(mTestView != null) {
                    if(isStringHaveUsed == false) {
                        s = string + s + "\n";
                        isStringHaveUsed = true;
                        counter++;
                    } else {
                        s = mTestView.getText() + s + "\n";
                        counter++;
                    }
                    //delete before cleanAnims
                    mTestView.setText(String.valueOf( s*//*(System.getProperty("line.separator"))*//*));
                    // mTestView.setText(System.getProperty("line.separator"));
                } else {
                    string = string + s + "\n"*//*System.getProperty("line.separator")*//*;
                    counter++;
                }
                if(counter >= 60){
                    clearString(s);
                }
            }*/
        }

        private static void clearString(String s){
            /*string = "";
            counter = 0;
            mTestView.setText(string);
            isStringHaveUsed = false;*/

        }
    }


    //from GameMenu
    private LinearLayout.LayoutParams getLayoutWithSize(int sizeInPx){
        //int size2 = getSize(sizeInDp);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                sizeInPx);
        viewParams.gravity = Gravity.START;
        return viewParams;
    }

    private void setTextSize(TextView textView, int height, int i){
        textView.setLayoutParams(getLayoutWithSize((height/5) * i));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height);
        textView.setGravity(Gravity.CENTER|Gravity.START);
    }

}
