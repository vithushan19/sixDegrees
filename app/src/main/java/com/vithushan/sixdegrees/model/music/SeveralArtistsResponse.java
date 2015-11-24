package com.vithushan.sixdegrees.model.music;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vnama on 11/23/2015.
 */
public class SeveralArtistsResponse {

    @SerializedName("artists")
    List<Artist> artists;

    public List<Artist> getItems() {
        return artists;
    }
}
