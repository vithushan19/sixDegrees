package com.vithushan.sixdegrees;

import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.model.CastResponse;
import com.vithushan.sixdegrees.model.Movie;
import com.vithushan.sixdegrees.model.MovieCredits;
import com.vithushan.sixdegrees.model.PopularPeople;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import retrofit.RestAdapter;

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
        MovieCredits movieCredits = mMovieAPIClient.getMediaForActor("18918", API_KEY);
        assertNotNull(movieCredits);
        assertNotNull(movieCredits.cast);
        assertNotEquals(movieCredits.cast.size(),0);
    }

    @Test
    public void testGetCastForTV() {
        Movie m = new Movie("8592","ASDSAD", "SADSAD");
        //TODO try with caps

        CastResponse combinedCredits = mMovieAPIClient.getCastForTV(m.id, API_KEY);
        assertNotNull(combinedCredits);
        assertNotNull(combinedCredits.cast);
        assertNotEquals(combinedCredits.cast.size(), 0);
    }

    @Test
    public void testGetCastForMovie() {
        Movie m = new Movie("21862", "SASDASD", "SDASDAS");
        //TODO try with caps

        CastResponse combinedCredits = mMovieAPIClient.getCastForMovie(m.id,API_KEY);
        assertNotNull(combinedCredits);
        assertNotNull(combinedCredits.cast);
        assertNotEquals(combinedCredits.cast.size(), 0);
    }
}

