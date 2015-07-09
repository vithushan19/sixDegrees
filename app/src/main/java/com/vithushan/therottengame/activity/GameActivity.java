package com.vithushan.therottengame.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.vithushan.therottengame.R;
import com.vithushan.therottengame.model.Actor;

public class GameActivity extends BaseActivity {
	private String TAG = "Vithushan";
	private Actor mSelectedActor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_game);
		Intent i = getIntent();
		String actorJSON = i.getStringExtra("SelectedActor");
		mSelectedActor = new Gson().fromJson(actorJSON, Actor.class);
	}

	public Actor getmSelectedActor() {
		return mSelectedActor;
	}

}
