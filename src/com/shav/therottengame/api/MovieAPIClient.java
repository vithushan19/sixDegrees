package com.shav.therottengame.api;

import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.omertron.themoviedbapi.MovieDbException;

public interface MovieAPIClient {

	List<BasicNameValuePair> getMovieCast(int movieId) throws MovieDbException;

	List<BasicNameValuePair> getMoviesForActor(int actorId) throws MovieDbException;

	BasicNameValuePair getFirstActor();

	BasicNameValuePair getLastActor();

}
