package com.vithushan.therottengame.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vnama on 7/8/2015.
 */
public class MediaModel implements IHollywoodObject {

    public int id;
    public String poster_path;


    public String name;
    public String title;

    @SerializedName("media_type")
    public MediaType type;

    @Override
    public String getId() {
        return id+"";
    }

    @Override
    public String getName() {
        if (name != null) {
            if (!name.equals(""))
            return name;
        }
        return title;
    }

    @Override
    public String getImageURL() {
        String res =  "http://image.tmdb.org/t/p/w92" + poster_path;
        return res;
    }

    public enum MediaType {
        @SerializedName("movie")
        movie,
        @SerializedName("tv")
        tv
    }
}
