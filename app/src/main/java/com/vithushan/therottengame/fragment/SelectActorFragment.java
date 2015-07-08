package com.vithushan.therottengame.fragment;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.vithushan.therottengame.GameApplication;
import com.vithushan.therottengame.R;
import com.vithushan.therottengame.adapter.ListViewAdapter;
import com.vithushan.therottengame.api.IMovieAPIClient;
import com.vithushan.therottengame.model.Actor;
import com.vithushan.therottengame.model.IHollywoodObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Vithushan on 7/6/2015.
 */
public class SelectActorFragment extends ListFragment {

    @Inject
    IMovieAPIClient mAPIClient;

    private List<IHollywoodObject> mPopularActorList;

    private ListViewAdapter mAdapter;
    private ListView mListView;

    private ProgressBar mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_actors, container,false);

        mListView = (ListView) view.findViewById(android.R.id.list);

        mProgress = (ProgressBar) view.findViewById(R.id.progressDialog);
        ((GameApplication) getActivity().getApplication()).inject(this);
        mPopularActorList = new ArrayList<IHollywoodObject>();

        new AsyncTask<Void, Void, List<Actor>>()

         {

            @Override
            protected void onPreExecute() {
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<Actor> doInBackground(Void... params) {
                List<Actor> resultList = mAPIClient.getPopularActors();
                return resultList;
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
                                //view.setSelected(true);
                            }
                        }
                );
                mProgress.setVisibility(View.GONE);
            }
        }.execute();



        return view;
    }
}
