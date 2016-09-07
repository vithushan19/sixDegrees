package com.vithushan.sixdegrees.maingame;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vithushan.sixdegrees.GameApplication;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.adapter.RecyclerViewAdapter;
import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.dagger.ApplicationComponent;
import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.CastResponse;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.model.Movie;
import com.vithushan.sixdegrees.model.MovieCredits;
import com.vithushan.sixdegrees.util.CircleTransform;
import com.vithushan.sixdegrees.util.Constants;
import com.vithushan.sixdegrees.util.DividerItemDecoration;
import com.vithushan.sixdegrees.util.NavigationUtils;
import com.vithushan.sixdegrees.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Vithushan on 7/5/2015.
 */
public class MainGameFragment extends Fragment implements MainGameViewBinder, RecyclerViewAdapter.ItemClickListener {
    public static final String SELECTED_ACTOR = "SelectedActor";
    public static final String OPP_SELECTED_ACTOR_ID = "OppSelectedActorId";
    private ArrayList<IHollywoodObject> mCurrentList;
    private Stack<IHollywoodObject> mHistory;
    private int mClickCount;

    private ProgressDialog mProgress;
    private TextView mStartingActortv;
    private TextView mEndingActortv;
    private ImageView mStartingImageView;
    private ImageView mEndingImageView;

    private AdView mAdView;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Inject
    IMovieAPIClient mAPIClient;

    private MainGamePresenter mPresenter;
    private boolean mIsRefreshing = false;

    protected ApplicationComponent getApplicationComponent() {
        return ((GameApplication)getActivity().getApplication()).getApplicationComponent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_game, container, false);
        getApplicationComponent().inject(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mStartingActortv = (TextView) view.findViewById(R.id.textViewStarting);
        mEndingActortv = (TextView) view.findViewById(R.id.textViewEnding);
        mStartingImageView = (ImageView) view.findViewById(R.id.imageview_starting_actor);
        mEndingImageView = (ImageView) view.findViewById(R.id.imageview_ending_actor);
        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("347FE137FE7B045143C25D3825D6BEA3")
                .build();
        mAdView.loadAd(adRequest);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Loading");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        mCurrentList = new ArrayList<>();

        mClickCount = 0;

        mHistory = new Stack<>();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // The actor that you chose
        final String myActorJSONString = getArguments().getString(SELECTED_ACTOR);
        final Actor mySelectedActor = new Gson().fromJson(myActorJSONString, Actor.class);

        // The actor your opponent chose
        final String oppSelectedActorId = String.valueOf(getArguments().getInt(OPP_SELECTED_ACTOR_ID));

        mPresenter = new MainGamePresenter(this, mAPIClient, mySelectedActor, oppSelectedActorId);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Drawable divider = getActivity().getDrawable(R.drawable.divider);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));

        // specify an adapter (see also next example)
        mAdapter = new RecyclerViewAdapter(mCurrentList,getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);

        mPresenter.loadFirstAndLastActors();

    }

    public void onItemClick(IHollywoodObject obj) {
        if (mPresenter.shouldWinGame(obj)) {
            mPresenter.winGame();
            return;
        }

        mPresenter.handleItemSelect(obj);
    }

    public void handleBackPress() {
        mPresenter.handleBackPress();
    }

    @Override
    public void showAlertDialog(final IHollywoodObject last) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle("Quit?");

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to quit the game?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        NavigationUtils.gotoSplashFragment(getActivity());
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        // false alarm add the last object back on
                        mHistory.push(last);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void updateListWithActors(Movie movie) {
        Subscriber<Actor> actorSubscriber = new Subscriber<Actor>() {
            @Override
            public void onCompleted() {

                //TODO: keep the actors in order

                mIsRefreshing = true;
                mAdapter.removeAll();
                mAdapter.refreshWithNewList(mCurrentList);
                mIsRefreshing = false;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Actor object) {
                mCurrentList.add(object);
            }
        };

        mCurrentList.clear();
        Observable<CastResponse> cast =  mAPIClient.getCastForMovie(movie.getId(),Constants.API_KEY);
        cast.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<CastResponse, List<Actor>>() {
                    @Override
                    public List<Actor> call(CastResponse castResponse) {
                        return castResponse.cast;
                    }
                })
                .flatMap(new Func1<List<Actor>, Observable<Actor>>() {
                    @Override
                    public Observable<Actor> call(List<Actor> actors) {
                        return Observable.from(actors);
                    }
                })
                .subscribe(actorSubscriber);
    }

    @Override
    public void updateListWithMovies(Actor actor) {
        Subscriber<Movie> movieSubscriber = new Subscriber<Movie>() {
            @Override
            public void onCompleted() {

                ArrayList<Movie> mediaList = new ArrayList<>();
                ArrayList<IHollywoodObject> resultList = new ArrayList<>();

                for (IHollywoodObject m : mCurrentList) {
                    Movie movie = (Movie) m;
                    mediaList.add(movie);
                }

                //Sort movies alphabetically
                Collections.sort(mediaList);
                for (Movie m : mediaList) {
                    resultList.add(m);
                }

                mCurrentList = resultList;
                mIsRefreshing = true;
                mAdapter.removeAll();
                mAdapter.refreshWithNewList(resultList);
                mIsRefreshing = false;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Movie object) {
                mCurrentList.add(object);
            }
        };

        mCurrentList.clear();
        Observable<MovieCredits> movieCredits = mAPIClient.getMediaForActor(actor.getId(), Constants.API_KEY);
        movieCredits.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<MovieCredits, List<Movie>>() {
                    @Override
                    public List<Movie> call(MovieCredits credits) {
                        return credits.cast;
                    }
                })
                .flatMap(new Func1<List<Movie>, Observable<Movie>>() {
                    @Override
                    public Observable<Movie> call(List<Movie> movies) {
                        return Observable.from(movies);
                    }
                })
                .subscribe(movieSubscriber);
    }

    private void bindAnchorActorToView(Actor result, TextView actorTextView, ImageView actorImageView ) {
        actorTextView.setText(result.getName());

        if (StringUtil.isEmpty(result.getImageURL())) {
            Picasso.with(getActivity()).load(R.drawable.movie_placeholder).into(actorImageView);
        } else {
            Picasso.with(getActivity()).load(result.getImageURL()).transform(new CircleTransform()).into(actorImageView);
        }
    }

    @Override
    public void navigateToGameOver(Stack<IHollywoodObject> mHistory) {
        IHollywoodObject[] historyArr = new IHollywoodObject[mHistory.size()];
        mHistory.toArray(historyArr);

        // We only want the ids
        String[] historyIdsArr = new String[historyArr.length];
        for (int i=0; i<historyArr.length; i++) {
            historyIdsArr[i] = historyArr[i].getId();
        }
        // Pass the id list of the our (winning) history
        NavigationUtils.gotoGameOverFragment(getActivity(), true, historyIdsArr);
    }

    @Override
    public void showLoading() {
        mProgress.show();
    }

    @Override
    public void hideLoading() {
        mProgress.hide();
    }

    @Override
    public void bindStartingActor(Actor actor) {
        bindAnchorActorToView(actor, mStartingActortv, mStartingImageView);
    }

    @Override
    public void bindEndingActor(Actor actor) {
        bindAnchorActorToView(actor, mEndingActortv, mEndingImageView);
    }
}
