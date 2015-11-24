package com.vithushan.sixdegrees.model.music;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vnama on 11/22/2015.
 */
public class SeveralAlbumsResponse {

    @SerializedName("albums")
    List<Album> albums;

    public List<Album> getItems() {
        return albums;
    }
}
