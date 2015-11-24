package com.vithushan.sixdegrees;

import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.model.movie.CastResponse;
import com.vithushan.sixdegrees.model.movie.Movie;
import com.vithushan.sixdegrees.model.movie.MovieCredits;
import com.vithushan.sixdegrees.model.movie.PopularPeople;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import retrofit.RestAdapter;
import rx.Observable;
import rx.functions.Action1;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

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
        Observable<PopularPeople> actors = mMovieAPIClient.getPopularActors(API_KEY);
        assertNotNull(actors);

        actors.subscribe(new Action1<PopularPeople>() {
            @Override
            public void call(PopularPeople popularPeople) {
                assertNotSame(popularPeople.results.size(), 0);
            }
        });
    }

    @Test
    public void testGetCombinedCredits() {
        Observable<MovieCredits> movieCredits = mMovieAPIClient.getMediaForActor("18918", API_KEY);
        assertNotNull(movieCredits);

        movieCredits.subscribe(new Action1<MovieCredits>() {
            @Override
            public void call(MovieCredits movieCredits) {
                assertNotNull(movieCredits.cast);
                assertNotSame(movieCredits.cast.size(), 0);
            }
        });
    }

    @Test
    public void testGetCastForTV() {
        Movie m = new Movie("8592","ASDSAD", "SADSAD");
        //TODO try with caps

        CastResponse combinedCredits = mMovieAPIClient.getCastForTV(m.id, API_KEY);
        assertNotNull(combinedCredits);
        assertNotNull(combinedCredits.cast);
        assertNotSame(combinedCredits.cast.size(), 0);
    }

    @Test
    public void testGetCastForMovie() {
        Movie m = new Movie("21862", "SASDASD", "SDASDAS");
        //TODO try with caps

        Observable<CastResponse> combinedCredits = mMovieAPIClient.getCastForMovie(m.id,API_KEY);
        assertNotNull(combinedCredits);

        combinedCredits.subscribe(new Action1<CastResponse>() {
            @Override
            public void call(CastResponse castResponse) {
                assertNotNull(castResponse);
                assertNotSame(castResponse.cast.size(), 0);
            }
        });

    }
}

