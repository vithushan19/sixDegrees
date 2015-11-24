package com.vithushan.sixdegrees.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.model.IGameObject;

import java.util.ArrayList;

/**
 * Created by vnama on 10/8/2015.
 */

public class HighlightableRecyclerViewAdapter extends RecyclerViewAdapter {

    private IGameObject mLastClickedItem;
    private IGameObject mSecondLastClickedItem;


    public HighlightableRecyclerViewAdapter(ArrayList<IGameObject> myDataset, Context context, ItemClickListener listener) {
        super(myDataset, context, listener);
    }

    public void setLastClickedItem(IGameObject selectedItem) {
        mSecondLastClickedItem = mLastClickedItem;
        mLastClickedItem = selectedItem;
    }

    public IGameObject getLastClickedItem () {
        return mLastClickedItem;
    }

    public IGameObject getSecondLastClickedItem() {
        return mSecondLastClickedItem;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (mDataset.get(position).equals(mLastClickedItem) || mDataset.get(position).equals(mSecondLastClickedItem)) {
            Drawable background = mContext.getResources().getDrawable(R.drawable.blue_panel);
            holder.txtFooter.setBackgroundDrawable(background);
        } else {
            Drawable background = mContext.getResources().getDrawable(R.drawable.background);
            holder.txtFooter.setBackgroundDrawable(background);
        }
    }
}
