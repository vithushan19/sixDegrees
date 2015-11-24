package com.vithushan.sixdegrees.dagger;

import android.content.Context;

import com.vithushan.sixdegrees.GameApplication;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.api.SpotifyClientModule;
import com.vithushan.sixdegrees.fragment.GameOverFragment;
import com.vithushan.sixdegrees.fragment.MainGameFragment;
import com.vithushan.sixdegrees.fragment.SelectActorFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = GameApplicationModule.class)
public interface ApplicationComponent {
    // Field injections of any dependencies of the GameApplication
    void inject(GameApplication application);
    void inject(GameActivity activity);
    void inject(MainGameFragment fragment);
    void inject(SelectActorFragment fragment);
    void inject(GameOverFragment fragment);


    // Exported for child-components.
    Context context();
    SpotifyClientModule spotifyClientModule();
}
