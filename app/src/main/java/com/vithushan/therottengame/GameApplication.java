package com.vithushan.therottengame;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by Vithushan on 7/5/2015.
 */
public class GameApplication extends Application {

    private ObjectGraph graph;

    @Override public void onCreate() {
        super.onCreate();

        graph = ObjectGraph.create(getModules().toArray());
    }

    protected List<GameModule> getModules() {
        return Arrays.asList(
                new GameModule()
        );
    }

    public void inject(Object object) {
        graph.inject(object);
    }
}
