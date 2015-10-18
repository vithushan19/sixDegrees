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
import android.view.animation.Interpolator;
import android.widget.Button;

import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.animator.LogoAnimator;
import com.vithushan.sixdegrees.view.Circle;

/**
 * Created by vnama on 7/9/2015.
 */
public class SplashFragment extends Fragment {

    private String TAG = "SplashFragment";

    private View mTransparentRect;
    private Circle mCircle;
    private Button mQuickGame;
    private Button mInvitePlayers;
    private Button mMyInvitations;
    private Button mSinglePlayer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        mCircle = (Circle) view.findViewById(R.id.circles);
        mTransparentRect = view.findViewById(R.id.transparentRect);

        mQuickGame = (Button) view.findViewById(R.id.quick_game_button);
        mQuickGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)getActivity()).startQuickGame();
            }
        });
        mInvitePlayers = (Button) view.findViewById(R.id.invite_players_button);
        mInvitePlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)getActivity()).invitePlayers();
            }
        });
        mMyInvitations = (Button) view.findViewById(R.id.my_invitations_button);
        mMyInvitations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)getActivity()).showInvitationInbox();
            }
        });
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

        LogoAnimator logoAnimator = new LogoAnimator(mCircle, "color1", 0);
        LogoAnimator logoAnimator2 = new LogoAnimator(mCircle, "color2", 250);
        LogoAnimator logoAnimator3 = new LogoAnimator(mCircle, "color3", 500);
        LogoAnimator logoAnimator4 = new LogoAnimator(mCircle, "color4", 750);
        LogoAnimator logoAnimator5 = new LogoAnimator(mCircle, "color5", 1000);
        LogoAnimator logoAnimator6 = new LogoAnimator(mCircle, "color6", 1250);

        logoAnimator.getAnim().start();
        logoAnimator2.getAnim().start();
        logoAnimator3.getAnim().start();
        logoAnimator4.getAnim().start();
        logoAnimator5.getAnim().start();
        logoAnimator6.getAnim().start();

        ValueAnimator anim = ObjectAnimator.ofInt
                (mTransparentRect, "backgroundColor",
                        Color.argb(0xA0, 0xFF, 0xFF, 0xFF), Color.argb(0x50, 0xFF, 0xFF, 0xFF));
        anim.setDuration(1500);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setEvaluator(new ArgbEvaluator());
        anim.start();
    }
}
