package com.vithushan.sixdegrees.model.music;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vnama on 11/20/2015.
 */
public class TopTracksResponse {

    @SerializedName("items")
    private List<TrackResponse> items;

    public List<TrackResponse> getItems() {
        return items;
    }

}
