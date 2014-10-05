package com.shav.therottengame;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shav.therottengame.network.ApiRequester;

public class ListViewActivity extends Activity {
	
	private ListViewAdapter mAdapter;
	private ListView mListView;
	private List<String> mCurrentList;
	private String mStartingActor;
	private String mEndingActor;
	private int mClickCount;
	private ApiRequester mApiRequester;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mListView = (ListView) findViewById(R.id.listview);
		String[] values = new String[] { "Hugh Jackman" };

		mCurrentList = new ArrayList<String>();
		for (int i = 0; i < values.length; ++i) {
			mCurrentList.add(values[i]);
		}
		mClickCount = 0;
		mAdapter = new ListViewAdapter(this, mCurrentList);
		mListView.setAdapter(mAdapter);
		mApiRequester = new ApiRequester();
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				String text = (String) parent.getItemAtPosition(position);
				if (mClickCount % 2 == 0) {
					new NetworkTask().execute("movies", text);
				} else {
					new NetworkTask().execute("actors", text);
				}
				
			}
		});
	}
	
	 private class NetworkTask extends AsyncTask<String, Void, List<String>> {
	     protected List<String> doInBackground(String... strings) {
	    	 String downloadType = strings[0];
	    	 String query = strings[1];
	    	 if (downloadType == "movies") {
	    		 return mApiRequester.getMoviesForActor(query);
	    	 } else {
	    		 return null;
	    	 }
	     }

	     protected void onPostExecute(List<String> result) {
	        mCurrentList = result;
	        mAdapter.replaceAndRefreshData(mCurrentList);
	     }
	 }

}
