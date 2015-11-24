package com.vithushan.sixdegrees.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.vithushan.sixdegrees.GameApplication;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.api.SpotifyClientModule;
import com.vithushan.sixdegrees.dagger.ApplicationComponent;
import com.vithushan.sixdegrees.fragment.GameOverFragment;
import com.vithushan.sixdegrees.fragment.MainGameFragment;
import com.vithushan.sixdegrees.fragment.SplashFragment;
import com.vithushan.sixdegrees.util.Constants;
import com.vithushan.sixdegrees.util.NavigationUtils;

import javax.inject.Inject;

public class GameActivity extends FragmentActivity  {

	private String TAG = "Vithushan";

    private String mGameType;

    private ProgressDialog mProgress;

    @Inject SpotifyClientModule module;

    /*
		LIFECYCLE METHODS
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

        ApplicationComponent mApplicationComponent = ((GameApplication) getApplication()).getApplicationComponent();
        mApplicationComponent.inject(this);

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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Check if result comes from the correct activity
        if (requestCode == Constants.SPOTIFY_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response

                    Constants.ACCESS_TOKEN = response.getAccessToken();
                    setGameType(Constants.MUSIC_GAME_TYPE);
                    startGame();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }

    }

    public void handleRematch() {
            NavigationUtils.gotoSelectActorFragment(this);
    }

    public void askForRematch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Rematch?");

        // set dialog message
        alertDialogBuilder
                .setMessage("Your opponent has requested a rematch. Would you like to accept?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        NavigationUtils.gotoSelectActorFragment(GameActivity.this);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        Fragment frag = getFragmentManager().findFragmentById(R.id.fragment_container);
                        if (frag instanceof GameOverFragment) {
                            ((GameOverFragment)frag).setmRematchDisabled();
                        }
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    // Should only be called one enough players are in the room
    public void startGame() {
        NavigationUtils.gotoSelectActorFragment(this);
    }

    public void setGameType(String gameType) {
        mGameType = gameType;
    }

    public String getGameType () {
        return mGameType;
    }
}
