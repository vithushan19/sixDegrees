package com.vithushan.sixdegrees;

import android.app.Application;

import com.vithushan.sixdegrees.dagger.ApplicationComponent;
import com.vithushan.sixdegrees.dagger.DaggerApplicationComponent;
import com.vithushan.sixdegrees.dagger.GameApplicationModule;



/**
 * Created by Vithushan on 7/5/2015.
 */
public class GameApplication extends Application {

    private ApplicationComponent mApplicationComponent;

    @Override public void onCreate() {
        super.onCreate();

        mApplicationComponent = DaggerApplicationComponent.builder()
                .gameApplicationModule(new GameApplicationModule(GameApplication.this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}
