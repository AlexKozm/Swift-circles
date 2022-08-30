package com.alexander.kozminykh.game.sounds;

/**
 * Created by kozmi on 1/26/2018.
 */

public class Sound {

    private String mAssetPath;
    private String mName;
    private Integer mSoundId;

    public Sound(String assetPath){
        mAssetPath = assetPath;
        String[] components = assetPath.split("/");
        String filename = components[components.length - 1];
        mName = filename.replace(".wav", "");
        //mName = filename.replace(".mp3", "");

    }

    public String getName() {
        return mName;
    }

    public String getAssetPath() {
        return mAssetPath;
    }

    public Integer getSoundId() {
        return mSoundId;
    }

    public void setSoundId(Integer soundId) {
        mSoundId = soundId;
    }
}
