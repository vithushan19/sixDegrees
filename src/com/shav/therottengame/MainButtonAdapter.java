package com.shav.therottengame;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainButtonAdapter extends BaseAdapter {

	private Context context;
	private final int[] mainButtons;

	public MainButtonAdapter(Context context, int[] buttonTitles) {
		this.context = context;
		this.mainButtons = buttonTitles;
	}

	public TextView getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		TextView gridView;

		if (convertView == null) {

			gridView = new TextView(context);

			// get layout from mobile.xml
			gridView = (TextView) inflater.inflate(R.layout.main_button, null);
			
			// set value into textview
			gridView.setText(context.getString(mainButtons[position]));
			gridView.setGravity(Gravity.CENTER);
			
			switch (position) {
			case 0:
			case 2:
			case 4:
				gridView.setBackgroundColor(context.getResources().getColor(
						R.color.AccentColor));
				gridView.setTextColor(context.getResources().getColor(
						R.color.BlueColor));
				break;
			case 1:
			case 3:
			default:
				gridView.setBackgroundColor(context.getResources().getColor(
						R.color.BlueColor));
				gridView.setTextColor(context.getResources().getColor(
						R.color.AccentColor));

			}

		} else {
			gridView = (TextView) convertView;
		}
		
		return gridView;
	}

	@Override
	public int getCount() {
		return mainButtons.length;
	}

	@Override
	public Object getItem(int position) {
		return mainButtons[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	
}
