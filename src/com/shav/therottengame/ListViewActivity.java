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
import android.widget.TextView;
import android.widget.Toast;

import com.shav.therottengame.network.ApiRequester;

public class ListViewActivity extends Activity {
	
	private ListViewAdapter mAdapter;
	private ListView mListView;
	private List<String> mCurrentList;
	private String mStartingActor;
	private String mEndingActor;
	private int mClickCount;
	private RequestType mCurrentRequestType;
	private ApiRequester mApiRequester;
	
	private enum RequestType {
		ACTOR,
		MOVIE,
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mListView = (ListView) findViewById(R.id.listview);
		TextView startingActortv = (TextView) findViewById(R.id.textViewStarting);
		TextView endingActortv = (TextView) findViewById(R.id.textViewEnding);
		mCurrentList = new ArrayList<String>();
		String start = "Brad Pitt";
		startingActortv.setText(start);
		mCurrentList.add(start);
		String end = "Drew Barrymore";
		endingActortv.setText(end);
		mStartingActor = start;
		mEndingActor = end;
		mClickCount = 0;
		mCurrentRequestType = RequestType.MOVIE;
		mAdapter = new ListViewAdapter(this, mCurrentList);
		mListView.setAdapter(mAdapter);
		mApiRequester = new ApiRequester();
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) { 
				String text = (String) parent.getItemAtPosition(position);
				if (text.equals(mEndingActor)){
					Toast.makeText(getApplicationContext(), "You Won", Toast.LENGTH_LONG).show();
					return;
				}
				if (mCurrentRequestType == RequestType.MOVIE) {
					new NetworkTask().execute("movies", text);
					mCurrentRequestType = RequestType.ACTOR;
				} else {
					new NetworkTask().execute("actors", text);
					mCurrentRequestType = RequestType.MOVIE;
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
	    		 return mApiRequester.getActorsForMovies(query);
	    	 }
	     }

	     protected void onPostExecute(List<String> result) {
	        mCurrentList = result;
	        mAdapter.replaceAndRefreshData(mCurrentList);
	     }
	 }

}
