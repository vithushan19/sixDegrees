package com.vithushan.therottengame.api;

import com.vithushan.therottengame.model.Cast;
import com.vithushan.therottengame.model.CombinedCredits;
import com.vithushan.therottengame.model.PopularPeople;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface IMovieAPIClient {

    @GET("/tv/{id}/credits")
    Cast getCastForTV(@Path("id") String mediaId, @Query("api_key") String apiKey);

    @GET("/movie/{id}/credits")
    Cast getCastForMovie(@Path("id") String movieId, @Query("api_key") String apiKey);

	@GET("/person/{id}/combined_credits")
	CombinedCredits getMediaForActor(@Path("id") String actorId, @Query("api_key") String apiKey);

	@GET("/person/popular")
	PopularPeople getPopularActors(@Query("api_key") String apiKey);

}
