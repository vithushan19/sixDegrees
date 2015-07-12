package com.vithushan.sixdegrees;

import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.fragment.MainGameFragment;
import com.vithushan.sixdegrees.fragment.SelectActorFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

/**
 * Created by Vithushan on 7/5/2015.
 */

@Module(
        injects = {MainGameFragment.class, SelectActorFragment.class}
)

public class GameModule {
    @Provides @Singleton
    IMovieAPIClient provideMovieAPIClient() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .build();

        return restAdapter.create(IMovieAPIClient.class);
    }
}
