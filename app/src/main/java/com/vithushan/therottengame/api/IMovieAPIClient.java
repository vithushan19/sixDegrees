package com.vithushan.therottengame.api;

import java.util.List;

import com.vithushan.therottengame.model.Actor;
import com.vithushan.therottengame.model.IHollywoodObject;

public interface IMovieAPIClient {

	List<IHollywoodObject> getCastForMedia(int mediaID);

	List<IHollywoodObject> getMediaForActor(int actorId);

	Actor getFirstActor();

	Actor getLastActor();

    List<Actor> getPopularActors();

}
