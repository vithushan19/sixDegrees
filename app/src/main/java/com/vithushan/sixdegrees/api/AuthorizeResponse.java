package com.vithushan.sixdegrees.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vnama on 11/18/2015.
 */
public class AuthorizeResponse {

    @SerializedName("access_token")
    private String mAccessToken;

    @SerializedName("token_type")
    private String mTokenType;

    @SerializedName("expires_in")
    private Number expiresIn;

}
