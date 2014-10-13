package com.shav.therottengame;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	
	private final Context mContext;
	private List<BasicNameValuePair> mData;
	
	public ListViewAdapter(Context context, List<BasicNameValuePair> data){
		super();
		mContext = context;
		mData = data;
	}
	
	public void replaceAndRefreshData(List<BasicNameValuePair> data){
		mData = new ArrayList<BasicNameValuePair>();
		mData.addAll(data);
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
	public BasicNameValuePair getItem(int pos) {
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(getItem(position).getName());
        return convertView;
    }

}