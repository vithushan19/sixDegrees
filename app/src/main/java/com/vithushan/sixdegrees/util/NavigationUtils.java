package com.vithushan.sixdegrees.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;

import com.google.gson.Gson;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.fragment.GameOverFragment;
import com.vithushan.sixdegrees.fragment.MainGameFragment;
import com.vithushan.sixdegrees.fragment.SelectActorFragment;
import com.vithushan.sixdegrees.fragment.SplashFragment;
import com.vithushan.sixdegrees.model.IGameObject;

/**
 * Created by vnama on 10/10/2015.
 */
public class NavigationUtils {

    public static void gotoSelectActorFragment(Activity a) {
        // Create a new Fragment to be placed in the activity layout
        SelectActorFragment selectActorFragment = new SelectActorFragment();

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentManager fm = a.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right);
        ft.replace(R.id.fragment_container, selectActorFragment).commit();
    }

    public static void gotoMainFragment(Activity a, IGameObject mySelectedActor, String oppSelectedActorId) {

        Intent intent = new Intent(a, GameActivity.class);
        intent.putExtra("SelectedActor", new Gson().toJson(mySelectedActor));
        intent.putExtra("OppSelectedActorId", oppSelectedActorId);

        MainGameFragment fragment = new MainGameFragment();
        fragment.setArguments(intent.getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentManager fm = a.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right);
        ft.replace(R.id.fragment_container, fragment).commit();
    }

    public static void gotoGameOverFragment(Activity a, boolean won, String[] historyIds) {
        Intent intent = new Intent(a, GameActivity.class);
        intent.putExtra("Won", won);
        intent.putExtra("History", historyIds);

        GameOverFragment fragment = new GameOverFragment();
        fragment.setArguments(intent.getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentManager fm = a.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        ft.replace(R.id.fragment_container, fragment).commit();
    }

    public static void gotoSplashFragment(Activity a) {

        SplashFragment fragment = new SplashFragment();

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentManager fm = a.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment).commit();
    }

}
