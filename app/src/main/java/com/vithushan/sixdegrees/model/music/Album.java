package com.vithushan.sixdegrees.model.music;

import com.google.gson.annotations.SerializedName;
import com.vithushan.sixdegrees.model.IGameObject;

import java.util.List;

/**
 * Created by vnama on 11/22/2015.
 */
public class Album extends IGameObject {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("images")
    private List<SpotifyImage> imageList;

    @SerializedName("tracks")
    private AlbumTracks tracks;

    public List<SpotifyImage> getImageList() {
        return imageList;
    }

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
        if (imageList == null || imageList.size() == 0) {
            return "http://icons.iconarchive.com/icons/custom-icon-design/pretty-office-8/128/Accept-icon.png";
        } else {
            if (imageList.size() > 1) {
                return imageList.get(imageList.size()-2).getUrl();
            } else {
                return imageList.get(imageList.size()-1).getUrl();
            }
        }
    }

    public AlbumTracks getTracks() {
        return tracks;
    }

    public Album setAlbumCover() {
        for (Track track : tracks.getItems()) {
            track.setAlbum(this);
        }
        return this;
    }

}
