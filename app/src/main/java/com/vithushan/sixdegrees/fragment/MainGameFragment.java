package com.vithushan.sixdegrees.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vithushan.sixdegrees.GameApplication;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.adapter.RecycleViewAdapter;
import com.vithushan.sixdegrees.animator.SlideAnimator;
import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.Cast;
import com.vithushan.sixdegrees.model.MovieCredits;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.model.Movie;
import com.vithushan.sixdegrees.util.Constants;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import javax.inject.Inject;

import com.vithushan.sixdegrees.util.StringUtil;

/**
 * Created by Vithushan on 7/5/2015.
 */
public class MainGameFragment extends Fragment implements RecycleViewAdapter.ItemClickListener {
    private ArrayList<IHollywoodObject> mCurrentList;
    private Stack<IHollywoodObject> mHistory;
    private Actor mStartingActor;
    private Actor mEndingActor;
    private int mClickCount;

    private ProgressBar mProgress;
    private TextView mStartingActortv;
    private TextView mEndingActortv;
    private ImageView mStartingImageView;
    private ImageView mEndingImageView;

    private RecyclerView mRecyclerView;
    private RecycleViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Inject
    IMovieAPIClient mAPIClient;
    private boolean mIsRefreshing = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_game, container,false);
        ((GameApplication) getActivity().getApplication()).inject(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mStartingActortv = (TextView) view.findViewById(R.id.textViewStarting);
        mEndingActortv = (TextView) view.findViewById(R.id.textViewEnding);
        mStartingImageView = (ImageView) view.findViewById(R.id.imageview_starting_actor);
        mEndingImageView = (ImageView) view.findViewById(R.id.imageview_ending_actor);
        mProgress = (ProgressBar) view.findViewById(R.id.progressDialog);

        mCurrentList = new ArrayList<IHollywoodObject>();

        mClickCount = 0;

        mHistory = new Stack<IHollywoodObject>();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // The actor that you chose
        final String myActorJSONString = getArguments().getString("SelectedActor");
        final Actor mySelectedActor = new Gson().fromJson(myActorJSONString, Actor.class);

        // The actor your opponent chose
        final String oppSelectedActorId = String.valueOf(getArguments().getInt("OppSelectedActorId"));
        new AsyncTask<Void, Void, Actor>() {

            @Override
            protected void onPreExecute() {
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected Actor doInBackground(Void... params) {

                Actor oppSelectedActor = mAPIClient.getActor(oppSelectedActorId, Constants.API_KEY);

                return oppSelectedActor;
            }

            @Override
            protected void onPostExecute(Actor actor) {


                boolean isHost = ((GameActivity)getActivity()).getIsHost();

                if (isHost) {
                    setupStartingActor(mySelectedActor);
                    setupEndingActor(actor);
                } else {
                    setupStartingActor(actor);
                    setupEndingActor(mySelectedActor);
                }
                mProgress.setVisibility(View.GONE);
            }

            //TODO Duplicate setup code
            private void setupStartingActor(Actor result) {
                mStartingActor = result;
                new NetworkTask().execute(mStartingActor);
                mStartingActortv.setText(mStartingActor.getName());

                if (StringUtil.isEmpty(mStartingActor.getImageURL())) {
                    mStartingImageView.setImageResource(R.drawable.question_mark);
                } else {
                    //TODO check if getbasebcontext part is needed
                    Picasso.with(getActivity().getBaseContext()).load(mStartingActor.getImageURL()).into(mStartingImageView);
                }
            }

            private void setupEndingActor(Actor result) {
                mEndingActor = result;
                mEndingActortv.setText(mEndingActor.getName());

                if (StringUtil.isEmpty(mEndingActor.getImageURL())) {
                    mEndingImageView.setImageResource(R.drawable.question_mark);
                } else {
                    Picasso.with(getActivity().getBaseContext()).load(mEndingActor.getImageURL()).into(mEndingImageView);
                }
            }
        }.execute();

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(new SlideAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);

        // specify an adapter (see also next example)
        mAdapter = new RecycleViewAdapter(mCurrentList,getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    // TODO change this to a postgame fragment
    protected void winGame() {
        // Broadcast your selection to other player(s)
        // TODO save scores/win record
        mHistory.push(mEndingActor);

        // Broadcast our (winning) history to the other player
        ((GameActivity)getActivity()).broadcastGameOver(mHistory);

        IHollywoodObject[] historyArr = new IHollywoodObject[mHistory.size()];
        mHistory.toArray(historyArr);

        // We only want the ids
        String[] historyIdsArr = new String[historyArr.length];
        for (int i=0; i<historyArr.length; i++) {
            historyIdsArr[i] = historyArr[i].getId();
        }
        // Pass the id list of the our (winning) history
        ((GameActivity) getActivity()).gotoGameOverFragment(true, historyIdsArr);
    }

    public void onItemClick(IHollywoodObject obj) {
        String text = obj.getName();
        if (text.equals(mEndingActor.getName())) {
            winGame();
            return;
        }

        new NetworkTask().execute(obj);
    }

    public void handleBackPress() {
        final IHollywoodObject last = mHistory.pop();

        //If we're at the beginning, ask if player wants to leave the game
        if (mHistory.size() == 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            // set title
            alertDialogBuilder.setTitle("Quit?");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure you want to quit the game?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            ((GameActivity)getActivity()).leaveRoom();
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
        } else {
            //Go to previous IHollywooObject
            IHollywoodObject obj = mHistory.pop();
            new NetworkTask().execute(obj);
        }

    }

    public Actor getStartingActor() {
        return mStartingActor;
    }

    public Actor getEndingActor() {
        return mEndingActor;
    }

    public Stack<IHollywoodObject> getHistory() {
        return mHistory;
    }

    private class NetworkTask extends
            AsyncTask<IHollywoodObject, Void, ArrayList<IHollywoodObject>> {

        protected ArrayList<IHollywoodObject> doInBackground(IHollywoodObject... params) {

            IHollywoodObject obj = params[0];

            try {
                mHistory.push(obj);
                if (obj instanceof Actor) {
                    Actor actor = (Actor) obj;
                    MovieCredits res =  mAPIClient.getMediaForActor(actor.getId(),Constants.API_KEY);
                    ArrayList<Movie> mediaList = new ArrayList<>();
                    ArrayList<IHollywoodObject> resList = new ArrayList<>();
                    for (Movie m : res.cast) {
                        mediaList.add(m);
                    }
                    //Sort movies alphabetically
                    Collections.sort(mediaList);
                    for (Movie m : mediaList) {
                        resList.add(m);
                    }
                    Log.d("FINISHED", "TTT");
                    return resList;
                } else if (obj instanceof Movie){
                    Movie movie = (Movie) obj;
                    Cast res = null;
                    res =  mAPIClient.getCastForMovie(obj.getId(),Constants.API_KEY);

                    ArrayList<IHollywoodObject> resList = new ArrayList<>();
                    for (Actor m : res.cast) {
                        resList.add(m);
                    }

                   return resList;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(ArrayList<IHollywoodObject> result) {
            mCurrentList = result;
            mIsRefreshing = true;

            mAdapter.removeAll();
            mAdapter.refreshWithNewList(mCurrentList);
            mIsRefreshing = false;
        }
    }

}
