package com.vithushan.therottengame;

import com.vithushan.therottengame.api.IMovieAPIClient;
import com.vithushan.therottengame.model.Cast;
import com.vithushan.therottengame.model.CombinedCredits;
import com.vithushan.therottengame.model.MediaModel;
import com.vithushan.therottengame.model.PopularPeople;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import retrofit.RestAdapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class MovieAPIClientTest {


    private String API_KEY = "4e83b0a69397058d51b07371e1eb131a";

    @Inject
    IMovieAPIClient mMovieAPIClient;

    @Before
    public void setUp() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .build();

        mMovieAPIClient = restAdapter.create(IMovieAPIClient.class);
    }

    @Test
    public void testGetPopularPeopleReturnsList() {
        PopularPeople actors = mMovieAPIClient.getPopularActors(API_KEY);
        assertNotNull(actors);
        assertNotEquals(actors.results.size(), 0);
    }

    @Test
    public void testGetCombinedCredits() {
        CombinedCredits combinedCredits = mMovieAPIClient.getMediaForActor("18918", API_KEY);
        assertNotNull(combinedCredits);
        assertNotNull(combinedCredits.cast);
        assertNotEquals(combinedCredits.cast.size(),0);
    }

    @Test
    public void testGetCastForTV() {
        MediaModel m = new MediaModel();
        //TODO try with caps
        m.type = MediaModel.MediaType.tv;
        m.id = "8592";

        Cast combinedCredits = mMovieAPIClient.getCastForTV(m.id, API_KEY);
        assertNotNull(combinedCredits);
        assertNotNull(combinedCredits.cast);
        assertNotEquals(combinedCredits.cast.size(), 0);
    }

    @Test
    public void testGetCastForMovie() {
        MediaModel m = new MediaModel();
        //TODO try with caps
        m.type = MediaModel.MediaType.movie;
        m.id = "21862";

        Cast combinedCredits = mMovieAPIClient.getCastForMovie(m.id,API_KEY);
        assertNotNull(combinedCredits);
        assertNotNull(combinedCredits.cast);
        assertNotEquals(combinedCredits.cast.size(), 0);
    }
}

