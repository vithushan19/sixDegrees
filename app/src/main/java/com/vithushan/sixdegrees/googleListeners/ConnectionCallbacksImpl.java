package com.vithushan.sixdegrees.googleListeners;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.util.NavigationUtils;

/**
 * Created by vnama on 10/10/2015.
 */
public class ConnectionCallbacksImpl implements  GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = ConnectionCallbacksImpl.class.getName();
    private GameActivity mActivity;

    public ConnectionCallbacksImpl(GameActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected() called. Sign in successful!");

        Log.d(TAG, "Sign-in succeeded.");

        mActivity.registerInvitationListener();

        if (connectionHint != null) {
            Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
            Invitation inv = connectionHint
                    .getParcelable(Multiplayer.EXTRA_INVITATION);
            if (inv != null && inv.getInvitationId() != null) {
                // retrieve and cache the invitation ID
                Log.d(TAG,"onConnected: connection hint has a room invite!");
                mActivity.acceptInviteToRoom(inv.getInvitationId());
                return;
            }
        }
        NavigationUtils.gotoSplashFragment(mActivity);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        mActivity.connectClient();
    }
}
