package com.vithushan.sixdegrees.animator;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.animation.Interpolator;

import com.vithushan.sixdegrees.view.CirclesLogo;

/**
 * Created by vnama on 10/18/2015.
 */
public class LogoAnimator {

    private ValueAnimator mAnim;

    public LogoAnimator (CirclesLogo circlesLogo, String circleNumber, long delay) {
        mAnim = ObjectAnimator.ofInt
                (circlesLogo, circleNumber,
                        Color.rgb(0x00, 0xAC, 0xC1), Color.rgb(0x85, 0xC8, 0xC5));

        mAnim.setDuration(1500);
        mAnim.setStartDelay(delay);
        mAnim.setRepeatCount(ValueAnimator.INFINITE);
        mAnim.setRepeatMode(ValueAnimator.REVERSE);
        mAnim.setEvaluator(new ArgbEvaluator());
        mAnim.setInterpolator(input -> {
            float result = (float) ((Math.cos((float)((input+1) * Math.PI)) / 2) + 0.5);
            return result;
        });
    }

    public ValueAnimator getAnim() {
        return mAnim;
    }



}
