package com.vithushan.sixdegrees.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Vithushan on 7/18/2015.
 */

/*
    RecyclerView throws an exception when animations try to scroll their views to a location
    This exception went uncaught and crashed the app. Use this class as a dirty fix to swall that
    exception, until the RecyclerView issue is addressed
 */
public class MyRecyclerView extends RecyclerView{

    private static final String TAG = "CustomRecyclerView";

    public MyRecyclerView(android.content.Context context) {
        super(context);
    }

    public MyRecyclerView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void scrollTo(int x, int y) {

        try {
            super.scrollTo(x,y);
        } catch (Exception e) {
            //Log.e(TAG, "CustomRecyclerView does not support scrolling to an absolute position.");
        }
        // Either don't call super here or call just for some phones, or try catch it. From default implementation we have removed the Runtime Exception trown
    }
}
