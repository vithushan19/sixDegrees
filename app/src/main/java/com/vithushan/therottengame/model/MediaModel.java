package com.vithushan.therottengame.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vnama on 7/8/2015.
 */
public class MediaModel {

    public int id;
    public String posterPath;


    public String name;

    @SerializedName("media_type")
    public MediaType type;

    public enum MediaType {
        @SerializedName("movie")
        MOVIE,
        @SerializedName("tv")
        TV
    }
}
