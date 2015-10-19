package dagger;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.googleListeners.ConnectionCallbacksImpl;
import com.vithushan.sixdegrees.googleListeners.OnConnectionFailedListenerImpl;
import com.vithushan.sixdegrees.googleListeners.OnInvitationReceivedListenerImpl;
import com.vithushan.sixdegrees.googleListeners.RealTimeMessageReceivedListenerImpl;
import com.vithushan.sixdegrees.googleListeners.RoomStatusUpdateListenerImpl;
import com.vithushan.sixdegrees.googleListeners.RoomUpdateListenerImpl;

/**
 * Created by vnama on 10/19/2015.
 */
@Module
public class GameActivityModule {
    private final GameActivity activity;

    public GameActivityModule(GameActivity activity) {
        this.activity = activity;
    }

    @Provides @PerActivity
    GameActivity activity() {
        return this.activity;
    }

    @Provides @PerActivity
    GoogleApiClient.ConnectionCallbacks provideConnectionCallbacks () {
        return new ConnectionCallbacksImpl(activity);
    }

    @Provides @PerActivity
    GoogleApiClient.OnConnectionFailedListener provideConnectionFailedListener() {
        return new OnConnectionFailedListenerImpl(activity);
    }

    @Provides @PerActivity
    RoomUpdateListener provideRoomUpdateListener() {
        return new RoomUpdateListenerImpl(activity);
    }

    @Provides @PerActivity
    RoomStatusUpdateListener provideRoomStatusUpdateListener() {
        return new RoomStatusUpdateListenerImpl(activity);
    }

    @Provides @PerActivity
    RealTimeMessageReceivedListener providRealTimeMessageReceivedListener() {
        return new RealTimeMessageReceivedListenerImpl(activity);
    }

    @Provides @PerActivity
    OnInvitationReceivedListener provodieInvitationReceivedListener() {
        return new OnInvitationReceivedListenerImpl(activity);
    }



    @Provides @PerActivity
    GoogleApiClient provideGoogleApiClient(GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        return new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }



}