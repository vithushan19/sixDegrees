package com.vithushan.sixdegrees.model;

import com.google.gson.annotations.SerializedName;
import com.vithushan.sixdegrees.model.music.Album;
import com.vithushan.sixdegrees.model.music.Track;

import java.util.List;

/**
 * Created by vnama on 11/23/2015.
 */
public class SeveralTracksResponse {
    @SerializedName("tracks")
    List<Track> tracks;

    public List<Track> getItems() {
        return tracks;
    }
}
