package com.alexander.kozminykh.game.gameObject;

import com.alexander.kozminykh.game.animation.Animation;

public interface GameObject extends Animation{
    void setOnDefeatListener(OnDefeat onDefeat);
}
