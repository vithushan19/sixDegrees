package com.vithushan.therottengame.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.vithushan.therottengame.R;

/**
 * Created by vnama on 7/9/2015.
 */
public class SplashActivity extends BaseActivity {

    private Button mQuickGame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mQuickGame = (Button) findViewById(R.id.quick_game_button);
        mQuickGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startQuickGame();
            }
        });
    }

    void startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        //switchToScreen(R.id.screen_wait);
        //keepScreenOn();
        //resetGameVars();
        Games.RealTimeMultiplayer.create(this.mGoogleApiClient, rtmConfigBuilder.build());
    }



    // Sets the flag to keep this screen on. It's recommended to do that during
    // the handshake when setting up a game, because if the screen turns off, the
    // game will be cancelled.
    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
