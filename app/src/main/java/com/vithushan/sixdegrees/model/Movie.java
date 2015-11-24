package com.vithushan.sixdegrees.model;

import com.google.gson.annotations.SerializedName;
import com.vithushan.sixdegrees.util.StringUtil;

/**
 * Created by vnama on 7/8/2015.
 */
public class Movie extends IHollywoodObject {

    public String id;
    public String poster_path;
    public String title;

    public Movie(String id, String name, String imgURL) {
        if ("http://image.tmdb.org/t/p/w92".equals(imgURL)) {
            imgURL = "";
        }
        this.id = id;
        this.title = name;
        this.poster_path = imgURL;
    }


    @Override
    public String getId() {
        return id+"";
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public String getImageURL() {
        String res =  "http://image.tmdb.org/t/p/w154" + poster_path;
        return res;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ ID: " ).append(id).append(", NAME: ").append(title).append(" }");
        return builder.toString();
    }

}
