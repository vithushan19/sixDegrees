package com.vithushan.sixdegrees.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.model.IHollywoodObject;

import java.util.ArrayList;

/**
 * Created by vnama on 10/8/2015.
 */

public class HighlightableRecyclerViewAdapter extends RecyclerViewAdapter {

    private IHollywoodObject mLastClickedItem;


    public HighlightableRecyclerViewAdapter(ArrayList<IHollywoodObject> myDataset, Context context, ItemClickListener listener) {
        super(myDataset, context, listener);
    }

    public void setLastClickedItem(IHollywoodObject selectedItem) {
        mLastClickedItem = selectedItem;
    }

    public IHollywoodObject getLastClickedItem () {
        return mLastClickedItem;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (mDataset.get(position).equals(mLastClickedItem)) {
            Drawable background = mContext.getResources().getDrawable(R.drawable.blue_panel);
            holder.txtFooter.setBackgroundDrawable(background);
        } else {
            Drawable background = mContext.getResources().getDrawable(R.drawable.background);
            holder.txtFooter.setBackgroundDrawable(background);
        }
    }
}
