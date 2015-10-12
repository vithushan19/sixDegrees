package com.vithushan.sixdegrees.googleListeners;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.WindowManager;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.vithushan.sixdegrees.activity.GameActivity;

/**
 * Created by vnama on 10/10/2015.
 */
public class OnInvitationReceivedListenerImpl implements OnInvitationReceivedListener {


    private String mIncomingInvitationId;
    private GameActivity mActivity;

    public OnInvitationReceivedListenerImpl(GameActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        mIncomingInvitationId = invitation.getInvitationId();

        Participant inviter = invitation.getInviter();
        String message = inviter.getPlayer().getDisplayName() + " has invited you to a game.";

        //TODO display inviter image
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);

        // set title
        alertDialogBuilder.setTitle("Incoming Invitation");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Accept",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        mActivity.acceptInviteToRoom(mIncomingInvitationId);
                    }
                })
                .setNegativeButton("Decline",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public void onInvitationRemoved(String s) {

    }
}
