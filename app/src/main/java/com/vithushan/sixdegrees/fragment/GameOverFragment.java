package com.vithushan.sixdegrees.fragment;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vithushan.sixdegrees.GameApplication;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.adapter.RecyclerViewAdapter;
import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.dagger.ApplicationComponent;
import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.model.Movie;
import com.vithushan.sixdegrees.util.Constants;
import com.vithushan.sixdegrees.util.DividerItemDecoration;
import com.vithushan.sixdegrees.util.NavigationUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;


public class GameOverFragment extends Fragment {
	private TextView mStateTextView;
	private Button mRematch;
    private Button mMainMenu;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgress;

    private String [] mWinningHistory;
    private ArrayList <IHollywoodObject> mResultList;
    private boolean mWonGame;

    @Inject
    IMovieAPIClient mAPIClient;

    protected ApplicationComponent getApplicationComponent() {
        return ((GameApplication) getActivity().getApplication()).getApplicationComponent();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_game_over, container, false);
        getApplicationComponent().inject(this);

        mRematch = (Button) view.findViewById(R.id.button_rematch);
        mRematch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRematch.setEnabled(false);
                mRematch.setText("Waiting for response...");
                ((GameActivity)getActivity()).handleRematch();
            }
        });

        mMainMenu = (Button) view.findViewById(R.id.button_main_menu);
        mMainMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.gotoSplashFragment(getActivity());
            }
        });

        mStateTextView = (TextView) view.findViewById(R.id.textview_status);
        mStateTextView.setSelected(true);
		mWonGame = getArguments().getBoolean("Won");
		if (mWonGame) {
				mStateTextView.setText("YOU WON");
		} else {
			mStateTextView.setText("YOU LOST");
		}

        mWinningHistory = getArguments().getStringArray("History");

        mProgress = (ProgressBar) view.findViewById(R.id.progressDialog);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

		return view;
	}

    @Override
    public void onResume() {
        super.onResume();

        mProgress.setVisibility(View.VISIBLE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        Drawable divider = getActivity().getDrawable(R.drawable.divider);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));
        mAdapter = new RecyclerViewAdapter(new ArrayList<IHollywoodObject>(),getActivity(),null);
        mRecyclerView.setAdapter(mAdapter);

        // Get all the actor/movie info using the passed in ids
        mResultList = new ArrayList<>(mWinningHistory.length);

        Subscriber<String> historySubscriber = new Subscriber<String>() {

            final Subscriber<Actor> finalActorSubscriber = new Subscriber<Actor>() {
                @Override
                public void onCompleted() {
                    mProgress.setVisibility(View.GONE);
                    mAdapter.removeAll();
                    mAdapter.refreshWithNewList(mResultList);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Actor actor) {
                    mResultList.add(actor);
                }
            };

            int index = 0;
            Observable<Actor> _actor;
            Observable<Movie> _movie;

            final Func2<Actor, Movie, Pair<Actor, Movie>> zipFunc = new Func2<Actor, Movie, Pair<Actor, Movie>>() {
                @Override
                public Pair<Actor, Movie> call(Actor actor, Movie movie) {
                    return new Pair<> (actor, movie);
                }
            };

            final Subscriber<Pair<Actor, Movie>> zipSubscriber = new Subscriber<Pair<Actor, Movie>>() {

                @Override
                public void onCompleted() {
                    _actor.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(finalActorSubscriber);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Pair<Actor, Movie> actorMoviePair) {
                    mResultList.add(actorMoviePair.first);
                    mResultList.add(actorMoviePair.second);
                }
            };

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String objectId) {
                if ((index % 2)==0) {
                    _actor = mAPIClient.getActor(objectId, Constants.API_KEY);
                } else {
                    _movie = mAPIClient.getMovie(objectId, Constants.API_KEY);
                    Observable.zip(_actor, _movie, zipFunc)
                    .subscribe(zipSubscriber);
                }
                index++;
            }
        };

        Observable.from(mWinningHistory)
        .subscribe(historySubscriber);
 }
}
