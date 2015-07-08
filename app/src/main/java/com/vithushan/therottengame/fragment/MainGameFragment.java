package com.vithushan.therottengame.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vithushan.therottengame.GameApplication;
import com.vithushan.therottengame.adapter.ListViewAdapter;
import com.vithushan.therottengame.R;
import com.vithushan.therottengame.activity.GameOverActivity;
import com.vithushan.therottengame.api.IMovieAPIClient;
import com.vithushan.therottengame.model.Actor;
import com.vithushan.therottengame.model.IHollywoodObject;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Vithushan on 7/5/2015.
 */
public class MainGameFragment extends ListFragment {
    private ListViewAdapter mAdapter;
    private ListView mListView;
    private List<IHollywoodObject> mCurrentList;
    private Actor mStartingActor;
    private Actor mEndingActor;
    private int mClickCount;
    private RequestType mCurrentRequestType;
    private ProgressBar progressDialog;

    @Inject
    IMovieAPIClient mAPIClient;


    private enum RequestType {
        ACTOR, MOVIE,
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_game, container,false);
        ((GameApplication) getActivity().getApplication()).inject(this);
        mListView = (ListView) view.findViewById(android.R.id.list);
        final TextView startingActortv = (TextView) view.findViewById(R.id.textViewStarting);
        final TextView endingActortv = (TextView) view.findViewById(R.id.textViewEnding);
        final ImageView startingImageView = (ImageView) view.findViewById(R.id.imageview_starting_actor);
        final ImageView endingImageView = (ImageView) view.findViewById(R.id.imageview_ending_actor);
        mCurrentList = new ArrayList<IHollywoodObject>();

        mClickCount = 0;
        mAdapter = new ListViewAdapter(this.getActivity(), mCurrentList);
        mListView.setAdapter(mAdapter);

        final AsyncTask<Void, Void, Actor> getFirstActorTask = new AsyncTask<Void, Void, Actor>() {

            @Override
            protected Actor doInBackground(Void... params) {
                return mAPIClient.getFirstActor();
            }

            @Override
            protected void onPostExecute(Actor result) {
                super.onPostExecute(result);
                mStartingActor = result;
                new NetworkTask().execute(0,
                        Integer.valueOf(mStartingActor.getId()));
                mCurrentRequestType = RequestType.ACTOR;
                startingActortv.setText(mStartingActor.getName());

                if (isEmpty(mStartingActor.getImageURL())) {
                    startingImageView.setImageResource(R.drawable.question_mark);
                } else {
                    //TODO check if getbasebcontext part is needed
                    Picasso.with(getActivity().getBaseContext()).load(mStartingActor.getImageURL()).into(startingImageView);
                }

            }

        };

        final AsyncTask<Void, Void, Actor> getLastActorTask = new AsyncTask<Void, Void, Actor>() {

            @Override
            protected Actor doInBackground(Void... params) {
                return mAPIClient.getLastActor();
            }

            @Override
            protected void onPostExecute(Actor result) {
                super.onPostExecute(result);
                mEndingActor = result;
                endingActortv.setText(mEndingActor.getName());

                if (isEmpty(mEndingActor.getImageURL())) {
                    startingImageView.setImageResource(R.drawable.question_mark);
                } else {
                    Picasso.with(getActivity().getBaseContext()).load(mEndingActor.getImageURL()).into(endingImageView);
                }
            }

        };


        //getFirstActorTask.execute();
        //getLastActorTask.execute();

        progressDialog = (ProgressBar) view.findViewById(R.id.progressDialog);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String text = ((IHollywoodObject) parent
                        .getItemAtPosition(position)).getName();
                int objId = Integer.valueOf(((IHollywoodObject) parent
                        .getItemAtPosition(position)).getId());
                if (text.equals(mEndingActor.getName())) {
                    winGame();
                    return;
                }
                if (mCurrentRequestType == RequestType.MOVIE) {
                    new NetworkTask().execute(0, objId);
                    mCurrentRequestType = RequestType.ACTOR;
                } else {
                    new NetworkTask().execute(1, objId);
                    mCurrentRequestType = RequestType.MOVIE;
                }

            }
        });


        return view;
    }

    private boolean isEmpty(String str) {
        str = str.trim();
        if (str.equals(null)) return true;
        if (str.equals("")) return true;
        return false;
    }

    // TODO change this to a postgame fragment
    protected void winGame() {
        Intent intent = new Intent(this.getActivity(), GameOverActivity.class);
        intent.putExtra("Won", true);
        startActivity(intent);
        return;
    }

    // Called when we receive a real-time message from the network.
    // Messages in our game are made up of 2 bytes: the first one is 'F' or 'U'
    // indicating
    // whether it's a final or interim score. The second byte is the score.
    // There is also the
    // 'S' message, which indicates that the game should start.


    private class NetworkTask extends
            AsyncTask<Integer, Void, List<IHollywoodObject>> {
        @Override
        protected void onPreExecute() {
            mListView.setVisibility(View.GONE);
            progressDialog.setVisibility(View.VISIBLE);
        };

        protected List<IHollywoodObject> doInBackground(Integer... params) {
            int downloadType = params[0];
            int id = params[1];

            try {
                // 0 - movies
                // 1 - actors
                if (downloadType == 0) {
                    return mAPIClient.getMediaForActor(id);
                } else {
                    return mAPIClient.getCastForMedia(id);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(List<IHollywoodObject> result) {
            mCurrentList = result;
            mAdapter.replaceAndRefreshData(mCurrentList);
            progressDialog.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mListView.setSelection(0);
        }
    }
}
