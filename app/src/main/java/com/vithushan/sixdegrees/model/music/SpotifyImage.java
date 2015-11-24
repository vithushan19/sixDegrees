package com.vithushan.sixdegrees.model.music;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vnama on 11/20/2015.
 */
public class SpotifyImage {

    @SerializedName("height")
    private Number height;

    @SerializedName("url")
    private String url;

    @SerializedName("width")
    private Number width;

    public Number getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

    public Number getWidth() {
        return width;
    }
}
