package com.vithushan.sixdegrees.googleListeners;

import android.util.Log;

import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.vithushan.sixdegrees.activity.GameActivity;

import java.util.List;

/**
 * Created by vnama on 10/10/2015.
 */

public class RoomStatusUpdateListenerImpl implements RoomStatusUpdateListener {
    // We treat most of the room update callbacks in the same way: we update our list of
    // participants and update the display. In a real game we would also have to check if that
    // change requires some action like removing the corresponding player avatar from the screen,
    // etc.

    private static final String  TAG = RoomStatusUpdateListenerImpl.class.getName();

    private GameActivity mActivity;

    public RoomStatusUpdateListenerImpl(GameActivity activity) {
        mActivity = activity;
    }

    // Called when we get disconnected from the room. We return to the main screen.
    @Override
    public void onDisconnectedFromRoom(Room room) {
        mActivity.disconnectFromRoom(room);
    }

    // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
    // is connected yet).
    @Override
    public void onConnectedToRoom(Room room) {
        Log.d(TAG, "onConnectedToRoom.");
        mActivity.connectToRoom(room);
    }


    @Override
    public void onPeerDeclined(Room room, List<String> arg1) {
        mActivity.updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> arg1) {
        mActivity.updateRoom(room);
    }

    @Override
    public void onP2PDisconnected(String participant) {
    }

    @Override
    public void onP2PConnected(String participant) {
    }

    @Override
    public void onPeerJoined(Room room, List<String> arg1) {
        mActivity.updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> peersWhoLeft) {
        mActivity.updateRoom(room);

    }

    @Override
    public void onRoomAutoMatching(Room room) {
        mActivity.updateRoom(room);
    }

    @Override
    public void onRoomConnecting(Room room) {
        mActivity.updateRoom(room);
    }

    @Override
    public void onPeersConnected(Room room, List<String> peers) {
        mActivity.updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> peers) {
        mActivity.updateRoom(room);
    }
}

