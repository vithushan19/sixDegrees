package com.vithushan.sixdegrees.dagger;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by vnama on 10/19/2015.
 */

@Scope
@Retention(RUNTIME)
public @interface PerActivity {}
