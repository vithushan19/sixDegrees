package com.vithushan.sixdegrees.model.music;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vnama on 11/20/2015.
 */
public class TrackArtist {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
