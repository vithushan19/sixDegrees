package com.vithushan.sixdegrees.maingame;

import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.model.Movie;
import com.vithushan.sixdegrees.util.Constants;

import java.util.Stack;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by vithushan on 9/6/16.
 */
public class MainGamePresenter {

    private final MainGameViewBinder mViewBinder;
    private final IMovieAPIClient mAPIClient;
    private final Actor mMySelectedActor;
    private final String mOppSelectedActorId;
    private Actor mStartingActor;
    private Actor mEndingActor;
    private Stack<IHollywoodObject> mHistory;

    public MainGamePresenter(MainGameViewBinder viewBinder, IMovieAPIClient apiClient, Actor mySelectedActor, String oppSelectedActorId) {
        mViewBinder = viewBinder;
        mAPIClient = apiClient;
        mMySelectedActor = mySelectedActor;
        mOppSelectedActorId = oppSelectedActorId;
        mHistory = new Stack<>();
    }


    public void loadFirstAndLastActors() {
        Action1<Actor> nextAction = new Action1<Actor>() {
            @Override
            public void call(Actor actor) {
                mStartingActor = actor;
                mEndingActor = mMySelectedActor;

                mViewBinder.bindStartingActor(mStartingActor);
                mViewBinder.bindEndingActor(mEndingActor);

                handleActorSelection(mStartingActor);
                mViewBinder.hideLoading();
            }
        };

        mViewBinder.showLoading();
        Observable<Actor> oppSelectedActor = mAPIClient.getActor(mOppSelectedActorId, Constants.API_KEY);
        oppSelectedActor.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nextAction);
    }

    private void handleActorSelection(Actor actor) {
        mHistory.push(actor);
        mViewBinder.updateListWithMovies(actor);
    }

    private void handleMovieSelection(Movie movie) {
        mHistory.push(movie);
        mViewBinder.updateListWithActors(movie);
    }

    public void handleItemSelect(IHollywoodObject obj) {
        if (obj instanceof Actor) {
            Actor actor = (Actor) obj;
            handleActorSelection(actor);
        } else if (obj instanceof Movie) {
            Movie movie = (Movie) obj;
            handleMovieSelection(movie);
        }
    }

    public boolean shouldWinGame(IHollywoodObject obj) {
        String text = obj.getName();
        return text.equals(mEndingActor.getName());
    }

    // TODO change this to a postgame fragment
    protected void winGame() {
        // Broadcast your selection to other player(s)
        // TODO save scores/win record
        mHistory.push(mEndingActor);

        mViewBinder.navigateToGameOver(mHistory);
    }

    public void handleBackPress() {
        final IHollywoodObject last = mHistory.pop();

        //If we're at the beginning, ask if player wants to leave the game
        if (mHistory.size() == 0) {
            mViewBinder.showAlertDialog(last);
        } else {
            //Go to previous IHollywooObject
            IHollywoodObject obj = mHistory.pop();
            handleItemSelect(obj);
        }
    }
}
