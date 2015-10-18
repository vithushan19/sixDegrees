package com.vithushan.sixdegrees.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vithushan.sixdegrees.R;
import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.util.StringUtil;

public class ListViewAdapter extends BaseAdapter {
	
	protected final Context mContext;
	protected List<IHollywoodObject> mData;
    private int selectedIndex = -1;
	
	public ListViewAdapter(Context context, List<IHollywoodObject> mCurrentList){
		super();
		mContext = context;
		mData = mCurrentList;
	}

    public void setSelectedIndex (int pos) {
        selectedIndex = pos;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }


	public void replaceAndRefreshData(List<IHollywoodObject> data){
		mData = new ArrayList<IHollywoodObject>();
		mData.addAll(data);
		notifyDataSetChanged();
	}
	
	protected static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public IHollywoodObject getItem(int pos) {
		return mData.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        IHollywoodObject item = getItem(position);
        if (convertView == null) {
            final LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.listrow, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.textViewRow);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewRow);
            convertView.setTag(holder);
            
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(item.getName());

        if (StringUtil.isEmpty(item.getImageURL())) {
        	// TODO get a better asset/
        	holder.imageView.setImageResource(R.drawable.movie_placeholder);
        } else {
            if (item instanceof Actor) {
                Picasso.with(mContext).load(item.getImageURL()).into(holder.imageView);
            }
            holder.imageView.setImageResource(R.drawable.movie_placeholder);

        }

        if (position == selectedIndex) {
            convertView.setBackgroundResource(R.color.main_blue);
        } else {
            convertView.setBackgroundResource(R.color.white);
        }
        return convertView;
    }

}