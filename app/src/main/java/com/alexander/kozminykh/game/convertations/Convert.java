package com.alexander.kozminykh.game.convertations;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Convert {

    public static int dpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dpi = metrics.densityDpi;
        float def = metrics.DENSITY_DEFAULT;
        float fpx = (dpi / def);
        fpx = fpx * dp;
        int px = (int) fpx;
        return px;
    }

    public static float pixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dpi = metrics.densityDpi;
        float def = metrics.DENSITY_DEFAULT;
        float dp = (dpi / def);
        dp = px / dp;
        //float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
