package com.vithushan.sixdegrees;

import com.vithushan.sixdegrees.api.ISpotifyAPIClient;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import retrofit.RestAdapter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

public class SpotifyAPIClientTest {

    @Inject
    ISpotifyAPIClient ISpotifyAPIClient;

    @Before
    public void setUp() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.spotify.com/v1")
                .build();

        ISpotifyAPIClient = restAdapter.create(ISpotifyAPIClient.class);
    }

    @Test
    public void testGetTopTracks() {



    }
}

