package com.shav.therottengame.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.Person;
import com.omertron.themoviedbapi.model.PersonCredit;

public class TMDBClient implements MovieAPIClient {

	private TheMovieDbApi tmdb;
	private String API_KEY = "4e83b0a69397058d51b07371e1eb131a";
	private String BEN_AFFLECK = "880";
	private String GONE_GIRL = "210577";
	private List<BasicNameValuePair> mPopularActors;
	
	public TMDBClient() throws MovieDbException {
		tmdb = new TheMovieDbApi(API_KEY);
		mPopularActors = getPopularActors();
	}

	@Override
	public List<BasicNameValuePair> getMovieCast(int movieId) throws MovieDbException {

		List<Person> cast = tmdb.getMovieCasts(movieId, "").getResults();
		List<BasicNameValuePair> castNames = new ArrayList<BasicNameValuePair>();
		for (Person person : cast) {
			BasicNameValuePair item = new BasicNameValuePair(person.getName(), String.valueOf(person.getId()));
			castNames.add(item);
		}

		return castNames;
	}

	@Override
	public List<BasicNameValuePair> getMoviesForActor(int actorId) throws MovieDbException {
		List<PersonCredit> movies = tmdb.getPersonCredits(actorId, "")
				.getResults();
		List<BasicNameValuePair> movieList = new ArrayList<BasicNameValuePair>();
		for (PersonCredit movie : movies) {
			BasicNameValuePair item = new BasicNameValuePair(movie.getMovieTitle(), String.valueOf(movie.getMovieId()));
			movieList.add(item);
		}

		return movieList;

	}

	public List<BasicNameValuePair> getPopularActors() throws MovieDbException {
		List<Person> popularPeople = tmdb.getPersonPopular().getResults();
		List<BasicNameValuePair> popularPeopleList = new ArrayList<BasicNameValuePair>();
		for (Person popularPerson : popularPeople) {
			BasicNameValuePair item = new BasicNameValuePair(popularPerson.getName(), String.valueOf(popularPerson.getId()));
			popularPeopleList.add(item);
			Log.d("VITHUSHAN", popularPerson.getName());
		}
		
		return popularPeopleList;
	}
	
	public String getActorName (int id) throws MovieDbException {
		String name = tmdb.getPersonInfo(id, "").getName();
		return name;
	}
	
	// TODO: Rand this
	@Override
	public BasicNameValuePair getFirstActor() {
	    return mPopularActors.get(9);
	}
	
	@Override
	public BasicNameValuePair getLastActor() {
		return mPopularActors.get(16);
		
	}
	
}
