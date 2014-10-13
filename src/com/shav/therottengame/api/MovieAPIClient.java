package com.shav.therottengame.api;

import java.util.List;

import com.omertron.themoviedbapi.MovieDbException;

public interface MovieAPIClient {

	List<String> getMovieCast(int movieId) throws MovieDbException;

	List<String> getMoviesForActor(int actorId) throws MovieDbException;

	String getFirstActor();

	String getLastActor();

}
