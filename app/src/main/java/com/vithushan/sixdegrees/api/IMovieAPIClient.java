package com.vithushan.sixdegrees.api;


import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.CastResponse;
import com.vithushan.sixdegrees.model.Movie;

import com.vithushan.sixdegrees.model.MovieCredits;
import com.vithushan.sixdegrees.model.PopularPeople;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface IMovieAPIClient {

    @GET("/tv/{id}/credits")
    CastResponse getCastForTV(@Path("id") String mediaId, @Query("api_key") String apiKey);

    @GET("/movie/{id}/credits")
    Observable<CastResponse> getCastForMovie(@Path("id") String movieId, @Query("api_key") String apiKey);

	@GET("/person/{id}/movie_credits")
    Observable<MovieCredits> getMediaForActor(@Path("id") String actorId, @Query("api_key") String apiKey);

	@GET("/person/popular")
    Observable<PopularPeople> getPopularActors(@Query("api_key") String apiKey);

    @GET("/person/{id}")
    Observable<Actor> getActor(@Path("id") String actorId, @Query("api_key") String apiKey);

    @GET("/movie/{id}")
    Observable<Movie> getMovie(@Path("id") String movieId, @Query("api_key") String apiKey);

}
