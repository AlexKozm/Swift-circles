package com.alexander.kozminykh.game.sounds;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.alexander.kozminykh.game.database.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * Created by kozmi on 1/9/2018.
 */

public class BeatBox {
    public static String BANG = "bang";
    public static String BULK1 = "bulk1";
    public static String BULK2 = "bulk2";
    public static String BULK3 = "bulk3";
    public static String BACKGROUND_MUSIC = "background.mp3";
    public static String GOOD = "good";
    public static String LOSE = "lose";

    private static final String Tag = "BeatBox";
    private static final String SOUND_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;

    private static BeatBox mBeatBox;

    private AssetManager mAsset;
    private List<Sound> mSounds = new ArrayList<>();
    private static SoundPool mSoundPool;

    //private Context mContext;

    private static boolean mIsSoundsOn;

    public static BeatBox get(Context context){
        if (mBeatBox == null){
            mBeatBox = new BeatBox(context);
        }
        return mBeatBox;
    }

    public static void updateBeatBox(Context context){
        mBeatBox = new BeatBox(context);
    }

    private BeatBox(Context context) {
        //mContext = context;
        mAsset = context.getAssets();
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        loadSounds();
        mIsSoundsOn = Settings.isSoundsOn();
    }


    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if(mIsSoundsOn){
            int i = mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
            i = i;
        }
    }


    public void release() {
        mSoundPool.release();
    }


    private void load(Sound sound) throws IOException {
        AssetFileDescriptor afd = mAsset.openFd(sound.getAssetPath());
        int soundId = mSoundPool.load(afd, 1);
        sound.setSoundId(soundId);
    }


    private void loadSounds() {
        String[] soundNames;
        try {
            soundNames = mAsset.list(SOUND_FOLDER);
            Log.i(Tag, "Found " + soundNames.length + " sounds");
        } catch(IOException ioe) {
            Log.e(Tag, "Could not list assets", ioe);
            return;
        }

        for(String filename : soundNames) {
            try {
                String assetPath = SOUND_FOLDER + "/" + filename;
                Sound sound = new Sound(assetPath);
                load(sound);
                mSounds.add(sound);
            } catch(IOException ioe) {
                Log.e(TAG, "Could not load sound " + filename, ioe);
            }

        }

    }


    private List<Sound> getSounds() {
        return mSounds;
    }


    public void play(String soundName) {
        if(mIsSoundsOn){
            List<Sound> sounds = getSounds();

            for(int i = 0; sounds.size() - 1 >= i; i++) {
                if(sounds.get(i).getName().equals(soundName)) {
                    play(sounds.get(i));
                }
            }
        }
    }

    public void playBulk(){
        if(mIsSoundsOn) {
            int randomNumFromNullTo = 3;
            Random random = new Random();
            int randomNum = random.nextInt(randomNumFromNullTo);

            String soundName;
            if (randomNum == 0) {
                soundName = BULK1;
            } else if (randomNum == 1) {
                soundName = BULK2;
            } else {
                soundName = BULK3;
            }
            final String sound = soundName;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    play(sound);
                }
            });
            thread.start();
        }
    }

    public void playBackground(Context context) {
        if(mIsSoundsOn) {
            context.startService(new Intent(context, MyService.class));
        }
    }

    public void endBackgroundMusic(Context context){
        context.stopService(new Intent(context, MyService.class));
    }

    public void ChangeSoundOnOff(Context context){
        if (mIsSoundsOn){
            Settings.setSound(false);
            mIsSoundsOn = false;
            endBackgroundMusic(context);
            ActivitiesControl.getInstance().setServiseRun(false);
        } else {
            Settings.setSound(true);
            mIsSoundsOn = true;
            playBackground(context);
            ActivitiesControl.getInstance().setServiseRun(true);
        }
    }

    public boolean isSoundsOn(){
        return mIsSoundsOn;
    }

    public void stopSounds(){
        mSoundPool.autoPause();
    }
}
