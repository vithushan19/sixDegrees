package com.vithushan.therottengame;

import com.vithushan.therottengame.api.IMovieAPIClient;
import com.vithushan.therottengame.api.MovieAPIClient;
import com.vithushan.therottengame.api.TMDBClientI;
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
    @Provides @Singleton
    IMovieAPIClient provideMovieAPIClient() {
        return new MovieAPIClient();
    }
}
