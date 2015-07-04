package com.vithushan.therottengame.api;

import android.net.http.AndroidHttpClient;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.credits.CreditBasic;
import com.omertron.themoviedbapi.model.credits.CreditMovieBasic;
import com.omertron.themoviedbapi.model.credits.CreditTVBasic;
import com.omertron.themoviedbapi.model.credits.MediaCreditCast;
import com.omertron.themoviedbapi.model.person.*;
import com.vithushan.therottengame.model.Actor;
import com.vithushan.therottengame.model.IHollywoodObject;
import com.vithushan.therottengame.model.Movie;

import org.apache.http.impl.client.DefaultHttpClient;

public class TMDBClient implements MovieAPIClient {

	private TheMovieDbApi tmdb;
	private String API_KEY = "4e83b0a69397058d51b07371e1eb131a";
	private String BEN_AFFLECK = "880";
	private String GONE_GIRL = "210577";
	private List<Actor> mPopularActors;

	public TMDBClient() throws MovieDbException {
		tmdb = new TheMovieDbApi(API_KEY, new DefaultHttpClient());
		mPopularActors = getPopularActors();
	}

	@Override
	public List<IHollywoodObject> getMovieCast(int movieId)
			throws MovieDbException {

		List<MediaCreditCast> cast = tmdb.getMovieCredits(movieId).getCast();
		List<IHollywoodObject> castNames = new ArrayList<IHollywoodObject>();
		for (MediaCreditCast person : cast) {
			URL url = tmdb.createImageUrl(person.getArtworkPath(), "w92");
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
		List<CreditBasic> movies = tmdb.getPersonCombinedCredits(actorId, "").getCast();

		List<IHollywoodObject> movieList = new ArrayList<IHollywoodObject>();
		for (CreditBasic movie : movies) {
			URL url = tmdb.createImageUrl(movie.getArtworkPath(), "w92");
            String title="";
            if (movie instanceof CreditMovieBasic) {
                title = ((CreditMovieBasic)movie).getTitle();
            } else if (movie instanceof CreditTVBasic) {
                title = ((CreditTVBasic)movie).getName();
            }
			Movie item = new Movie(String.valueOf(movie.getId()),
					title, url.toString());
			if (!movieList.contains(item)) {
				movieList.add(item);
			}
		}

		return movieList;

	}

	public List<Actor> getPopularActors() throws MovieDbException {
		List<PersonFind> popularPeople = tmdb.getPersonPopular(0).getResults();
		List<Actor> popularPeopleList = new ArrayList<Actor>();
		for (PersonFind popularPerson : popularPeople) {
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
