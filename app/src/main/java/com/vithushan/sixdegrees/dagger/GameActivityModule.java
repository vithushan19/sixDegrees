package com.vithushan.sixdegrees.dagger;

import com.vithushan.sixdegrees.activity.GameActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by vnama on 10/19/2015.
 */
@Module
public class GameActivityModule {
    private final GameActivity activity;

    public GameActivityModule(GameActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    GameActivity activity() {
        return this.activity;
    }

}