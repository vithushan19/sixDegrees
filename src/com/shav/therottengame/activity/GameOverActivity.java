package com.shav.therottengame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.shav.therottengame.R;

public class GameOverActivity extends Activity {
	private TextView mStateTextView;
	private GoogleApiClient mGoogleApiClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_over);
		mStateTextView = (TextView) findViewById(R.id.textview_status);

		Intent intent = getIntent();
		boolean wonGame = intent.getBooleanExtra("Won", false);
		Room room = intent.getParcelableExtra("Room");
		
		if (wonGame) {
			mStateTextView.setText("YOU WON");
		} else {
			mStateTextView.setText("YOU LOST");
		}

	}
}
