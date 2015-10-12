package com.vithushan.sixdegrees.util;

/**
 * Created by vnama on 10/12/2015.
 */
public interface MessageBroadcaster {

    void broadcastMessageToParticipants(byte[] msgBuf);
}
