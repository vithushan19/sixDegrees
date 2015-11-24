package com.vithushan.sixdegrees.model.music;

import com.google.gson.annotations.SerializedName;
import com.vithushan.sixdegrees.model.IGameObject;

import java.util.List;

/**
 * Created by vnama on 11/20/2015.
 */
public class Track extends IGameObject {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("artists")
    private List<TrackArtist> trackArtistList;

    @SerializedName("album")
    private Album album;

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
        if (album == null) {
            return "http://icons.iconarchive.com/icons/custom-icon-design/pretty-office-8/128/Accept-icon.png";
        } else {
            return album.getImageURL();
        }
    }

    public List<TrackArtist> getTrackArtistList() {
        return trackArtistList;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

}
