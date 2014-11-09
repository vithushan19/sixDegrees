package com.shav.therottengame.api;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.Person;
import com.omertron.themoviedbapi.model.PersonCredit;
import com.shav.therottengame.model.Actor;
import com.shav.therottengame.model.IHollywoodObject;
import com.shav.therottengame.model.Movie;

public class TMDBClient implements MovieAPIClient {

	private TheMovieDbApi tmdb;
	private String API_KEY = "4e83b0a69397058d51b07371e1eb131a";
	private String BEN_AFFLECK = "880";
	private String GONE_GIRL = "210577";
	private List<Actor> mPopularActors;

	public TMDBClient() throws MovieDbException {
		tmdb = new TheMovieDbApi(API_KEY);
		mPopularActors = getPopularActors();
	}

	@Override
	public List<IHollywoodObject> getMovieCast(int movieId)
			throws MovieDbException {

		List<Person> cast = tmdb.getMovieCasts(movieId, "").getResults();
		List<IHollywoodObject> castNames = new ArrayList<IHollywoodObject>();
		for (Person person : cast) {
			URL url = tmdb.createImageUrl(person.getProfilePath(), "w92");
			Actor item = new Actor(String.valueOf(person.getId()),
					person.getName(), url.toString());
			if (!castNames.contains(item)) {
				castNames.add(item);
			}
		}

		return castNames;
	}

	@Override
	public List<IHollywoodObject> getMoviesForActor(int actorId)
			throws MovieDbException {
		List<PersonCredit> movies = tmdb.getPersonCredits(actorId, "")
				.getResults();
		List<IHollywoodObject> movieList = new ArrayList<IHollywoodObject>();
		for (PersonCredit movie : movies) {
			URL url = tmdb.createImageUrl(movie.getPosterPath(), "w92");
			Movie item = new Movie(String.valueOf(movie.getMovieId()),
					movie.getMovieTitle(), url.toString());
			if (!movieList.contains(item)) {
				movieList.add(item);
			}
		}

		return movieList;

	}

	public List<Actor> getPopularActors() throws MovieDbException {
		List<Person> popularPeople = tmdb.getPersonPopular().getResults();
		List<Actor> popularPeopleList = new ArrayList<Actor>();
		for (Person popularPerson : popularPeople) {
			String id = String.valueOf(popularPerson.getId());
			URL url = tmdb
					.createImageUrl(popularPerson.getProfilePath(), "w92");
			Actor item = new Actor(id, popularPerson.getName(), url.toString());
			
			if (!popularPeopleList.contains(item)) {
				popularPeopleList.add(item);
			}
		}

		return popularPeopleList;
	}

	public String getActorName(int id) throws MovieDbException {
		String name = tmdb.getPersonInfo(id, "").getName();
		return name;
	}

	// TODO: Rand this
	@Override
	public Actor getFirstActor() {
		return mPopularActors.get(4);
	}

	@Override
	public Actor getLastActor() {
		return mPopularActors.get(14);

	}

}
