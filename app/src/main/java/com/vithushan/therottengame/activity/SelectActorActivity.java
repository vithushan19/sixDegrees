package com.vithushan.therottengame.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.vithushan.therottengame.R;

public class SelectActorActivity extends BaseActivity {
	private String TAG = "Vithushan";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_actor);
	}

}
