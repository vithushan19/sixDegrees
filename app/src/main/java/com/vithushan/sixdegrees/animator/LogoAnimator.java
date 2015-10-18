package com.vithushan.sixdegrees.animator;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.animation.Interpolator;

import com.vithushan.sixdegrees.view.Circle;

/**
 * Created by vnama on 10/18/2015.
 */
public class LogoAnimator {

    private ValueAnimator mAnim;

    public LogoAnimator (Circle circle, String circleNumber, long delay) {
        mAnim = ObjectAnimator.ofInt
                (circle, circleNumber,
                        Color.rgb(0x00, 0xAC, 0xC1), Color.rgb(0x26, 0xC6, 0xDA));

        mAnim.setDuration(3000);
        mAnim.setStartDelay(delay);
        mAnim.setRepeatCount(ValueAnimator.INFINITE);
        mAnim.setRepeatMode(ValueAnimator.REVERSE);
        mAnim.setEvaluator(new ArgbEvaluator());
        mAnim.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                float result = (float) ((Math.cos((float)((input+1) * Math.PI)) / 2) + 0.5);
                return result;
            }
        });
    }

    public ValueAnimator getAnim() {
        return mAnim;
    }



}
