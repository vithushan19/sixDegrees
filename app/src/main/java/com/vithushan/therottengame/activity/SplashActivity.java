package com.vithushan.therottengame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.vithushan.therottengame.R;

import java.util.ArrayList;

/**
 * Created by vnama on 7/9/2015.
 */
public class SplashActivity extends Activity {

    private String TAG = "VITHUSHAN - SPLASH";
    private Button mQuickGame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mQuickGame = (Button) findViewById(R.id.quick_game_button);
        mQuickGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getBaseContext(), GameActivity.class);
                i.putExtra("GAME_MODE", "MULTIPLAYER");
                startActivity(i);
            }
        });
    }

}
