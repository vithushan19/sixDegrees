package com.vithushan.sixdegrees.dagger;

import android.app.Application;
import android.content.Context;

import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.api.ISpotifyAPIClient;
import com.vithushan.sixdegrees.api.SpotifyClientModule;
import com.vithushan.sixdegrees.util.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by Vithushan on 7/5/2015.
 */

/**
 * A module for Android-specific dependencies which require a {@link Context} or
 * {@link android.app.Application} to create.
 */
@Module
public class GameApplicationModule {
    private final Application application;

    public GameApplicationModule(Application application) {
        this.application = application;
    }

    /**
     * Expose the application to the graph.
     */
    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    IMovieAPIClient provideMovieAPIClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .build();

        return restAdapter.create(IMovieAPIClient.class);
    }

    @Provides
    @Singleton
    ISpotifyAPIClient provideSpotifyAPIClient() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.spotify.com/v1")
                    .setRequestInterceptor(request -> {
                        String accessToken = Constants.ACCESS_TOKEN;
                        request.addHeader("Authorization", "Bearer " + accessToken);
                    })
                .build();

        return restAdapter.create(ISpotifyAPIClient.class);

    }

    @Provides
    @Singleton
    SpotifyClientModule provideSpotifyClientModule(ISpotifyAPIClient client) {
        return new SpotifyClientModule(client);
    }

}
