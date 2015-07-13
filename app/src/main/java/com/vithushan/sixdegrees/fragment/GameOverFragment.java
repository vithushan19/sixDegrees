package com.vithushan.sixdegrees.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.example.games.basegameutils.BaseGameUtils;
import com.vithushan.sixdegrees.GameApplication;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.adapter.ListViewAdapter;
import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.util.Constants;

import java.util.ArrayList;

import javax.inject.Inject;


public class GameOverFragment extends Fragment {
	private TextView mStateTextView;
	private Button mRematch;
    private Button mMainMenu;
    private ListViewAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgress;

    private String [] mWinningHistory;
    private boolean mWonGame;

    @Inject
    IMovieAPIClient mAPIClient;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_game_over, container, false);
        ((GameApplication) getActivity().getApplication()).inject(this);

        mRematch = (Button) view.findViewById(R.id.button_rematch);
        mRematch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRematch.setEnabled(false);
                mRematch.setText("Waiting for response...");
                ((GameActivity)getActivity()).broadcastRematchRequest();
            }
        });

        mMainMenu = (Button) view.findViewById(R.id.button_main_menu);
        mMainMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameActivity)getActivity()).leaveRoom();
            }
        });

        mStateTextView = (TextView) view.findViewById(R.id.textview_status);
        mStateTextView.setSelected(true);
		mWonGame = getArguments().getBoolean("Won");
		if (mWonGame) {
				mStateTextView.setText("YOU WON");
		} else {
			mStateTextView.setText("YOU LOST");
		}

        mWinningHistory = getArguments().getStringArray("History");

        mProgress = (ProgressBar) view.findViewById(R.id.progressDialog);
        mListView = (ListView) view.findViewById(android.R.id.list);

		return view;
	}

    @Override
    public void onResume() {
        super.onResume();

        new AsyncTask<Void, Void, ArrayList<IHollywoodObject>>() {

            @Override
            protected void onPreExecute() {
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected ArrayList<IHollywoodObject> doInBackground(Void... params) {

                // Get all the actor/movie info using the passed in ids
                ArrayList<IHollywoodObject> resultList = new ArrayList<IHollywoodObject>(mWinningHistory.length);

                for (int i=0; i<mWinningHistory.length; i++) {
                    IHollywoodObject item = null;
                    if ((i%2)==0) {
                        item = mAPIClient.getActor(mWinningHistory[i], Constants.API_KEY);
                    } else {
                        item = mAPIClient.getMovie(mWinningHistory[i], Constants.API_KEY);
                    }
                    resultList.add(item);
                }


                return resultList;
            }

            @Override
            protected void onPostExecute(ArrayList<IHollywoodObject> objects) {
                mAdapter = new ListViewAdapter(getActivity(), objects);
                mListView.setAdapter(mAdapter);
                mProgress.setVisibility(View.GONE);
            }

        }.execute();
    }

    public void showRematchDeclined() {
        mRematch.setText("Opponent Declined");
        BaseGameUtils.makeSimpleDialog(getActivity(), "Your opponenet has declined a rematch");
    }

    public void setmRematchDisabled() {
        mRematch.setEnabled(false);
    }

}
