package com.vithushan.therottengame.fragment;

import android.app.ListFragment;
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
import com.vithushan.therottengame.GameApplication;
import com.vithushan.therottengame.activity.GameActivity;
import com.vithushan.therottengame.adapter.ListViewAdapter;
import com.vithushan.therottengame.R;
import com.vithushan.therottengame.api.IMovieAPIClient;
import com.vithushan.therottengame.model.Actor;
import com.vithushan.therottengame.model.Cast;
import com.vithushan.therottengame.model.CombinedCredits;
import com.vithushan.therottengame.model.IHollywoodObject;
import com.vithushan.therottengame.model.MediaModel;
import com.vithushan.therottengame.util.Constants;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.vithushan.therottengame.util.StringUtil;

/**
 * Created by Vithushan on 7/5/2015.
 */
public class MainGameFragment extends ListFragment {
    private List<IHollywoodObject> mCurrentList;
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
        ((GameActivity)getActivity()).broadcastGameOver();
        ((GameActivity)getActivity()).gotoGameOverFragment(true);
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
                if (obj instanceof Actor) {
                    Actor actor = (Actor) obj;
                    CombinedCredits res =  mAPIClient.getMediaForActor(actor.getId(),Constants.API_KEY);
                    ArrayList<IHollywoodObject> resList = new ArrayList<>();
                    for (MediaModel m : res.cast) {
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
            mListView.setSelection(0);
        }
    }
}
