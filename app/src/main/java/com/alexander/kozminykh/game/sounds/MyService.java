package com.alexander.kozminykh.game.sounds;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MyService extends Service implements MediaPlayer.OnPreparedListener{
    public static String BACKGROUND_MUSIC = "background.mp3";
    private static final String SOUND_FOLDER = "sample_sounds";

    private static MediaPlayer mMediaPlayer;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = getAssets().openFd(SOUND_FOLDER + "/" + BACKGROUND_MUSIC);
            mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mMediaPlayer.prepare();
            mMediaPlayer.setVolume(1f, 1f);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
    }
}
