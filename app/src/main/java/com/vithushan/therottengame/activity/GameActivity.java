package com.vithushan.therottengame.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;

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
import com.vithushan.therottengame.fragment.GameOverFragment;
import com.vithushan.therottengame.fragment.SelectActorFragment;
import com.vithushan.therottengame.fragment.SplashFragment;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends FragmentActivity implements RealTimeMessageReceivedListener {

    public interface onOppSelectedActorSetListener {
        void onSet();
    }

	private String TAG = "Vithushan";
    int i;

	// Request codes for the UIs that we show with startActivityForResult:
	final static int RC_SELECT_PLAYERS = 10000;
	final static int RC_INVITATION_INBOX = 10001;
	final static int RC_WAITING_ROOM = 10002;

	// Request code used to invoke sign in user interactions.
	final static  int RC_SIGN_IN = 9001;

	// Client used to interact with Google APIs.
	protected GoogleApiClient mGoogleApiClient;

	// Are we currently resolving a connection failure?
	protected boolean mResolvingConnectionFailure = false;

	// Has the user clicked the sign-in button?
	protected boolean mSignInClicked = false;

	// Set to true to automatically start the sign in flow when the Activity starts.
	// Set to false to require the user to click the button in order to sign in.
	protected boolean mAutoStartSignInFlow = true;

	// Room ID where the currently active game is taking place; null if we're
	// not playing.
	String mRoomId = null;

	// Are we playing in multiplayer mode?
	boolean mMultiplayer = false;

	// The participants in the currently active game
	ArrayList<Participant> mParticipants = null;

	// My participant ID in the currently active game
	String mMyId = null;
	protected boolean mHost = false;

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

		// Create the Google Api Client with access to Plus and Games
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(new RottenConnectionCallback(this))
				.addOnConnectionFailedListener(new RottenConnectionFailedListener(this))
				.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
				.addApi(Games.API).addScope(Games.SCOPE_GAMES)

				.build();

        setContentView(R.layout.activity_game);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            SplashFragment firstFragment = new SplashFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
        keepScreenOn();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mGoogleApiClient.disconnect();
        stopKeepingScreenOn();
	}


	// Accept the given invitation.
	void acceptInviteToRoom(String invId) {
		// accept the invitation
		Log.d(TAG, "Accepting invitation: " + invId);
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(new RottenRoomUpdateListener(this));
		roomConfigBuilder.setInvitationIdToAccept(invId)
				.setMessageReceivedListener(this)
				.setRoomStatusUpdateListener(new RottenRoomStatusUpdateListener());
		//switchToScreen(R.id.screen_wait);
		keepScreenOn();
		//resetGameVars();
		Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
	}

    // Show error message about game being cancelled and return to main screen.
    void showGameError() {
        BaseGameUtils.makeSimpleDialog(this, getString(R.string.game_problem));
        gotoSplashFragment();
    }

	void updateRoom(Room room) {
		if (room != null) {
			mParticipants = room.getParticipants();
		}
	}

    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    void showWaitingRoom(Room room) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, 0);

        // show waiting room UI
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    // Sets the flag to keep this screen on. It's recommended to do that during
    // the handshake when setting up a game, because if the screen turns off, the
    // game will be cancelled.
    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    private void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void startSinglePlayer() {
        mMultiplayer = false;
        gotoSelectActorFragment();
    }

    public void createGameRoom() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(new RottenRoomUpdateListener(this));
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(new RottenRoomStatusUpdateListener());
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        //switchToScreen(R.id.screen_wait);
        //resetGameVars();
        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "CONNECTED");
        } else {
            Log.d(TAG, "NOT CONNECTED");
            mGoogleApiClient.connect();
        }
        Games.RealTimeMultiplayer.create(this.mGoogleApiClient, rtmConfigBuilder.build());
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
                    startGame(true);
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
                    BaseGameUtils.showActivityResultError(this, requestCode, responseCode, R.string.signin_other_error);
                }
                break;
        }
        super.onActivityResult(requestCode, responseCode, intent);
    }

    /*
        GOOGLE CALLBACKS
    */

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        byte[] buf = rtm.getMessageData();
        String sender = rtm.getSenderParticipantId();

        // Actor message
        if (buf.length == 4) {
            int oppSelectedActorId = byteArrayToInt(buf);
            SelectActorFragment frag = (SelectActorFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
            if (frag != null) {
                frag.setOppSelectedActor(oppSelectedActorId);
                frag.onSet();
            }
        } else {
            // handle end game broadcast
            gotoGameOverFragment(false);
        }
    }

    /*
        PUBLIC METHODS
     */

    public void broadcastSelectedActorToOpp(int actorId) {
        byte[] msgBuf = IntToByteArray(actorId);
        broadcastMessageToParticipants(msgBuf);
    }

    public void broadcastGameOver() {
        byte[] msgBuf = new byte[1];
        msgBuf[0] = 'W';
        broadcastMessageToParticipants(msgBuf);
    }

    private void broadcastMessageToParticipants(byte[] msgBuf) {
        if (!mMultiplayer)
            return; // playing single-player mode

        // Send to every other participant.
        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId))
                continue;
            if (p.getStatus() != Participant.STATUS_JOINED)
                continue;

            // final score notification must be sent via reliable message
            Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, msgBuf,
                    mRoomId, p.getParticipantId());

        }
    }


    /*
        PRIVATE METHODS
     */




    // Should only be called one enough players are in the room
    private void startGame(boolean multiplayer) {
        mMultiplayer = multiplayer;
        if (!multiplayer) return;

        selectHost();
        gotoSelectActorFragment();
    }


    //TODO make first person choose random host
    private void selectHost() {
        if (mParticipants != null) {
            ArrayList<String> participantsId = new ArrayList<>();
            for (Participant p : mParticipants) {
                participantsId.add(p.getParticipantId());
            }
            Collections.sort(participantsId);
            String hostId = participantsId.get(0);
            if (mMyId.equals(hostId)) {
                mHost = true;
            }

        }
    }

    private void gotoSelectActorFragment() {
        // Create a new Fragment to be placed in the activity layout
        SelectActorFragment selectActorFragment = new SelectActorFragment();

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, selectActorFragment).commit();
    }

    public void gotoGameOverFragment(boolean won) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Won", won);

        GameOverFragment fragment = new GameOverFragment();
        fragment.setArguments(intent.getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment).commit();
    }

    private void gotoSplashFragment() {

        SplashFragment fragment = new SplashFragment();

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment).commit();
    }

    // Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.
    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
            //gotoSplashFragment();
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
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(new RottenRoomUpdateListener(this));
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(new RottenRoomStatusUpdateListener());
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
            //gotoSplashFragment();
            return;
        }

        Log.d(TAG, "Invitation inbox UI succeeded.");
        Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        acceptInviteToRoom(inv.getInvitationId());
    }

    public boolean getIsHost() {
        return this.mHost;
    }

    public boolean getIsMultiplayer() {
        return this.mMultiplayer;
    }
    private int byteArrayToInt (byte[] arr) {
        ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
        int num = wrapped.getInt(); // 1
        return num;
    }

    private byte[] IntToByteArray (int num) {
        ByteBuffer dbuf = ByteBuffer.allocate(4);
        dbuf.putInt(num);
        byte[] bytes = dbuf.array(); // { 0, 1 }
        return bytes;
    }

    private class RottenConnectionCallback implements  GoogleApiClient.ConnectionCallbacks {

        GameActivity mActivity;

        RottenConnectionCallback (GameActivity activity) {
            mActivity = activity;
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            Log.d(TAG, "onConnected() called. Sign in successful!");

            Log.d(TAG, "Sign-in succeeded.");

            // register listener so we are notified if we receive an invitation to play
            // while we are in the game
            Games.Invitations.registerInvitationListener(mGoogleApiClient, new RottenInvitationReceivedListener());

            if (connectionHint != null) {
                Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
                Invitation inv = connectionHint
                        .getParcelable(Multiplayer.EXTRA_INVITATION);
                if (inv != null && inv.getInvitationId() != null) {
                    // retrieve and cache the invitation ID
                    Log.d(TAG,"onConnected: connection hint has a room invite!");
                    acceptInviteToRoom(inv.getInvitationId());
                    return;
                }
            }
            gotoSplashFragment();

        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
            mGoogleApiClient.connect();
        }
    }

    private class RottenConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {
        GameActivity mActivity;

        RottenConnectionFailedListener (GameActivity activity) {
            mActivity = activity;
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

            if (mResolvingConnectionFailure) {
                Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
                return;
            }

            if (mSignInClicked || mAutoStartSignInFlow) {
                mAutoStartSignInFlow = false;
                mSignInClicked = false;
                mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(mActivity, mGoogleApiClient,
                        connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
            }

            //gotoSplashFragment();
        }
    }

    private class RottenRoomUpdateListener implements RoomUpdateListener {
        GameActivity mActivity;

        RottenRoomUpdateListener (GameActivity activity) {
            mActivity = activity;
        }

        // Called when we've successfully left the room (this happens a result of voluntarily leaving
        // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
        @Override
        public void onLeftRoom(int statusCode, String roomId) {
            // we have left the room; return to main screen.
            Log.d(TAG, "onLeftRoom, code " + statusCode);
            gotoSplashFragment();
        }

        // Called when room has been created
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

        // Called when room is fully connected.
        @Override
        public void onRoomConnected(int statusCode, Room room) {
            Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
            if (statusCode != GamesStatusCodes.STATUS_OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
                showGameError();
                return;
            }
            updateRoom(room);
        }

        @Override
        public void onJoinedRoom(int statusCode, Room room) {
            Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
            if (statusCode != GamesStatusCodes.STATUS_OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
                showGameError();
                return;
            }

            // show the waiting room UI
            showWaitingRoom(room);
        }

    }

    private class RottenRoomStatusUpdateListener implements RoomStatusUpdateListener {
        // We treat most of the room update callbacks in the same way: we update our list of
        // participants and update the display. In a real game we would also have to check if that
        // change requires some action like removing the corresponding player avatar from the screen,
        // etc.

        // Called when we get disconnected from the room. We return to the main screen.
        @Override
        public void onDisconnectedFromRoom(Room room) {
            mRoomId = null;
            showGameError();
        }

        // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
        // is connected yet).
        @Override
        public void onConnectedToRoom(Room room) {
            Log.d(TAG, "onConnectedToRoom.");

            // get room ID, participants and my ID:
            mRoomId = room.getRoomId();
            mParticipants = room.getParticipants();
            mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));

            // print out the list of participants (for debug purposes)
            Log.d(TAG, "Room ID: " + mRoomId);
            Log.d(TAG, "My ID " + mMyId);
            Log.d(TAG, "<< CONNECTED TO ROOM>>");
        }


        @Override
        public void onPeerDeclined(Room room, List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerInvitedToRoom(Room room, List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onP2PDisconnected(String participant) {
        }

        @Override
        public void onP2PConnected(String participant) {
        }

        @Override
        public void onPeerJoined(Room room, List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerLeft(Room room, List<String> peersWhoLeft) {
            updateRoom(room);
        }

        @Override
        public void onRoomAutoMatching(Room room) {
            updateRoom(room);
        }

        @Override
        public void onRoomConnecting(Room room) {
            updateRoom(room);
        }

        @Override
        public void onPeersConnected(Room room, List<String> peers) {
            updateRoom(room);
        }

        @Override
        public void onPeersDisconnected(Room room, List<String> peers) {
            updateRoom(room);
        }
    }

    private class RottenInvitationReceivedListener implements OnInvitationReceivedListener {
         @Override
         public void onInvitationReceived(Invitation invitation) {

         }

         @Override
         public void onInvitationRemoved(String s) {

         }
     }
}
