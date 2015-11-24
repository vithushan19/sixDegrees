package com.vithushan.sixdegrees.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.vithushan.sixdegrees.api.SpotifyClientModule;
import com.vithushan.sixdegrees.dagger.ApplicationComponent;
import com.vithushan.sixdegrees.model.SeveralTracksResponse;
import com.vithushan.sixdegrees.model.movie.Actor;
import com.vithushan.sixdegrees.model.IGameObject;
import com.vithushan.sixdegrees.model.movie.Movie;
import com.vithushan.sixdegrees.model.music.Artist;
import com.vithushan.sixdegrees.model.music.SeveralArtistsResponse;
import com.vithushan.sixdegrees.model.music.Track;
import com.vithushan.sixdegrees.util.Constants;
import com.vithushan.sixdegrees.util.NavigationUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
    private ArrayList <IGameObject> mResultList;

    @Inject IMovieAPIClient mAPIClient;
    @Inject SpotifyClientModule spotifyClientModule;

    protected ApplicationComponent getApplicationComponent() {
        return ((GameApplication) getActivity().getApplication()).getApplicationComponent();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_game_over, container, false);
        getApplicationComponent().inject(this);

        mRematch = (Button) view.findViewById(R.id.button_rematch);
        mRematch.setOnClickListener(v -> {
            NavigationUtils.gotoSelectActorFragment(getActivity());
        });

        mMainMenu = (Button) view.findViewById(R.id.button_main_menu);
        mMainMenu.setOnClickListener(v -> {
            NavigationUtils.gotoSplashFragment(getActivity());
        });

        mStateTextView = (TextView) view.findViewById(R.id.textview_status);
        mStateTextView.setSelected(true);
        mStateTextView.setText("YOU WON");

        mWinningHistory = getArguments().getStringArray("History");
        Log.d("VITHUSHAN", "GameOverFrag has received this winning history array");
        for (String id : mWinningHistory) {
            Log.d("VITHUSHAN", id);
        }

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
        mAdapter = new RecyclerViewAdapter(new ArrayList<>(),getActivity(),null);
        mRecyclerView.setAdapter(mAdapter);

        // Get all the actor/movie info using the passed in ids
        mResultList = new ArrayList<>(mWinningHistory.length);

        if (((GameActivity)getActivity()).getGameType().equals(Constants.MUSIC_GAME_TYPE)) {
            displayWinningHistoryForMusic();
        } else {
            displayWinningHistoryForMovies();
        }

 }

    private void displayWinningHistoryForMusic() {
        ArrayList<String> artists = new ArrayList<>();
        ArrayList<String> tracks = new ArrayList<>();

        for (int i=0; i<mWinningHistory.length; i++) {
            if (i%2 == 0) {
                artists.add(mWinningHistory[i]);
            } else {
                tracks.add(mWinningHistory[i]);
            }
        }

        StringBuilder artistIds = new StringBuilder();
        for (String artistId : artists) {
            artistIds.append(artistId).append(',');
        }
        artistIds.deleteCharAt(artistIds.length() - 1);


        StringBuilder trackIds = new StringBuilder();
        for (String trackId : tracks) {
            trackIds.append(trackId).append(',');
        }
        trackIds.deleteCharAt(trackIds.length() - 1);

        Observable<Artist> artistObservable = spotifyClientModule.getArtists(artistIds.toString())
                .map(severalArtistsResponse -> severalArtistsResponse.getItems())
                .flatMap(artists1 -> Observable.from(artists1));


        Observable<Track> trackObservable = spotifyClientModule.getTracks(trackIds.toString())
                .map(severalTracksResponse -> severalTracksResponse.getItems())
                .flatMap(tracks1 -> Observable.from(tracks1));


        Observable.zip(artistObservable, trackObservable, (artist, track) -> new Pair<>(artist, track))
                .subscribe(new Subscriber<Pair<Artist, Track>>() {
                    @Override
                    public void onCompleted() {
                        artistObservable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(artist -> {
                                    mProgress.setVisibility(View.GONE);
                                    mAdapter.removeAll();
                                    mAdapter.refreshWithNewList(mResultList);
                                });

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Pair<Artist, Track> artistTrackPair) {
                        mResultList.add(artistTrackPair.first);
                        mResultList.add(artistTrackPair.second);
                    }
                });

    }

    private void displayWinningHistoryForMovies() {
        Subscriber<String> historySubscriber = new Subscriber<String>() {


            int index = 0;
            Observable<Actor> _actor;
            Observable<Movie> _movie;

            final Func2<Actor, Movie, Pair<Actor, Movie>> zipFunc = (actor, movie) -> new Pair<> (actor, movie);

            final Subscriber<Pair<Actor, Movie>> zipSubscriber = new Subscriber<Pair<Actor, Movie>>() {

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Pair<Actor, Movie> actorMoviePair) {
                    mResultList.add(actorMoviePair.first);
                    mResultList.add(actorMoviePair.second);

                    for (IGameObject objectId : mResultList) {
                        Log.d("VITHUSHAN", "HistorySubscriber: " + objectId.toString());
                    }

                }
            };

            @Override
            public void onCompleted() {
                _actor.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(actor -> {
                            mResultList.add(actor);
                            mProgress.setVisibility(View.GONE);
                            mAdapter.removeAll();
                            mAdapter.refreshWithNewList(mResultList);
                        });
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
