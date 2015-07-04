package com.vithushan.therottengame.api;

import java.util.List;

import com.omertron.themoviedbapi.MovieDbException;
import com.vithushan.therottengame.model.Actor;
import com.vithushan.therottengame.model.IHollywoodObject;

public interface MovieAPIClient {

	List<IHollywoodObject> getMovieCast(int movieId) throws MovieDbException;

	List<IHollywoodObject> getMoviesForActor(int actorId) throws MovieDbException;

	Actor getFirstActor();

	Actor getLastActor();

}
