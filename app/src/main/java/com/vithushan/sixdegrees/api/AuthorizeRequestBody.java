package com.vithushan.sixdegrees.api;

/**
 * Created by vnama on 11/18/2015.
 */
public class AuthorizeRequestBody {
    private String grant_type;

    public AuthorizeRequestBody() {
        grant_type = "client_credentials";
    }
}
