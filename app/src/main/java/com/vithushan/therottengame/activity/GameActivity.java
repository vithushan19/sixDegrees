package com.vithushan.therottengame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.vithushan.therottengame.R;
import com.vithushan.therottengame.model.Actor;

public class GameActivity extends FragmentActivity {
	private String TAG = "Vithushan";
	private Actor mSelectedActor;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		Intent i = getIntent();
		String actorJSON = i.getStringExtra("SelectedActor");
		mSelectedActor = new Gson().fromJson(actorJSON, Actor.class);

	}



	protected void onStop() {
		super.onStop();

	};


	public Actor getmSelectedActor() {
		return mSelectedActor;
	}

}
