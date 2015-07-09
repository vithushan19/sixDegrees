package com.vithushan.therottengame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import com.google.android.gms.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.vithushan.therottengame.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
		View.OnClickListener, RealTimeMessageReceivedListener,
		RoomStatusUpdateListener, RoomUpdateListener, OnInvitationReceivedListener {

	private String TAG = "Vithushan";
    int i;

	// Request codes for the UIs that we show with startActivityForResult:
	final static int RC_SELECT_PLAYERS = 10000;
	final static int RC_INVITATION_INBOX = 10001;
	final static int RC_WAITING_ROOM = 10002;



	// Request code used to invoke sign in user interactions.
	private static final int RC_SIGN_IN = 9001;

	// Client used to interact with Google APIs.
	protected GoogleApiClient mGoogleApiClient;

	// Are we currently resolving a connection failure?
	private boolean mResolvingConnectionFailure = false;

	// Has the user clicked the sign-in button?
	private boolean mSignInClicked = false;

	// Set to true to automatically start the sign in flow when the Activity starts.
	// Set to false to require the user to click the button in order to sign in.
	private boolean mAutoStartSignInFlow = true;

	// Room ID where the currently active game is taking place; null if we're
	// not playing.
	String mRoomId = null;

	// Are we playing in multiplayer mode?
	boolean mMultiplayer = false;

	// The participants in the currently active game
	ArrayList<Participant> mParticipants = null;

	// My participant ID in the currently active game
	String mMyId = null;

	// If non-null, this is the id of the invitation we received via the
	// invitation listener
	String mIncomingInvitationId = null;

	// Message buffer for sending messages
	byte[] mMsgBuf = new byte[2];


	/*
		LIFECYCLE METHODS
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the Google Api Client with access to Plus and Games
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
				.addApi(Games.API).addScope(Games.SCOPE_GAMES)
				.build();

	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mGoogleApiClient.disconnect();
	}

	@Override
	public void onActivityResult(int requestCode, int responseCode,
								 Intent intent) {
		super.onActivityResult(requestCode, responseCode, intent);

		switch (requestCode) {
			case RC_SELECT_PLAYERS:
				// we got the result from the "select players" UI -- ready to create the room
				handleSelectPlayersResult(responseCode, intent);
				break;
			case RC_INVITATION_INBOX:
				// we got the result from the "select invitation" UI (invitation inbox). We're
				// ready to accept the selected invitation:
				handleInvitationInboxResult(responseCode, intent);
				break;
			case RC_WAITING_ROOM:
				// we got the result from the "waiting room" UI.
				if (responseCode == Activity.RESULT_OK) {
					// ready to start playing
					Log.d(TAG, "Starting game (waiting room returned OK).");
					//startGame(true);
				} else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
					// player indicated that they want to leave the room
					//leaveRoom();
				} else if (responseCode == Activity.RESULT_CANCELED) {
					// Dialog was cancelled (user pressed back key, for instance). In our game,
					// this means leaving the room too. In more elaborate games, this could mean
					// something else (like minimizing the waiting room UI).
					//leaveRoom();
				}
				break;
			case RC_SIGN_IN:
				Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode="
						+ responseCode + ", intent=" + intent);
				mSignInClicked = false;
				mResolvingConnectionFailure = false;
				if (responseCode == RESULT_OK) {
					mGoogleApiClient.connect();
				} else {
					// Bring up an error dialog to alert the user that sign-in
					// failed. The R.string.signin_failure should reference an error
					// string in your strings.xml file that tells the user they
					// could not be signed in, such as "Unable to sign in."
					BaseGameUtils.showActivityResultError(this,requestCode,responseCode, R.string.signin_other_error);
				}
				break;
		}
		super.onActivityResult(requestCode, responseCode, intent);
	}

	// Handle the result of the "Select players UI" we launched when the user clicked the
	// "Invite friends" button. We react by creating a room with those players.
	private void handleSelectPlayersResult(int response, Intent data) {
		if (response != Activity.RESULT_OK) {
			Log.w(TAG, "*** select players UI cancelled, " + response);
			//switchToMainScreen();
			return;
		}

		Log.d(TAG, "Select players UI succeeded.");

		// get the invitee list
		final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
		Log.d(TAG, "Invitee count: " + invitees.size());

		// get the automatch criteria
		Bundle autoMatchCriteria = null;
		int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
		int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
		if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
			autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
					minAutoMatchPlayers, maxAutoMatchPlayers, 0);
			Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
		}

		// create the room
		Log.d(TAG, "Creating room...");
		RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
		rtmConfigBuilder.addPlayersToInvite(invitees);
		rtmConfigBuilder.setMessageReceivedListener(this);
		rtmConfigBuilder.setRoomStatusUpdateListener(this);
		if (autoMatchCriteria != null) {
			rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
		}
		//switchToScreen(R.id.screen_wait);
		//keepScreenOn();
		//resetGameVars();
		Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
		Log.d(TAG, "Room created, waiting for it to be ready...");
	}

	// Handle the result of the invitation inbox UI, where the player can pick an invitation
	// to accept. We react by accepting the selected invitation, if any.
	private void handleInvitationInboxResult(int response, Intent data) {
		if (response != Activity.RESULT_OK) {
			Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
			//switchToMainScreen();
			return;
		}

		Log.d(TAG, "Invitation inbox UI succeeded.");
		Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

		// accept invitation
		acceptInviteToRoom(inv.getInvitationId());
	}

	// Accept the given invitation.
	void acceptInviteToRoom(String invId) {
		// accept the invitation
		Log.d(TAG, "Accepting invitation: " + invId);
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
		roomConfigBuilder.setInvitationIdToAccept(invId)
				.setMessageReceivedListener(this)
				.setRoomStatusUpdateListener(this);
		//switchToScreen(R.id.screen_wait);
		//keepScreenOn();
		//resetGameVars();
		Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
	}



	/*
		CONNECTION CALLBACKS
	 */

	@Override
	public void onClick(View view) {

	}

	@Override
	public void onConnected(Bundle bundle) {
        i = 0;
	}


	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (mResolvingConnectionFailure) {
			// already resolving
			return;
		}

		// if the sign-in button was clicked or if auto sign-in is enabled,
		// launch the sign-in flow
		if (mSignInClicked || mAutoStartSignInFlow) {
			mAutoStartSignInFlow = false;
			mSignInClicked = false;
			mResolvingConnectionFailure = true;

			// Attempt to resolve the connection failure using BaseGameUtils.
			// The R.string.signin_other_error value should reference a generic
			// error string in your strings.xml file, such as "There was
			// an issue with sign-in, please try again later."
			if (!BaseGameUtils.resolveConnectionFailure(this,
					mGoogleApiClient, connectionResult,
					RC_SIGN_IN, "SIGN IN ERROR")) {
				mResolvingConnectionFailure = false;
			}
		}

		// Put code here to display the sign-in button
	}

	@Override
	public void onConnectionSuspended(int i) {
		// Attempt to reconnect
		mGoogleApiClient.connect();
	}

	@Override
	public void onInvitationReceived(Invitation invitation) {

	}

	@Override
	public void onInvitationRemoved(String s) {

	}

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {

	}

	@Override
	public void onRoomConnecting(Room room) {
        i = 0;
	}

	@Override
	public void onRoomAutoMatching(Room room) {
        i = 0;
	}

	@Override
	public void onPeerInvitedToRoom(Room room, List<String> list) {

	}

	@Override
	public void onPeerDeclined(Room room, List<String> list) {

	}

	@Override
	public void onPeerJoined(Room room, List<String> list) {

	}

	@Override
	public void onPeerLeft(Room room, List<String> list) {

	}

	@Override
	public void onConnectedToRoom(Room room) {
        i = 0;
	}

	@Override
	public void onDisconnectedFromRoom(Room room) {
        i = 0;
	}

	@Override
	public void onPeersConnected(Room room, List<String> list) {
        i = 0;
	}

	@Override
	public void onPeersDisconnected(Room room, List<String> list) {

	}

	@Override
	public void onP2PConnected(String s) {

	}

	@Override
	public void onP2PDisconnected(String s) {

	}

    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    void showWaitingRoom(Room room) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, 2);

        // show waiting room UI
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    // Show error message about game being cancelled and return to main screen.
    void showGameError() {
        BaseGameUtils.makeSimpleDialog(this, getString(R.string.game_problem));
        //TODO take back to beginning
    }


	@Override
	public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            showGameError();
            return;
        }

        // show the waiting room UI
        showWaitingRoom(room);
	}

	@Override
	public void onJoinedRoom(int i, Room room) {
        i = 0;
	}

	@Override
	public void onLeftRoom(int i, String s) {

	}

	@Override
	public void onRoomConnected(int i, Room room) {
        i = 0;
	}


}
