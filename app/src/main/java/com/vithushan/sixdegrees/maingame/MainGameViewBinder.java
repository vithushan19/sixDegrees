package com.vithushan.sixdegrees.maingame;

import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.model.Movie;

import java.util.Stack;

/**
 * Created by vithushan on 9/6/16.
 */
public interface MainGameViewBinder {
    void showLoading();
    void hideLoading();
    void bindStartingActor(Actor actor);
    void bindEndingActor(Actor actor);
    void updateListWithActors(Movie movie);
    void updateListWithMovies(Actor actor);

    void navigateToGameOver(Stack<IHollywoodObject> mHistory);

    void showAlertDialog(IHollywoodObject last);
}
