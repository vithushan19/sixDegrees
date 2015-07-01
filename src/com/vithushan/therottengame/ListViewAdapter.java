package com.vithushan.therottengame;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vithushan.therottengame.model.IHollywoodObject;

public class ListViewAdapter extends BaseAdapter {
	
	private final Context mContext;
	private List<IHollywoodObject> mData;
	
	public ListViewAdapter(Context context, List<IHollywoodObject> mCurrentList){
		super();
		mContext = context;
		mData = mCurrentList;
	}
	
	public void replaceAndRefreshData(List<IHollywoodObject> data){
		mData = new ArrayList<IHollywoodObject>();
		mData.addAll(data);
		notifyDataSetChanged();
	}
	
	private static class ViewHolder {
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
        holder.textView.setText(getItem(position).getName());
        

        if (StringUtils.isEmpty(getItem(position).getImageURL())) {
        	// TODO get a better asset/
        	holder.imageView.setImageResource(R.drawable.question_mark);
        } else {
        	Picasso.with(mContext).load(getItem(position).getImageURL()).into(holder.imageView);
        }
        return convertView;
    }

}