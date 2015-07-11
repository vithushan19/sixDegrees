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

    private String TAG = "VITHUSHAN - SPLASH";
    private Button mQuickGame;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_splash, container, false);
        mQuickGame = (Button) view.findViewById(R.id.quick_game_button);
        mQuickGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)getActivity()).createGameRoom();
            }
        });
        return view;
    }
}
