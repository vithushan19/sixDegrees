package com.vithushan.sixdegrees.animator;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

import jp.wasabeef.recyclerview.animators.BaseItemAnimator;

/**
 * Created by Vithushan on 7/18/2015.
 *
 * ItemAnimator to be used with a RecycleView
 */
public class SlideAnimator extends BaseItemAnimator {

    @Override
    protected void animateRemoveImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView).translationX((float)(-holder.itemView.getRootView().getWidth())).setDuration(this.getRemoveDuration()).setListener(new DefaultRemoveVpaListener(holder)).start();
    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView, (float)holder.itemView.getRootView().getWidth());
    }

    @Override
    protected void animateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView).translationX(0.0F).setDuration(this.getAddDuration()).setListener(new DefaultAddVpaListener(holder)).start();
    }
}
