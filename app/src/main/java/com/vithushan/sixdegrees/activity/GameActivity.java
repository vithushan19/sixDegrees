package com.vithushan.sixdegrees.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.dagger.ActivityComponent;
import com.vithushan.sixdegrees.dagger.DaggerActivityComponent;
import com.vithushan.sixdegrees.dagger.GameActivityModule;
import com.vithushan.sixdegrees.fragment.GameOverFragment;
import com.vithushan.sixdegrees.fragment.SelectActorFragment;
import com.vithushan.sixdegrees.fragment.SplashFragment;
import com.vithushan.sixdegrees.maingame.MainGameFragment;
import com.vithushan.sixdegrees.util.NavigationUtils;

public class GameActivity extends FragmentActivity {

	private String TAG = "Vithushan";

    private ProgressDialog mProgress;

    private ActivityComponent mActivityComponent;

	/*
		LIFECYCLE METHODS
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mActivityComponent = DaggerActivityComponent.builder().gameActivityModule(new GameActivityModule(GameActivity.this)).build();

        mActivityComponent.inject(this);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        setContentView(R.layout.activity_game);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            SplashFragment firstFragment = new SplashFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
	}

    @Override
    public void onBackPressed() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (frag instanceof MainGameFragment) {
            ((MainGameFragment) frag).handleBackPress();
        } else if (frag instanceof GameOverFragment) {
            NavigationUtils.gotoSplashFragment(this);
        } else if (frag instanceof SelectActorFragment) {
            NavigationUtils.gotoSplashFragment(this);
        }
    }


    public void handleRematch() {
        NavigationUtils.gotoSelectActorFragment(this);
    }

    // Should only be called one enough players are in the room
    public void startGame(boolean multiplayer) {
        NavigationUtils.gotoSelectActorFragment(this);
    }


}
