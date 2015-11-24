package com.vithushan.sixdegrees.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.tumblr.backboard.Actor;
import com.tumblr.backboard.MotionProperty;
import com.tumblr.backboard.imitator.Imitator;
import com.tumblr.backboard.imitator.MotionImitator;
import com.tumblr.backboard.imitator.ToggleImitator;
import com.tumblr.backboard.performer.MapPerformer;
import com.vithushan.sixdegrees.BuildConfig;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.animator.LogoAnimator;
import com.vithushan.sixdegrees.api.ISpotifyAPIClient;
import com.vithushan.sixdegrees.util.Constants;
import com.vithushan.sixdegrees.view.CirclesLogo;

import java.util.ArrayList;

/**
 * Created by vnama on 7/9/2015.
 */
public class SplashFragment extends Fragment {

    private static final int DIAMETER = 75;
    private static final int RING_DIAMETER = 7 * DIAMETER;

    private static final int OPEN = 1;
    private static final int CLOSED = 0;

    private String TAG = "SplashFragment";

    private CirclesLogo mCirclesLogo;
    private RelativeLayout mTransparentBackground;
    private Button mOpenButton;
    private Button mMusicGame;
    private Button mMovieGame;

    private ArrayList<View> mButtons;

    private static final String REDIRECT_URI = "yourcustomprotocol://callback";
    private static final String CLIENT_ID = "adac294af526489ebf054368992d8350";

    private ISpotifyAPIClient mClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash, container, false);

        mCirclesLogo = (CirclesLogo) rootView.findViewById(R.id.circles);
        mTransparentBackground = (RelativeLayout) rootView.findViewById(R.id.transparent_background);

        mOpenButton = (Button) rootView.findViewById(R.id.open_menu_button);

        mMusicGame = (Button) rootView.findViewById(R.id.music_game_button);
        mMusicGame.setOnClickListener(view -> {

            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);


            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(getActivity(), Constants.SPOTIFY_REQUEST_CODE, request);
        });

        mMovieGame = (Button) rootView.findViewById(R.id.button_movie_game);
        mMovieGame.setOnClickListener(view -> {

            ((GameActivity) getActivity()).setGameType(Constants.MOVIE_GAME_TYPE);
            ((GameActivity) getActivity()).startGame();
        });

        if (BuildConfig.DEBUG) {
        }

        mButtons = new ArrayList<>();
        mButtons.add(mMovieGame);
        mButtons.add(mMusicGame);

        /* Animations! */

        final SpringSystem springSystem = SpringSystem.create();
        final Spring spring = springSystem.createSpring();

        // add listeners along arc
        final double arc = 2 * Math.PI / (mButtons.size());

        for (int i = 0; i < mButtons.size(); i++) {
            View view = mButtons.get(i);

            // map spring to a line segment from the center to the edge of the ring
            spring.addListener(new MapPerformer(view, View.TRANSLATION_X, 0, 1,
                    0, (float) (RING_DIAMETER * Math.sin(i * arc))));

            spring.addListener(new MapPerformer(view, View.TRANSLATION_Y, 0, 1,
                    0, (float) (RING_DIAMETER * Math.cos(i * arc))));

            spring.setEndValue(CLOSED);
        }

        final ToggleImitator imitator = new ToggleImitator(spring, CLOSED, OPEN);
        final SnapImitator snapImitator = new SnapImitator(MotionProperty.X);
        // move circle using finger, snap when near another circle, and bloom when touched
        new Actor.Builder(SpringSystem.create(), mOpenButton)
                .addMotion(snapImitator, View.TRANSLATION_X)
                .addMotion(new SnapImitator(MotionProperty.Y), View.TRANSLATION_Y)
                .onTouchListener((v, event) -> {
                    // bloom!
                    imitator.imitate(event);

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Button nearestButton = (Button)snapImitator.getNearest();
                        if (nearestButton != null && nearestButton.isEnabled()) {
                            nearestButton.performClick();
                        }
                    }

                    return true;
                })
                .build();


        return rootView;
    }

    private class MenuToggleImitator extends ToggleImitator {

        public MenuToggleImitator(Spring spring, double restValue, double activeValue) {
            super(spring, restValue, activeValue);
        }
    }

    private class SnapImitator extends MotionImitator {

        private View mNearest;

        public SnapImitator(MotionProperty property) {
            super(property, 0, Imitator.TRACK_ABSOLUTE, Imitator.FOLLOW_SPRING);
        }

        @Override
        public void mime(float offset, float value, float delta, float dt, MotionEvent event) {
            // find the mNearest view
            mNearest = nearest(
                    event.getX() + mOpenButton.getX(),
                    event.getY() + mOpenButton.getY(), mButtons);

            if (mNearest != null) {
                // snap to it - remember to compensate for translation
                switch (mProperty) {
                    case X:
                        getSpring().setEndValue(mNearest.getX() + mNearest.getWidth() / 2
                                - mOpenButton.getLeft() - mOpenButton.getWidth() / 2);
                        break;
                    case Y:
                        getSpring().setEndValue(mNearest.getY() + mNearest.getHeight() / 2
                                - mOpenButton.getTop() - mOpenButton.getHeight() / 2);
                        break;
                }
            } else {
                // follow finger
                super.mime(offset, value, delta, dt, event);
            }
        }

        public View getNearest () {
            return mNearest;
        }

        private double distSq(double x1, double y1, double x2, double y2) {
            return Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
        }

        private View nearest(float x, float y, ArrayList<View> views) {
            double minDistSq = Double.MAX_VALUE;
            View minView = null;

            for (View view : views) {
                double distSq = distSq(x, y, view.getX() + view.getMeasuredWidth() / 2,
                        view.getY() + view.getMeasuredHeight() / 2);

                if (distSq < Math.pow(1.5f * view.getMeasuredWidth(), 2) && distSq < minDistSq) {
                    minDistSq = distSq;
                    minView = view;
                }
            }

            return minView;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        LogoAnimator logoAnimator = new LogoAnimator(mCirclesLogo, "color1", 0);
        LogoAnimator logoAnimator2 = new LogoAnimator(mCirclesLogo, "color2", 250);
        LogoAnimator logoAnimator3 = new LogoAnimator(mCirclesLogo, "color3", 500);
        LogoAnimator logoAnimator4 = new LogoAnimator(mCirclesLogo, "color4", 750);
        LogoAnimator logoAnimator5 = new LogoAnimator(mCirclesLogo, "color5", 1000);
        LogoAnimator logoAnimator6 = new LogoAnimator(mCirclesLogo, "color6", 1250);

        logoAnimator.getAnim().start();
        logoAnimator2.getAnim().start();
        logoAnimator3.getAnim().start();
        logoAnimator4.getAnim().start();
        logoAnimator5.getAnim().start();
        logoAnimator6.getAnim().start();

        ValueAnimator anim = ObjectAnimator.ofInt
                (mTransparentBackground, "backgroundColor",
                        Color.argb(0xA0, 0xFF, 0xFF, 0xFF), Color.argb(0x50, 0xFF, 0xFF, 0xFF));
        anim.setDuration(1500);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setEvaluator(new ArgbEvaluator());
        anim.start();
    }
}
