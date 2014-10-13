package com.shav.therottengame.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.omertron.themoviedbapi.MovieDbException;
import com.shav.therottengame.ListViewAdapter;
import com.shav.therottengame.R;
import com.shav.therottengame.RottenGoogleClient;
import com.shav.therottengame.api.MovieAPIClient;
import com.shav.therottengame.api.TMDBClient;

public class GameActivity extends ListActivity implements
		RealTimeMessageReceivedListener, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {
	private String TAG = "Vithushan";

	private ListViewAdapter mAdapter;
	private ListView mListView;
	private List<String> mCurrentList;
	private String mStartingActor;
	private String mEndingActor;
	private int mClickCount;
	private RequestType mCurrentRequestType;
	private MovieAPIClient mAPIClient;
	private ProgressBar progressDialog;

	private Room mRoom;
	private GoogleApiClient mGoogleApiClient;

	// Message buffer for sending messages
	byte[] mMsgBuf = new byte[1];
	// My participant ID in the currently active game
	String mMyId = null;

	private enum RequestType {
		ACTOR, MOVIE,
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		mListView = (ListView) findViewById(android.R.id.list);
		final TextView startingActortv = (TextView) findViewById(R.id.textViewStarting);
		final TextView endingActortv = (TextView) findViewById(R.id.textViewEnding);
		mCurrentList = new ArrayList<String>();

		mClickCount = 0;
		mAdapter = new ListViewAdapter(this, mCurrentList);
		mListView.setAdapter(mAdapter);
		
		final AsyncTask<Void, Void, String> getFirstActorTask = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return mAPIClient.getFirstActor();
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				mStartingActor = result;
				startingActortv.setText(mStartingActor);
			}
			
		};
		
		final AsyncTask<Void, Void, String> getLastActorTask = new AsyncTask<Void, Void, String>() {
			
			@Override
			protected String doInBackground(Void... params) {
				return  mAPIClient.getLastActor();
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				mEndingActor = result;
				endingActortv.setText(mEndingActor);
			}
			
		};
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					mAPIClient = new TMDBClient();
				} catch (MovieDbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				getFirstActorTask.execute();
				getLastActorTask.execute();
			};

		}.execute();

		progressDialog = (ProgressBar) findViewById(R.id.progressDialog);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				String text = (String) parent.getItemAtPosition(position);
				if (text.equals(mEndingActor)) {
					winGame();
					return;
				}
				if (mCurrentRequestType == RequestType.MOVIE) {
					new NetworkTask().execute("movies", text);
					mCurrentRequestType = RequestType.ACTOR;
				} else {
					new NetworkTask().execute("actors", text);
					mCurrentRequestType = RequestType.MOVIE;
				}

			}
		});
		mGoogleApiClient = RottenGoogleClient.getInstance(this);

		Intent intent = getIntent();
		if (intent != null) {
			mRoom = intent.getParcelableExtra("Room");
		}
		new NetworkTask().execute("movies", mStartingActor);
		mCurrentRequestType = RequestType.ACTOR;

	}

	protected void winGame() {
		broadcastScore(true);
		Intent intent = new Intent(this, GameOverActivity.class);
		intent.putExtra("Won", true);
		intent.putExtra("Room", mRoom);
		startActivity(intent);
		return;
	}

	protected void onStop() {
		super.onStop();

	};

	// Called when we receive a real-time message from the network.
	// Messages in our game are made up of 2 bytes: the first one is 'F' or 'U'
	// indicating
	// whether it's a final or interim score. The second byte is the score.
	// There is also the
	// 'S' message, which indicates that the game should start.

	// Participants who sent us their final score.
	Set<String> mFinishedParticipants = new HashSet<String>();

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage rtm) {

	}

	// Broadcast my score to everybody else.
	void broadcastScore(boolean finalScore) {
		if (mRoom == null) {
			return;
		}

		// First byte in message indicates whether it's a final score or not
		mMsgBuf[0] = (byte) (finalScore ? 'F' : 'U');
		mMyId = mRoom.getParticipantId(Games.Players
				.getCurrentPlayerId(mGoogleApiClient));

		// Send to every other participant.
		for (Participant p : mRoom.getParticipants()) {
			if (p.getParticipantId().equals(mMyId))
				continue;
			if (p.getStatus() != Participant.STATUS_JOINED)
				continue;
			if (finalScore) {
				// final score notification must be sent via reliable message
				Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient,
						null, mMsgBuf, mRoom.getRoomId(), p.getParticipantId());
			} else {
				// it's an interim score notification, so we can use unreliable
				Games.RealTimeMultiplayer.sendUnreliableMessage(
						mGoogleApiClient, mMsgBuf, mRoom.getRoomId(),
						p.getParticipantId());
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	private class NetworkTask extends AsyncTask<String, Void, List<String>> {
		@Override
		protected void onPreExecute() {
			mListView.setVisibility(View.GONE);
			progressDialog.setVisibility(View.VISIBLE);
		};

		protected List<String> doInBackground(String... strings) {
			String downloadType = strings[0];
			String query = strings[1];
			int id = 0;

			try {
				if (downloadType == "movies") {
					return mAPIClient.getMoviesForActor(880);
				} else {
					return mAPIClient.getMovieCast(210577);
				}
			} catch (MovieDbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(List<String> result) {
			mCurrentList = result;
			mAdapter.replaceAndRefreshData(mCurrentList);
			progressDialog.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			mListView.setSelection(0);
		}
	}

}
