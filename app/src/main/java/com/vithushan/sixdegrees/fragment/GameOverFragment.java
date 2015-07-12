package com.vithushan.sixdegrees.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.activity.GameActivity;


public class GameOverFragment extends Fragment {
	private TextView mStateTextView;
	private Button mRematch;
    private Button mMainMenu;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_game_over, container, false);

        mRematch = (Button) view.findViewById(R.id.button_rematch);
        mRematch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameActivity)getActivity()).gotoSelectActorFragment();
            }
        });

        mMainMenu = (Button) view.findViewById(R.id.button_main_menu);
        mMainMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameActivity)getActivity()).leaveRoom();
            }
        });

        mStateTextView = (TextView) view.findViewById(R.id.textview_status);
        mStateTextView.setSelected(true);
		boolean wonGame = getArguments().getBoolean("Won");
		if (wonGame) {
				mStateTextView.setText("YOU WON");
		} else {
			mStateTextView.setText("YOU LOST");
		}
		return view;
	}


}
