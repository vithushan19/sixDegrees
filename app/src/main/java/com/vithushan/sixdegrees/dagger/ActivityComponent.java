package com.vithushan.sixdegrees.dagger;

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
}