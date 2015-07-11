package com.vithushan.therottengame.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.vithushan.therottengame.R;
import com.vithushan.therottengame.fragment.GameOverFragment;
import com.vithushan.therottengame.fragment.MainGameFragment;
import com.vithushan.therottengame.fragment.SelectActorFragment;
import com.vithushan.therottengame.model.Actor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends BaseActivity {
	private String TAG = "Vithushan";



    public interface onOppSelectedActorSetListener {
        void onSet();
    }

    /*
        ACTIVITY CALLBACKS
     */

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_game);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            Fragment firstFragment = new Fragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }

	}

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();
        String mode = i.getStringExtra("GAME_MODE");
        if (mode.equals("MULTIPLAYER")) {
            mMultiplayer = true;
            mGoogleApiClient.connect();
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

    @Override
    public void onConnected(Bundle connectionHint) {
        createGameRoom();
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

    private void createGameRoom() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
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


    // Should only be called one enough players are in the room
    private void startGame(boolean multiplayer) {
        mMultiplayer = multiplayer;
        if (!multiplayer) return;

        selectHost();
        gotoSelectActorScreen();
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

    private void gotoSelectActorScreen() {
        // Create a new Fragment to be placed in the activity layout
        SelectActorFragment firstFragment = new SelectActorFragment();

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, firstFragment).commit();
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

    public boolean getIsHost() {
        return this.mHost;
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


}
