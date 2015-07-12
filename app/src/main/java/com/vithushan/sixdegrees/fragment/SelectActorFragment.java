package com.vithushan.sixdegrees.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.vithushan.sixdegrees.GameApplication;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.adapter.ListViewAdapter;
import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.model.PopularPeople;
import com.vithushan.sixdegrees.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

/**
 * Created by Vithushan on 7/6/2015.
 */
public class SelectActorFragment extends ListFragment implements GameActivity.onOppSelectedActorSetListener {


    @Inject
    IMovieAPIClient mAPIClient;

    private List<IHollywoodObject> mPopularActorList;

    private Actor mMySelectedActor;
    private int mOppSelectedActor = 0;

    private ListViewAdapter mAdapter;
    private ListView mListView;

    private ProgressBar mProgress;
    private Button mButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_select_actors, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mButton = (Button) view.findViewById(R.id.submit);
        mProgress = (ProgressBar) view.findViewById(R.id.progressDialog);

        ((GameApplication) getActivity().getApplication()).inject(this);
        mPopularActorList = new ArrayList<IHollywoodObject>();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mButton.setEnabled(true);
        mProgress.setVisibility(View.INVISIBLE);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save your selection
                int index = mAdapter.getSelectedIndex();
                mMySelectedActor = (Actor) mAdapter.getItem(index);

                // TODO disable submit button after one click

                if (((GameActivity)getActivity()).getIsMultiplayer()) {
                    // Broadcast your selection to other player(s)
                    ((GameActivity)getActivity()).broadcastSelectedActorToOpp(Integer.valueOf(mMySelectedActor.getId()));

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
                        int i1 = r.nextInt(mAdapter.getCount());
                        randomActorId = mAdapter.getItem(i1).getId();
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

                mAdapter = new ListViewAdapter(getActivity(), mPopularActorList);

                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                mAdapter.setSelectedIndex(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                );

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
}
