package com.vithushan.sixdegrees.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.fragment.MainGameFragment;
import com.vithushan.sixdegrees.model.IHollywoodObject;

import java.util.Stack;

/**
 * Created by vnama on 10/12/2015.
 */
public class MessageBroadcastUtils {

    public static void broadcastRematchRequest(MessageBroadcaster broadcaster) {
        byte[] msgBuf = new byte[1];
        msgBuf[0] = 'R';
        broadcaster.broadcastMessageToParticipants(msgBuf);
    }

    public static void broadcastRematchAccepted(MessageBroadcaster broadcaster) {
        byte[] msgBuf = new byte[1];
        msgBuf[0] = 'A';
        broadcaster.broadcastMessageToParticipants(msgBuf);
    }

    public static void broadcastRematchDeclined(MessageBroadcaster broadcaster) {
        byte[] msgBuf = new byte[1];
        msgBuf[0] = 'D';
        broadcaster.broadcastMessageToParticipants(msgBuf);
    }

    public static void broadcastSelectedActorToOpp(int actorId, MessageBroadcaster broadcaster) {
        byte[] msgBuf = SixDegreesUtils.IntToByteArray(actorId);
        broadcaster.broadcastMessageToParticipants(msgBuf);
    }

    public static void broadcastGameOver(Stack<IHollywoodObject> historyStack, MessageBroadcaster broadcaster, Activity activity) {
        if (historyStack.size() != 0) {
            Fragment frag = activity.getFragmentManager().findFragmentById(R.id.fragment_container);
            if (frag instanceof MainGameFragment) {
                IHollywoodObject[] historyArray = new IHollywoodObject[historyStack.size()];
                historyStack.toArray(historyArray);
                byte[] historyByteArr = SixDegreesUtils.HollywoodListToByteArray(historyArray);
                broadcaster.broadcastMessageToParticipants(historyByteArr);
            }
        }
    }
}
