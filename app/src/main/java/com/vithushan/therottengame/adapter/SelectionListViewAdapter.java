package com.vithushan.therottengame.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vithushan.therottengame.R;
import com.vithushan.therottengame.model.IHollywoodObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectionListViewAdapter extends ListViewAdapter {

    private int selectedItemPosition;

	public SelectionListViewAdapter(Context context, List<IHollywoodObject> mCurrentList){
		super(context, mCurrentList);
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