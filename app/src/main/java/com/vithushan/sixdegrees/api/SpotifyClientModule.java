package com.vithushan.sixdegrees.api;

import com.vithushan.sixdegrees.model.SeveralTracksResponse;
import com.vithushan.sixdegrees.model.music.AlbumsForArtistResponse;
import com.vithushan.sixdegrees.model.music.Artist;
import com.vithushan.sixdegrees.model.music.SeveralAlbumsResponse;
import com.vithushan.sixdegrees.model.music.SeveralArtistsResponse;
import com.vithushan.sixdegrees.model.music.TopTracksResponse;
import com.vithushan.sixdegrees.util.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by vnama on 11/19/2015.
 */

@Singleton
public class SpotifyClientModule {

    private ISpotifyAPIClient mClient;

    @Inject
    public SpotifyClientModule (ISpotifyAPIClient client) {
        mClient = client;
    }

    public Observable<TopTracksResponse> getTopTracks() {
        return mClient.getTopTracks(Constants.TOP_TRACKS_PLAYLIST_ID);
    }

    public Observable<Artist> getArtist(String a) {
        return mClient.getArtist(a);
    }

    public Observable<AlbumsForArtistResponse> getAlbumsForArtist (String artistId) {
        String albumType = "album,single,appears_on";
        return mClient.getAlbumsForArtist(artistId, albumType);
    }

    public Observable<SeveralAlbumsResponse> getAlbums (String albumIds) {
        return mClient.getAlbums(albumIds);
    }

    public Observable<SeveralArtistsResponse> getArtists(String artistsIds) {
        return mClient.getArtists(artistsIds);
    }

    public Observable<SeveralTracksResponse> getTracks(String trackIds) {
        return mClient.getTracks(trackIds);
    }
}
