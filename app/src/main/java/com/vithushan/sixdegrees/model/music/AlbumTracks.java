package com.vithushan.sixdegrees.model.music;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vnama on 11/22/2015.
 */
public class AlbumTracks {

    @SerializedName("items")
    List<Track> items;

    public List<Track> getItems() {
        return items;
    }
}
