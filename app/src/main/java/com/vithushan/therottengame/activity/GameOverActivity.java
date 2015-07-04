package com.vithushan.therottengame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.vithushan.therottengame.R;


public class GameOverActivity extends Activity implements OnClickListener {
	private TextView mStateTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_over);
		mStateTextView = (TextView) findViewById(R.id.textview_status);
		mStateTextView.setOnClickListener(this);
		Intent intent = getIntent();
		boolean wonGame = intent.getBooleanExtra("Won", false);

		if (wonGame) {
			mStateTextView.setText("YOU WON");
		} else {
			mStateTextView.setText("YOU LOST");
		}

	}

	@Override
	public void onClick(View v) {
		//Intent intent = new Intent (this, MainActivity.class);
		//startActivity(intent);
	}
}
