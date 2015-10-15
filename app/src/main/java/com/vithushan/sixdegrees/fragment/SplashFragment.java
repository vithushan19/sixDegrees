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
import com.vithushan.sixdegrees.view.Circle;

/**
 * Created by vnama on 7/9/2015.
 */
public class SplashFragment extends Fragment {

    private String TAG = "SplashFragment";

    private Circle mCircle;
    private Button mQuickGame;
    private Button mInvitePlayers;
    private Button mMyInvitations;
    private Button mSinglePlayer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        mCircle = (Circle) view.findViewById(R.id.circles);
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
        ValueAnimator skyAnim = ObjectAnimator.ofInt
                (mCircle, "backgroundColor",
                        Color.rgb(0x66, 0xcc, 0xff), Color.rgb(0x00, 0x66, 0x99));
        skyAnim.setDuration(3000);
        skyAnim.setRepeatCount(ValueAnimator.INFINITE);
        skyAnim.setRepeatMode(ValueAnimator.REVERSE);
        skyAnim.setEvaluator(new ArgbEvaluator());
        skyAnim.start();
    }
}
