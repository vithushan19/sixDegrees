package com.vithushan.sixdegrees.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vithushan.sixdegrees.GameApplication;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.adapter.HighlightableRecyclerViewAdapter;
import com.vithushan.sixdegrees.adapter.RecyclerViewAdapter;
import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.dagger.ApplicationComponent;
import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.model.PopularPeople;
import com.vithushan.sixdegrees.util.Constants;
import com.vithushan.sixdegrees.util.DebugFlag;
import com.vithushan.sixdegrees.util.DividerItemDecoration;
import com.vithushan.sixdegrees.util.NavigationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Vithushan on 7/6/2015.
 */
public class SelectActorFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener {


    @Inject
    IMovieAPIClient mAPIClient;

    private ArrayList<IHollywoodObject> mPopularActorList;

    private Actor mMySelectedActor;

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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
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

        mAdapter = new HighlightableRecyclerViewAdapter(new ArrayList<IHollywoodObject>(), getActivity(), SelectActorFragment.this);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        Drawable divider = getActivity().getDrawable(R.drawable.divider);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));
        mRecyclerView.setAdapter(mAdapter);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save your selection
                mMySelectedActor = (Actor) mAdapter.getLastClickedItem();

                // If single player, we must set mOppSelectedActorId ourselves
                String randomActorId = "";
                do {
                    Random r = new Random();
                    int i = r.nextInt(mAdapter.getItemCount());
                    randomActorId = mAdapter.getItem(i).getId();
                } while (randomActorId.equals(mMySelectedActor.getId()));

                if (DebugFlag.USE_SAME_START_END_ACTORS) {
                    NavigationUtils.gotoMainFragment(getActivity(), mMySelectedActor, Integer.valueOf(mMySelectedActor.getId()));
                } else {
                    NavigationUtils.gotoMainFragment(getActivity(), mMySelectedActor, Integer.valueOf(randomActorId));
                }

            }
        });

        Subscriber<Actor> subscriber = new Subscriber<Actor>() {
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
            public void onNext(Actor actor) {
                mPopularActorList.add(actor);
            }
        };


        Observable<PopularPeople> popularPeople = mAPIClient.getPopularActors(Constants.API_KEY);
        popularPeople.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<PopularPeople, List<Actor>>() {
                    @Override
                    public List<Actor> call(PopularPeople popularPeople) {
                        return popularPeople.results;
                    }
                })
                .flatMap(new Func1<List<Actor>, Observable<Actor>>() {
                    @Override
                    public Observable<Actor> call(List<Actor> actors) {
                        return Observable.from(actors);
                    }
                })
                .subscribe(subscriber);
    }

    @Override
    public void onItemClick(IHollywoodObject obj) {
        mAdapter.setLastClickedItem(obj);
        mAdapter.notifyDataSetChanged();
    }
}
