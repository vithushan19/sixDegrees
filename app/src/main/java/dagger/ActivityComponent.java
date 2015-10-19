package dagger;

import android.app.Activity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.vithushan.sixdegrees.activity.GameActivity;

import dagger.Component;

/**
 * Created by vnama on 10/19/2015.
 */

@PerActivity
@Component(modules = GameActivityModule.class)
public interface ActivityComponent {
    void inject(GameActivity activity);

    //Exposed to sub-graphs.
    GameActivity activity();
    RoomStatusUpdateListener roomStatusUpdateListener();
    RoomUpdateListener roomUpdateListener();
    RealTimeMessageReceivedListener realTimeMessageReceivedListener();
    OnInvitationReceivedListener invitationReceivedListener();
}