package com.vithushan.sixdegrees.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.vithushan.sixdegrees.BuildConfig;
import com.vithushan.sixdegrees.GameApplication;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.adapter.HighlightableRecyclerViewAdapter;
import com.vithushan.sixdegrees.adapter.RecyclerViewAdapter;
import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.api.SpotifyClientModule;
import com.vithushan.sixdegrees.dagger.ApplicationComponent;
import com.vithushan.sixdegrees.model.IGameObject;
import com.vithushan.sixdegrees.model.movie.PopularPeople;
import com.vithushan.sixdegrees.model.music.TopTracksResponse;
import com.vithushan.sixdegrees.util.Constants;
import com.vithushan.sixdegrees.util.NavigationUtils;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Vithushan on 7/6/2015.
 */
public class SelectActorFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener {


    @Inject IMovieAPIClient mAPIClient;

    @Inject
    SpotifyClientModule module;

    private ArrayList<IGameObject> mPopularActorList;

    private IGameObject mMySelectedActor;

    private HighlightableRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private ProgressDialog mProgress;
    private Button mButton;
    private LinearLayoutManager mLayoutManager;

    protected ApplicationComponent getApplicationComponent() {
        return ((GameApplication)getActivity().getApplication()).getApplicationComponent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getApplicationComponent().inject(this);

        View view = inflater.inflate(R.layout.fragment_select_actors, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view_select);
        mButton = (Button) view.findViewById(R.id.submit);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Loading");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        mPopularActorList = new ArrayList<>();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mButton.setEnabled(true);
        mProgress.show();

        mAdapter = new HighlightableRecyclerViewAdapter(new ArrayList<>(), getActivity(), SelectActorFragment.this);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mButton.setOnClickListener(view -> {
            //Save your selection
            mMySelectedActor = mAdapter.getLastClickedItem();

            // If single player, we must set mOppSelectedActorId ourselves
            String randomActorId = "";
            do {
                Random r = new Random();
                int i = r.nextInt(mAdapter.getItemCount());
                randomActorId = mAdapter.getItem(i).getId();
            } while (randomActorId.equals(mMySelectedActor.getId()));

            if (BuildConfig.DEBUG) {
                NavigationUtils.gotoMainFragment(getActivity(), mMySelectedActor, mMySelectedActor.getId());
            } else {
                NavigationUtils.gotoMainFragment(getActivity(), mMySelectedActor, randomActorId);
            }
        });


        Subscriber<IGameObject> subscriber = new Subscriber<IGameObject>() {
            @Override
            public void onCompleted() {
                mProgress.hide();
                mAdapter.removeAll();
                mAdapter.refreshWithNewList(mPopularActorList);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(IGameObject actor) {
                mPopularActorList.add(actor);
            }
        };

        if ( ((GameActivity) getActivity()).getGameType().equals(Constants.MUSIC_GAME_TYPE)) {
            displayPopularArtists(subscriber);
        } else {
            displayPopularActors(subscriber);
        }
    }

    private void displayPopularArtists(Subscriber<IGameObject> subscriber) {
        Observable<TopTracksResponse> topTracks = module.getTopTracks();
        topTracks.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(topTracksResponse -> topTracksResponse.getItems())
                .flatMap(trackResponses -> Observable.from(trackResponses))
                .map(trackResponse -> trackResponse.getTrack().getTrackArtistList().get(0).getId())
                .distinct()
                .buffer(50)
                .map(strings -> {
                    StringBuilder builder = new StringBuilder();
                    for (String s : strings) {
                        builder.append(s).append(',');
                    }
                    builder.deleteCharAt(builder.length() - 1);
                    return builder.toString();
                })
                .map(s2 -> module.getArtists(s2))
                .toList()
                .flatMap(observables -> Observable.merge(observables))
                .map(severalArtistsResponse -> severalArtistsResponse.getItems())
                .flatMap(artists -> Observable.from(artists))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private void displayPopularActors(Subscriber<IGameObject> subscriber) {
        Observable<PopularPeople> popularPeople = mAPIClient.getPopularActors(Constants.API_KEY);
        popularPeople.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(popularPeople1 -> popularPeople1.results)
                .flatMap(actors -> Observable.from(actors))
                .subscribe(subscriber);
    }

    @Override
    public void onItemClick(IGameObject obj) {
        mAdapter.setLastClickedItem(obj);
        mAdapter.notifyDataSetChanged();
    }
}
