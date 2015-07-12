package com.vithushan.sixdegrees.model;

import com.google.gson.annotations.SerializedName;
import com.vithushan.sixdegrees.util.StringUtil;

/**
 * Created by vnama on 7/8/2015.
 */
public class MediaModel implements IHollywoodObject, Comparable<MediaModel> {

    public String id;
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
        if (!StringUtil.isEmpty(name)) {
            return name;
        }
        return title;
    }

    @Override
    public String getImageURL() {
        String res =  "http://image.tmdb.org/t/p/w154" + poster_path;
        return res;
    }

    public enum MediaType {
        @SerializedName("movie")
        movie,
        @SerializedName("tv")
        tv
    }

    public int compareTo(MediaModel other) {
        return (getName().compareTo(other.getName()));
    }
}
