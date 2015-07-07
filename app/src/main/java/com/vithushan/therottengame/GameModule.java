package com.vithushan.therottengame;

import com.vithushan.therottengame.activity.GameActivity;
import com.vithushan.therottengame.activity.SelectActorActivity;
import com.vithushan.therottengame.api.MovieAPIClient;
import com.vithushan.therottengame.api.TMDBClient;
import com.vithushan.therottengame.fragment.MainGameFragment;
import com.vithushan.therottengame.fragment.SelectActorFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Vithushan on 7/5/2015.
 */

@Module(
        injects = {MainGameFragment.class, SelectActorFragment.class}
)

public class GameModule {
    @Provides @Singleton MovieAPIClient provideMovieAPIClient() {
        return new TMDBClient();
    }
}
