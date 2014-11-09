package com.shav.therottengame.api;

import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.omertron.themoviedbapi.MovieDbException;
import com.shav.therottengame.model.Actor;
import com.shav.therottengame.model.IHollywoodObject;
import com.shav.therottengame.model.Movie;
import com.shav.therottengame.util.javatuples.Triplet;

public interface MovieAPIClient {

	List<IHollywoodObject> getMovieCast(int movieId) throws MovieDbException;

	List<IHollywoodObject> getMoviesForActor(int actorId) throws MovieDbException;

	Actor getFirstActor();

	Actor getLastActor();

}
