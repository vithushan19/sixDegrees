package com.vithushan.therottengame.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vithushan.therottengame.R;
import com.vithushan.therottengame.activity.GameActivity;

/**
 * Created by vnama on 7/9/2015.
 */
public class SplashFragment extends Fragment {

    private String TAG = "SplashFragment";
    private Button mQuickGame;
    private Button mSinglePlayer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        mQuickGame = (Button) view.findViewById(R.id.quick_game_button);
        mQuickGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)getActivity()).createGameRoom();
            }
        });
        mSinglePlayer = (Button) view.findViewById(R.id.button_single_player);
        mSinglePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)getActivity()).startSinglePlayer();
            }
        });
        return view;
    }
}
