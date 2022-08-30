package com.alexander.kozminykh.game.animation;

public interface Animation{
    void end();
    void start();
    void setOnEndAnimationListener(OnEndAnimationListener listener);
}
