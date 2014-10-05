package com.shav.therottengame;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

public class RottenGoogleClient {

	static GoogleApiClient mClient = null;
	static Context c;

	private RottenGoogleClient() {
	}

	public static GoogleApiClient getInstance(Context c) {
		if (mClient != null) {
			return mClient;
		} else {
			mClient = new GoogleApiClient.Builder(c)
					.addConnectionCallbacks((ConnectionCallbacks) c)
					.addOnConnectionFailedListener((OnConnectionFailedListener) c).addApi(Plus.API)
					.addScope(Plus.SCOPE_PLUS_LOGIN).addApi(Games.API)
					.addScope(Games.SCOPE_GAMES).build();
		}
		return mClient;

	}
}
