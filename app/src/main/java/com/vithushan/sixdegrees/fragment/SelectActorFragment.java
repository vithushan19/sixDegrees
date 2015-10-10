package com.vithushan.sixdegrees.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.vithushan.sixdegrees.GameApplication;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.adapter.HighlightableRecyclerViewAdapter;
import com.vithushan.sixdegrees.adapter.RecyclerViewAdapter;
import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.model.PopularPeople;
import com.vithushan.sixdegrees.util.Constants;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

/**
 * Created by Vithushan on 7/6/2015.
 */
public class SelectActorFragment extends Fragment implements GameActivity.onOppSelectedActorSetListener, RecyclerViewAdapter.ItemClickListener {


    @Inject
    IMovieAPIClient mAPIClient;

    private ArrayList<IHollywoodObject> mPopularActorList;

    private Actor mMySelectedActor;
    private int mOppSelectedActor = 0;

    private HighlightableRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private ProgressBar mProgress;
    private Button mButton;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((GameApplication) getActivity().getApplication()).inject(this);

        View view = inflater.inflate(R.layout.fragment_select_actors, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mButton = (Button) view.findViewById(R.id.submit);
        mProgress = (ProgressBar) view.findViewById(R.id.progressDialog);

        mPopularActorList = new ArrayList<IHollywoodObject>();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mButton.setEnabled(true);
        mProgress.setVisibility(View.INVISIBLE);

        mAdapter = new HighlightableRecyclerViewAdapter(new ArrayList<IHollywoodObject>(), getActivity(), SelectActorFragment.this);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        Drawable gradient = getResources().getDrawable(R.drawable.blue_gradient);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).drawable(gradient).size(5).build());
        mRecyclerView.setAdapter(mAdapter);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save your selection
                mMySelectedActor = (Actor) mAdapter.getLastClickedItem();

                if (((GameActivity) getActivity()).getIsMultiplayer()) {
                    // Broadcast your selection to other player(s)
                    ((GameActivity) getActivity()).broadcastSelectedActorToOpp(Integer.valueOf(mMySelectedActor.getId()));

                    // If you already have your opponenet's selection, start the mainfragment
                    if (mOppSelectedActor != 0) {
                        gotoMainFragment();
                    } else {
                        // Wait for opp selection
                        mButton.setEnabled(false);
                        mProgress.setVisibility(View.VISIBLE);
                    }
                } else {
                    // If single player, we must set mOppSelectedActor ourselves
                    String randomActorId = "";
                    do {
                        Random r = new Random();
                        int i = r.nextInt(mAdapter.getItemCount());
                        randomActorId = mAdapter.getItem(i).getId();
                    } while (randomActorId == mMySelectedActor.getId());

                    mOppSelectedActor = Integer.valueOf(randomActorId);
                    gotoMainFragment();
                }

            }
        });

        // Get and display the popular actors for selection
        new AsyncTask<Void, Void, List<Actor>>() {

            @Override
            protected void onPreExecute() {
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<Actor> doInBackground(Void... params) {
                PopularPeople resultList = mAPIClient.getPopularActors(Constants.API_KEY);
                return resultList.results;
            }

            @Override
            protected void onPostExecute(List<Actor> actors) {
                for (Actor a : actors) {
                    mPopularActorList.add(a);
                }

                mAdapter.removeAll();
                mAdapter.refreshWithNewList(mPopularActorList);
                mProgress.setVisibility(View.GONE);
            }
        }.execute();
    }

    private void gotoMainFragment() {

        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtra("SelectedActor", new Gson().toJson(mMySelectedActor));
        intent.putExtra("OppSelectedActorId", mOppSelectedActor);

        MainGameFragment fragment = new MainGameFragment();
        fragment.setArguments(intent.getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right);
        ft.replace(R.id.fragment_container, fragment).commit();
    }

    public void setOppSelectedActor(int id) {
        mOppSelectedActor = id;
    }

    @Override
    public void onSet() {
        if (this.mMySelectedActor != null) {
            mProgress.setVisibility(View.GONE);
            gotoMainFragment();
        }
    }

    @Override
    public void onItemClick(IHollywoodObject obj) {
        mAdapter.setLastClickedItem(obj);
        mAdapter.notifyDataSetChanged();

    }
}
