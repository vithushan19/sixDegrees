package com.shav.therottengame.api;

import java.util.ArrayList;
import java.util.List;

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
	private List<String> mPopularActors;
	
	public TMDBClient() throws MovieDbException {
		tmdb = new TheMovieDbApi(API_KEY);
		mPopularActors = getPopularActors();
	}

	@Override
	public List<String> getMovieCast(int movieId) throws MovieDbException {

		List<Person> cast = tmdb.getMovieCasts(movieId, "").getResults();
		List<String> castNames = new ArrayList<String>();
		for (Person person : cast) {
			castNames.add(person.getName());
		}

		return castNames;
	}

	@Override
	public List<String> getMoviesForActor(int actorId) throws MovieDbException {
		List<PersonCredit> movies = tmdb.getPersonCredits(actorId, "")
				.getResults();
		List<String> movieNames = new ArrayList<String>();
		for (PersonCredit movie : movies) {
			movieNames.add(movie.getMovieTitle());
		}

		return movieNames;

	}

	public List<String> getPopularActors() throws MovieDbException {
		List<Person> popularPeople = tmdb.getPersonPopular().getResults();
		List<String> popularPeopleNames = new ArrayList<String>();
		for (Person popularPerson : popularPeople) {
			popularPeopleNames.add(popularPerson.getName());
			Log.d("VITHUSHAN", popularPerson.getName());
		}
		
		return popularPeopleNames;
	}
	
	// TODO: Rand this
	@Override
	public String getFirstActor() {
	    return mPopularActors.get(0);
	}
	
	@Override
	public String getLastActor() {
		return mPopularActors.get(1);
		
	}
	
}
