package com.vithushan.sixdegrees.googleListeners;

import android.util.Log;

import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.util.NavigationUtils;
import com.vithushan.sixdegrees.util.SixDegreesUtils;

/**
 * Created by vnama on 10/10/2015.
 */

public class RoomUpdateListenerImpl implements RoomUpdateListener {

    private static final String TAG = RoomUpdateListenerImpl.class.getName();

    private GameActivity mActivity;

    public RoomUpdateListenerImpl(GameActivity activity) {
        mActivity = activity;
    }

    // Called when we've successfully left the room (this happens a result of voluntarily leaving
    // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        // we have left the room; return to main screen.
        Log.d(TAG, "onLeftRoom, code " + statusCode);
        NavigationUtils.gotoSplashFragment(mActivity);
    }

    // Called when room has been created
    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            mActivity.showGameError();
            return;
        }

        // show the waiting room UI
        mActivity.showWaitingRoom(room);
    }

    // Called when room is fully connected.
    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            mActivity.showGameError();
            return;
        }
        mActivity.updateRoom(room);
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            mActivity.showGameError();
            return;
        }

        // show the waiting room UI
        mActivity.showWaitingRoom(room);
    }

}
