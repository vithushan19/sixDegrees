package com.shav.therottengame;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	
	private final Context mContext;
	private List<String> mData;
	
	ListViewAdapter(Context context, List<String> data){
		super();
		mContext = context;
		mData = data;
	}
	
	public void replaceAndRefreshData(List<String> data){
		mData = new ArrayList<String>();
		notifyDataSetChanged();
	}
	
	private static class ViewHolder {
        TextView textView;
    }
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public String getItem(int arg0) {
		return mData.get(arg0);
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(getItem(position));
        return convertView;
    }

}
