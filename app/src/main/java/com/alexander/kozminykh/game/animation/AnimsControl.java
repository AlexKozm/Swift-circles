package com.alexander.kozminykh.game.animation;


import java.util.ArrayList;
import java.util.List;

public class AnimsControl {
    private static AnimsControl sAnimsControl;

    private List<Animation> mAnims = new ArrayList<>();

    public static AnimsControl get() {
        if(sAnimsControl == null){
            sAnimsControl = new AnimsControl();
        }
        return sAnimsControl;
    }

    public void nullClass(){
        sAnimsControl = null;
    }

    private AnimsControl() {}

    public void cleanAnims(){
        /*for(int i = 0; i <= (mAnims.size() - 1); i++){
            Animation anim = mAnims.get(i);
            anim.cleanAnims();
        }*/
        for(; mAnims.size() >= 1;) {
            mAnims.get(0).end();
            mAnims.remove(0);
        }
    }

    public void add(Animation animation){
        mAnims.add(animation);
    }
}
