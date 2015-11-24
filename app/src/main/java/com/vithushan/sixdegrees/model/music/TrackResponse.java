package com.vithushan.sixdegrees.model.music;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vnama on 11/20/2015.
 */
public class TrackResponse {

    @SerializedName("track")
    private Track track;

    public Track getTrack() {
        return track;
    }
}
