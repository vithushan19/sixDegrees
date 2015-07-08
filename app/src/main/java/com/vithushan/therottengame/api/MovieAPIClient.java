package com.vithushan.therottengame.api;


import com.vithushan.therottengame.model.Actor;
import com.vithushan.therottengame.model.IHollywoodObject;
import com.vithushan.therottengame.model.Movie;

import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieAPIClient implements IMovieAPIClient {

	private String API_KEY = "4e83b0a69397058d51b07371e1eb131a";
	private String BEN_AFFLECK = "880";
	private String GONE_GIRL = "210577";
	private List<Actor> mPopularActors;

	public MovieAPIClient() {

    }

	@Override
	public List<IHollywoodObject> getCastForMedia(int mediaID) {

		List<IHollywoodObject> castNames = new ArrayList<IHollywoodObject>();


		return castNames;
	}

	@Override
	public List<IHollywoodObject> getMediaForActor(int actorId){

		List<IHollywoodObject> movieList = new ArrayList<IHollywoodObject>();

		return movieList;

	}

	public List<Actor> getPopularActors() {



        return this.mPopularActors;

	}

	public String getActorName(int id) {
	String name = "name";
	//	String name = tmdb.getPersonInfo(id, "").getName();
		return name;
	}

	// TODO: Rand this
	@Override
	public Actor getFirstActor() {
		return getPopularActors().get(4);
	}

	@Override
	public Actor getLastActor() {
        return  new Actor("1","Vithushan", "google.ca");
		//return mPopularActors.get(14);

	}



}
