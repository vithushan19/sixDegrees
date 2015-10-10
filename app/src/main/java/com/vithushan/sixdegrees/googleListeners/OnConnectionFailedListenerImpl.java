package com.vithushan.sixdegrees.googleListeners;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vithushan.sixdegrees.activity.GameActivity;

/**
 * Created by vnama on 10/10/2015.
 */
public class OnConnectionFailedListenerImpl implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = OnConnectionFailedListenerImpl.class.getName();
    private GameActivity mActivity;

    public OnConnectionFailedListenerImpl(GameActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mActivity.connectionFailed(connectionResult);
    }
}
