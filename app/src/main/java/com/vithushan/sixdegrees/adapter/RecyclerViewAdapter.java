package com.vithushan.sixdegrees.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.model.IGameObject;
import com.vithushan.sixdegrees.util.StringUtil;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(IGameObject obj);
    }

    protected ArrayList<IGameObject> mDataset;
    protected Context mContext;
    private ItemClickListener mListener;



    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(ArrayList<IGameObject> myDataset, Context context, ItemClickListener listener) {
        mDataset = myDataset;
        mContext = context;
        mListener = listener;
    }

    public void removeAll() {
        int oldSize = this.mDataset.size();
        if (oldSize > 0) {
            for (int i = 0; i < oldSize; i++) {
                this.mDataset.remove(0);
            }
            notifyItemRangeRemoved(0, oldSize);
        }

    }

    public void refreshWithNewList(ArrayList<IGameObject> currentList) {

        int newSize = currentList.size();
        if (newSize > 0) {
            for (int i = 0; i < newSize; i++) {
                mDataset.add(i, currentList.get(i));
            }
            notifyItemRangeInserted(0, newSize);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imgHeader;
        public TextView txtFooter;
        public LinearLayout container;

        public ViewHolder(View v) {
            super(v);
            imgHeader = (ImageView) v.findViewById(R.id.imageViewRow);
            txtFooter = (TextView) v.findViewById(R.id.textViewRow);
            container = (LinearLayout) v.findViewById(R.id.item_container);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listrow, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final IGameObject name = mDataset.get(position);
        if (StringUtil.isEmpty(mDataset.get(position).getImageURL())) {
            Picasso.with(mContext).load(R.drawable.movie_placeholder).into(holder.imgHeader);
        } else {
            Picasso.with(mContext).load(mDataset.get(position).getImageURL()).into(holder.imgHeader);

        }

        holder.txtFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener == null) return;
                mListener.onItemClick(mDataset.get(position));
            }
        });

        holder.txtFooter.setText(mDataset.get(position).getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public IGameObject getItem (int index) {
        return this.mDataset.get(index);
    }

}