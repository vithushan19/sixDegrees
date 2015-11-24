package com.vithushan.sixdegrees.model.music;

import com.google.gson.annotations.SerializedName;
import com.vithushan.sixdegrees.model.IGameObject;

import java.util.List;

/**
 * Created by vnama on 11/20/2015.
 */
public class Artist extends IGameObject {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("images")
    private List<SpotifyImage> imageList;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImageURL() {
        //TODO implement min priority queue
        return imageList.get(imageList.size() - 2).getUrl();
    }

}
