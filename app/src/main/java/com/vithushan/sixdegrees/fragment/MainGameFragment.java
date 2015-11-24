package com.vithushan.sixdegrees.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.adapter.RecyclerViewAdapter;
import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.api.SpotifyClientModule;
import com.vithushan.sixdegrees.dagger.ApplicationComponent;
import com.vithushan.sixdegrees.model.movie.Actor;
import com.vithushan.sixdegrees.model.music.AlbumsForArtistResponse;
import com.vithushan.sixdegrees.model.music.Artist;
import com.vithushan.sixdegrees.model.movie.CastResponse;
import com.vithushan.sixdegrees.model.IGameObject;
import com.vithushan.sixdegrees.model.movie.Movie;
import com.vithushan.sixdegrees.model.movie.MovieCredits;
import com.vithushan.sixdegrees.model.music.Track;
import com.vithushan.sixdegrees.util.Constants;
import com.vithushan.sixdegrees.util.NavigationUtils;
import com.vithushan.sixdegrees.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Vithushan on 7/5/2015.
 */
public class MainGameFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener {
    private ArrayList<IGameObject> mCurrentList;
    private Stack<IGameObject> mHistory;
    private IGameObject mStartingPerson;
    private IGameObject mEndingPerson;
    private int mClickCount;

    private ProgressDialog mProgress;
    private TextView mStartingPersonTextView;
    private TextView mEndingPersonTextView;
    private ImageView mStartingImageView;
    private ImageView mEndingImageView;

    private AdView mAdView;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Inject IMovieAPIClient mAPIClient;
    @Inject SpotifyClientModule mSpotifyClient;

    protected ApplicationComponent getApplicationComponent() {
        return ((GameApplication)getActivity().getApplication()).getApplicationComponent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_game, container, false);
        getApplicationComponent().inject(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mStartingPersonTextView = (TextView) view.findViewById(R.id.textViewStarting);
        mEndingPersonTextView = (TextView) view.findViewById(R.id.textViewEnding);
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
        final String myActorJSONString = getArguments().getString("SelectedActor");
        final String oppActorJSONString = getArguments().getString("OppSelectedActor");
        final IGameObject mySelectedActor;
        final IGameObject oppSelectedActor;

        if (((GameActivity) getActivity()).getGameType().equals(Constants.MUSIC_GAME_TYPE)) {
            mySelectedActor = new Gson().fromJson(myActorJSONString, Artist.class);
            oppSelectedActor = new Gson().fromJson(oppActorJSONString, Artist.class);
        } else {
            mySelectedActor = new Gson().fromJson(myActorJSONString, Actor.class);
            oppSelectedActor = new Gson().fromJson(oppActorJSONString, Actor.class);
        }

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Drawable gradient = getResources().getDrawable(R.drawable.blue_gradient);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // specify an adapter (see also next example)
        mAdapter = new RecyclerViewAdapter(new ArrayList<>(), getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);

        mStartingPerson = mySelectedActor;

        setupActorViews(mySelectedActor, mStartingPersonTextView, mStartingImageView);

        mEndingPerson = oppSelectedActor;
        setupActorViews(oppSelectedActor, mEndingPersonTextView, mEndingImageView);

        handleItemSelect(mStartingPerson);
    }

    //TODO Duplicate setup code
    private void setupActorViews (IGameObject result, TextView actorTextView, ImageView actorImageView ) {
        actorTextView.setText(result.getName());

        if (StringUtil.isEmpty(result.getImageURL())) {
            Picasso.with(getActivity().getBaseContext()).load(R.drawable.movie_placeholder).into(actorImageView);
        } else {
            //TODO check if getbasebcontext part is needed
            Picasso.with(getActivity().getBaseContext()).load(result.getImageURL()).into(actorImageView);
        }
    }

    // TODO change this to a postgame fragment
    protected void winGame() {
        // Broadcast your selection to other player(s)
        // TODO save scores/win record
        mHistory.push(mEndingPerson);

        IGameObject[] historyArr = new IGameObject[mHistory.size()];
        mHistory.toArray(historyArr);

        // We only want the ids
        String[] historyIdsArr = new String[historyArr.length];
        for (int i=0; i<historyArr.length; i++) {
            historyIdsArr[i] = historyArr[i].getId();
        }
        // Pass the id list of the our (winning) history
        NavigationUtils.gotoGameOverFragment(getActivity(), historyIdsArr);
    }

    public void onItemClick(IGameObject obj) {
        String text = obj.getName();
        if (text.equals(mEndingPerson.getName())) {
            winGame();
            return;
        }

        handleItemSelect(obj);
    }

    public void handleBackPress() {
        final IGameObject last = mHistory.pop();

        //If we're at the beginning, ask if player wants to leave the game
        if (mHistory.size() == 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            // set title
            alertDialogBuilder.setTitle("Quit?");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure you want to quit the game?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        dialog.cancel();
                        NavigationUtils.gotoSplashFragment(getActivity());
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        // false alarm add the last object back on
                        mHistory.push(last);
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        } else {


            //Go to previous IHollywooObject
            IGameObject obj = mHistory.pop();
            handleItemSelect(obj);
        }
    }

    private void handleItemSelect(IGameObject obj) {

        Subscriber<IGameObject> subscriber = new Subscriber<IGameObject>() {
            @Override
            public void onCompleted() {

                mProgress.hide();

                //Remove duplicates
                Set<IGameObject> s = new TreeSet<>((o1, o2) -> {
                    if (o1.getName().equals(o2.getName())) {
                        return 0;
                    } else {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                s.addAll(mCurrentList);
                mCurrentList.clear();
                mCurrentList.addAll(s);

                //Sort alphabetically
                Collections.sort(mCurrentList);

                mAdapter.removeAll();
                mAdapter.refreshWithNewList(mCurrentList);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(IGameObject object) {
                mCurrentList.add(object);
            }
        };

        mHistory.push(obj);
        mCurrentList.clear();

        if (((GameActivity)getActivity()).getGameType().equals(Constants.MUSIC_GAME_TYPE)) {
            mProgress.show();

            if (obj instanceof Artist) {
                Artist artist = (Artist) obj;
                Observable<AlbumsForArtistResponse> albumsForArtist = mSpotifyClient.getAlbumsForArtist(artist.getId());
                albumsForArtist.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(albumsForArtistResponse -> albumsForArtistResponse.getItems())
                        .flatMap(albums -> Observable.from(albums))
                        .map(album -> album.getId())
                        .buffer(50)
                        .map(strings -> {
                            StringBuilder builder = new StringBuilder();
                            for (String s : strings) {
                                builder.append(s).append(',');
                            }
                            builder.deleteCharAt(builder.length() - 1);
                            return builder.toString();
                        })
                        .map(s2 -> mSpotifyClient.getAlbums(s2))
                        .toList()
                        .flatMap(observables -> Observable.merge(observables))
                        .map(severalAlbumsResponse -> severalAlbumsResponse.getItems())
                        .flatMap(albums -> Observable.from(albums))
                        .map(album1 -> album1.setAlbumCover())
                        .map(album2 -> Observable.from(album2.getTracks().getItems()))
                        .toList()
                        .flatMap(observables1 -> Observable.merge(observables1))
                        .distinct()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
            } else if (obj instanceof Track) {
                Track track = (Track) obj;

                Observable.from(track.getTrackArtistList())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(trackArtist -> trackArtist.getId())
                        .buffer(50)
                        .map(strings -> {
                            StringBuilder builder = new StringBuilder();
                            for (String s : strings) {
                                builder.append(s).append(',');
                            }
                            builder.deleteCharAt(builder.length() - 1);
                            return builder.toString();
                        })
                        .map(s2 -> mSpotifyClient.getArtists(s2))
                        .toList()
                        .flatMap(observables -> Observable.merge(observables))
                        .map(severalArtistsResponse -> severalArtistsResponse.getItems())
                        .flatMap(artists -> Observable.from(artists))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);
            }
        } else {
            if (obj instanceof Actor) {
                Actor actor = (Actor) obj;

                Observable<MovieCredits> movieCredits = mAPIClient.getMediaForActor(actor.getId(), Constants.API_KEY);
                movieCredits.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(credits -> credits.cast)
                        .flatMap(movies -> Observable.from(movies))
                        .subscribe(subscriber);
            } else if (obj instanceof Movie) {
                Movie movie = (Movie) obj;

                Observable<CastResponse> cast = mAPIClient.getCastForMovie(movie.getId(), Constants.API_KEY);
                cast.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(castResponse -> castResponse.cast)
                        .flatMap(actors -> Observable.from(actors))
                        .subscribe(subscriber);

            }
        }
    }

}
