package com.vithushan.sixdegrees.fragment;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
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

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vithushan.sixdegrees.GameApplication;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.adapter.ListViewAdapter;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.Cast;
import com.vithushan.sixdegrees.model.CombinedCredits;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.model.MediaModel;
import com.vithushan.sixdegrees.util.Constants;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import com.vithushan.sixdegrees.util.StringUtil;

/**
 * Created by Vithushan on 7/5/2015.
 */
public class MainGameFragment extends ListFragment {
    private List<IHollywoodObject> mCurrentList;
    private Stack<IHollywoodObject> mHistory;
    private Actor mStartingActor;
    private Actor mEndingActor;
    private int mClickCount;

    private ProgressBar mProgress;
    private TextView mStartingActortv;
    private TextView mEndingActortv;
    private ImageView mStartingImageView;
    private ImageView mEndingImageView;

    private ListView mListView;
    private ListViewAdapter mAdapter;

    @Inject
    IMovieAPIClient mAPIClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_game, container,false);
        ((GameApplication) getActivity().getApplication()).inject(this);

        mListView = (ListView) view.findViewById(android.R.id.list);
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


        mAdapter = new ListViewAdapter(this.getActivity(), mCurrentList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String text = ((IHollywoodObject) parent
                                .getItemAtPosition(position)).getName();
                        IHollywoodObject obj = ((IHollywoodObject) parent
                                .getItemAtPosition(position));
                        if (text.equals(mEndingActor.getName())) {
                            winGame();
                            return;
                        }

                        new NetworkTask().execute(obj);
                    }
                }
        );
    }

    // TODO change this to a postgame fragment
    protected void winGame() {
        // Broadcast your selection to other player(s)
        // TODO save scores/win record
        ((GameActivity)getActivity()).broadcastGameOver();
        ((GameActivity)getActivity()).gotoGameOverFragment(true);
    }

    public void handleBackPress() {
        final IHollywoodObject last = mHistory.pop();
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

            IHollywoodObject obj = mHistory.pop();
            new NetworkTask().execute(obj);
        }

    }

    // Called when we receive a real-time message from the network.
    // Messages in our game are made up of 2 bytes: the first one is 'F' or 'U'
    // indicating
    // whether it's a final or interim score. The second byte is the score.
    // There is also the
    // 'S' message, which indicates that the game should start.


    private class NetworkTask extends
            AsyncTask<IHollywoodObject, Void, List<IHollywoodObject>> {
        @Override
        protected void onPreExecute() {
            mListView.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);

        };

        protected List<IHollywoodObject> doInBackground(IHollywoodObject... params) {

            IHollywoodObject obj = params[0];

            try {
                mHistory.push(obj);
                if (obj instanceof Actor) {
                    Actor actor = (Actor) obj;
                    CombinedCredits res =  mAPIClient.getMediaForActor(actor.getId(),Constants.API_KEY);
                    ArrayList<MediaModel> mediaList = new ArrayList<>();
                    ArrayList<IHollywoodObject> resList = new ArrayList<>();
                    for (MediaModel m : res.cast) {
                        mediaList.add(m);
                    }
                    Collections.sort(mediaList);
                    for (MediaModel m : mediaList) {
                        resList.add(m);
                    }
                    return resList;
                } else if (obj instanceof MediaModel){
                    MediaModel mediaModel = (MediaModel) obj;
                    Cast res = null;
                    if (mediaModel.type.equals(MediaModel.MediaType.movie)) {
                        res =  mAPIClient.getCastForMovie(obj.getId(),Constants.API_KEY);
                    } else if (mediaModel.type.equals(MediaModel.MediaType.tv)) {
                        res = mAPIClient.getCastForTV(obj.getId(), Constants.API_KEY);
                    }

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

        protected void onPostExecute(List<IHollywoodObject> result) {
            mCurrentList = result;
            mAdapter.replaceAndRefreshData(mCurrentList);
            mProgress.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }
}
