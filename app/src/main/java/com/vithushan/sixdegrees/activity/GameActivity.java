package com.vithushan.sixdegrees.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.fragment.GameOverFragment;
import com.vithushan.sixdegrees.fragment.MainGameFragment;
import com.vithushan.sixdegrees.fragment.SelectActorFragment;
import com.vithushan.sixdegrees.fragment.SplashFragment;
import com.vithushan.sixdegrees.util.MessageBroadcastUtils;
import com.vithushan.sixdegrees.util.MessageBroadcaster;
import com.vithushan.sixdegrees.util.NavigationUtils;
import com.vithushan.sixdegrees.util.SixDegreesUtils;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import dagger.ActivityComponent;
import dagger.DaggerActivityComponent;
import dagger.GameActivityModule;

public class GameActivity extends FragmentActivity implements MessageBroadcaster {

    public interface onOppSelectedActorSetListener {
        void onSet();
    }

	private String TAG = "Vithushan";

	// Request codes for the UIs that we show with startActivityForResult:
	final static int RC_SELECT_PLAYERS = 10000;
	final static int RC_INVITATION_INBOX = 10001;
	final static int RC_WAITING_ROOM = 10002;

	// Request code used to invoke sign in user interactions.
	final static  int RC_SIGN_IN = 9001;

	// Client used to interact with Google APIs.
	@Inject
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

    @Inject protected RoomUpdateListener mRoomUpdateListener;
    @Inject protected RoomStatusUpdateListener mSixDegreesRoomStatusUpdateListener;
    @Inject protected OnInvitationReceivedListener mInvitationReceivedListener;
    @Inject protected RealTimeMessageReceivedListener mRealTimeMessageReceivedListener;

    private ProgressDialog mProgress;

    private ActivityComponent mActivityComponent;

	/*
		LIFECYCLE METHODS
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mActivityComponent = DaggerActivityComponent.builder().gameActivityModule(new GameActivityModule(GameActivity.this)).build();

        mActivityComponent.inject(this);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

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
        SixDegreesUtils.keepScreenOn(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mGoogleApiClient.disconnect();
        SixDegreesUtils.stopKeepingScreenOn(this);
	}

    @Override
    public void onBackPressed() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (frag instanceof MainGameFragment) {
            ((MainGameFragment) frag).handleBackPress();
        } else if (frag instanceof GameOverFragment) {
            leaveRoom();
        } else if (frag instanceof SelectActorFragment) {
            leaveRoom();
        }
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
                mProgress.hide();
                if (responseCode == Activity.RESULT_OK) {
                    // ready to start playing
                    Log.d(TAG, "Starting game (waiting room returned OK).");
                    startGame(true);
                } else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player indicated that they want to leave the room
                    leaveRoom();
                } else if (responseCode == Activity.RESULT_CANCELED) {
                    // Dialog was cancelled (user pressed back key, for instance). In our game,
                    // this means leaving the room too. In more elaborate games, this could mean
                    // something else (like minimizing the waiting room UI).
                    leaveRoom();
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


    @Override
    public void broadcastMessageToParticipants(byte[] msgBuf) {
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
            INVITATION
     */

    //TODO implement invitations properly
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
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(mRoomUpdateListener);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(mRealTimeMessageReceivedListener);
        rtmConfigBuilder.setRoomStatusUpdateListener(mSixDegreesRoomStatusUpdateListener);
        if (autoMatchCriteria != null) {
            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }

        SixDegreesUtils.keepScreenOn(this);
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }


    // Accept the given invitation.
    public void acceptInviteToRoom(String invId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(mRoomUpdateListener);
        roomConfigBuilder.setInvitationIdToAccept(invId)
                .setMessageReceivedListener(mRealTimeMessageReceivedListener)
                .setRoomStatusUpdateListener(mSixDegreesRoomStatusUpdateListener);
        SixDegreesUtils.keepScreenOn(this);
        Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
        NavigationUtils.gotoSelectActorFragment(this);
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


    public void registerInvitationListener() {
        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        Games.Invitations.registerInvitationListener(mGoogleApiClient, mInvitationReceivedListener);
    }

    public void connectClient() {
        mGoogleApiClient.connect();
    }

    public void connectToRoom(Room room) {
        // get room ID, participants and my ID:
        mRoomId = room.getRoomId();
        mParticipants = room.getParticipants();
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));

        // print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + mRoomId);
        Log.d(TAG, "My ID " + mMyId);
        Log.d(TAG, "<< CONNECTED TO ROOM>>");
    }

    public void disconnectFromRoom(Room room) {
        mRoomId = null;
        Fragment frag = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (frag instanceof GameOverFragment) {

        } else {
            showGameError();
        }
    }

    public void connectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
        }

        //gotoSplashFragment();
    }


    /*
            HELPERS
     */

    public boolean getIsHost() {
        return mHost;
    }

    public boolean getIsMultiplayer() {
        return mMultiplayer;
    }

    public void handleRematch() {
        if (mMultiplayer) {
            MessageBroadcastUtils.broadcastRematchRequest(this);
        } else {
            NavigationUtils.gotoSelectActorFragment(this);
        }
    }
    public void askForRematch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Rematch?");

        // set dialog message
        alertDialogBuilder
                .setMessage("Your opponent has requested a rematch. Would you like to accept?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        NavigationUtils.gotoSelectActorFragment(GameActivity.this);
                        MessageBroadcastUtils.broadcastRematchAccepted(GameActivity.this);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        MessageBroadcastUtils.broadcastRematchDeclined(GameActivity.this);
                        Fragment frag = getFragmentManager().findFragmentById(R.id.fragment_container);
                        if (frag instanceof GameOverFragment) {
                            ((GameOverFragment)frag).setmRematchDisabled();
                        }
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    // Should only be called one enough players are in the room
    public void startGame(boolean multiplayer) {
        mMultiplayer = multiplayer;

        if (multiplayer) {
            selectHost();
        }
        NavigationUtils.gotoSelectActorFragment(this);
    }


    // Show error message about game being cancelled and return to main screen.
    public void showGameError() {
        Dialog dialog = BaseGameUtils.makeSimpleDialog(this, getString(R.string.game_problem));
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                SixDegreesUtils.stopKeepingScreenOn(GameActivity.this);
                NavigationUtils.gotoSplashFragment(GameActivity.this);
            }
        });
        dialog.show();
    }

    public void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
        }
    }

    public void showInvitationInbox() {
        // launch the intent to show the invitation inbox screen
        Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_INVITATION_INBOX);
    }

    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    public void showWaitingRoom(Room room) {

        // Number of players required to start the game
        final int MIN_PLAYERS = 0;
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);

        // show waiting room UI
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    // Leave the room.
    public void leaveRoom() {
        Log.d(TAG, "Leaving room.");
        SixDegreesUtils.stopKeepingScreenOn(this);
        if (mRoomId != null) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, mRoomUpdateListener, mRoomId);
            mRoomId = null;
        } else {
            NavigationUtils.gotoSplashFragment(this);
        }
    }

    //TODO make first person choose random host and broadcast that id
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

    public void invitePlayers() {
        //TODO support more than 2player
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 1);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    public void startQuickGame() {
        mProgress.show();
        createGameRoom();
    }

    private void createGameRoom() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(mRoomUpdateListener);
        rtmConfigBuilder.setMessageReceivedListener(mRealTimeMessageReceivedListener);
        rtmConfigBuilder.setRoomStatusUpdateListener(mSixDegreesRoomStatusUpdateListener);
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);

        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "CONNECTED");
        } else {
            Log.d(TAG, "NOT CONNECTED");
            mGoogleApiClient.connect();
        }
        Games.RealTimeMultiplayer.create(this.mGoogleApiClient, rtmConfigBuilder.build());
    }







}
