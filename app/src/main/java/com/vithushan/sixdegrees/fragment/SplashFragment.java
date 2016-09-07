package com.vithushan.sixdegrees.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.animator.LogoAnimator;
import com.vithushan.sixdegrees.view.CirclesLogo;

/**
 * Created by vnama on 7/9/2015.
 */
public class SplashFragment extends Fragment {

    private String TAG = "SplashFragment";

    private View mTransparentRect;
    private CirclesLogo mCirclesLogo;
    private Button mSinglePlayer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        mCirclesLogo = (CirclesLogo) view.findViewById(R.id.circles);
        mTransparentRect = view.findViewById(R.id.transparentRect);

        mSinglePlayer = (Button) view.findViewById(R.id.button_single_player);
        mSinglePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)getActivity()).startGame(false);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        LogoAnimator logoAnimator = new LogoAnimator(mCirclesLogo, "color1", 0);
        LogoAnimator logoAnimator2 = new LogoAnimator(mCirclesLogo, "color2", 250);
        LogoAnimator logoAnimator3 = new LogoAnimator(mCirclesLogo, "color3", 500);
        LogoAnimator logoAnimator4 = new LogoAnimator(mCirclesLogo, "color4", 750);
        LogoAnimator logoAnimator5 = new LogoAnimator(mCirclesLogo, "color5", 1000);
        LogoAnimator logoAnimator6 = new LogoAnimator(mCirclesLogo, "color6", 1250);

        logoAnimator.getAnim().start();
        logoAnimator2.getAnim().start();
        logoAnimator3.getAnim().start();
        logoAnimator4.getAnim().start();
        logoAnimator5.getAnim().start();
        logoAnimator6.getAnim().start();

        ValueAnimator anim = ObjectAnimator.ofInt
                (mTransparentRect, "backgroundColor",
                        Color.argb(0x02, 0x7B, 0xEC, 0xFF), Color.argb(0x5D, 0xD2, 0xF3, 0xFF));
        anim.setDuration(1500);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setEvaluator(new ArgbEvaluator());
        //anim.start();
    }
}
