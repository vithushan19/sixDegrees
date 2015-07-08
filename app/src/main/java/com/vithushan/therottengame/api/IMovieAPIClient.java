package com.vithushan.therottengame.api;

import java.util.List;

import com.vithushan.therottengame.model.Actor;
import com.vithushan.therottengame.model.CombinedCredits;
import com.vithushan.therottengame.model.IHollywoodObject;
import com.vithushan.therottengame.model.MediaModel;
import com.vithushan.therottengame.model.PopularPeople;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface IMovieAPIClient {

    @GET("/tv/{id}/credits")
	CombinedCredits getCastForTV(@Path("id") int mediaId, @Query("api_key") String apiKey);

    @GET("/movie/{id}/credits")
    CombinedCredits getCastForMovie(@Path("id") int movieId, @Query("api_key") String apiKey);

	@GET("/person/{id}/combined_credits")
	CombinedCredits getMediaForActor(@Path("id") int actorId, @Query("api_key") String apiKey);

	@GET("/person/popular")
	PopularPeople getPopularActors(@Query("api_key") String apiKey);

}
