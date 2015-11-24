package com.vithushan.sixdegrees.api;


import com.vithushan.sixdegrees.model.SeveralTracksResponse;
import com.vithushan.sixdegrees.model.music.AlbumsForArtistResponse;
import com.vithushan.sixdegrees.model.music.Artist;
import com.vithushan.sixdegrees.model.music.SeveralAlbumsResponse;
import com.vithushan.sixdegrees.model.music.SeveralArtistsResponse;
import com.vithushan.sixdegrees.model.music.TopTracksResponse;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface ISpotifyAPIClient {

    @GET("/users/spotify/playlists/{id}/tracks")
    Observable<TopTracksResponse> getTopTracks(@Path("id") String playlistId);

    @GET("/artists/{id}")
    Observable<Artist> getArtist(@Path("id") String artistId);

    @GET("/artists/{id}/albums")
    Observable<AlbumsForArtistResponse> getAlbumsForArtist(@Path("id") String id, @Query("album_type") String albumType);

    @GET("/albums")
    Observable<SeveralAlbumsResponse> getAlbums(@Query("ids") String ids);

    @GET("/artists")
    Observable<SeveralArtistsResponse> getArtists(@Query("ids") String ids);

    @GET("/tracks")
    Observable<SeveralTracksResponse> getTracks(@Query("ids") String trackIds);
}
