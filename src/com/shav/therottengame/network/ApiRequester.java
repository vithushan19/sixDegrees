package com.shav.therottengame.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ApiRequester {
	
	public List<String> getMoviesForActor (String actor) {
		List<String> movies = new ArrayList<String>();
        for (int i = 1; i < actor.length(); i++) {
            if (Character.isUpperCase(actor.charAt(i)) && !Character.isWhitespace(actor.charAt(i - 1))) {
                actor = actor.substring(0, i) + " " + actor.substring(i, actor.length());
                i++;
            }
        }
        actor = actor.replace(" ", "_").toLowerCase();
        try {
            Document doc = Jsoup.connect("http://www.rottentomatoes.com/celebrity/" + actor + "/").get();
            Elements films = doc.select("#filmographyTbl a");
            int i = 0;
            for ( Element e : films) {
                if (i < 5)
                    i++;
                else
                    movies.add(e.text());
            }
        } catch (IOException e) {

        }
        
        return movies;
	}

	public JSONObject makeRequest (String url) {
		
		HttpClient httpclient = new DefaultHttpClient();

	    // Prepare a request object
	    HttpGet httpget = new HttpGet(url); 

	    // Execute the request
	    HttpResponse response;
	    JSONObject jObject = null;
	    try {
	        response = httpclient.execute(httpget);

	        // Get hold of the response entity
	        HttpEntity entity = response.getEntity();
	        // If the response does not enclose an entity, there is no need
	        // to worry about connection release

	        if (entity != null) {

	            // A Simple JSON Response Read
	            InputStream instream = entity.getContent();
	            String result= convertStreamToString(instream);
	            jObject = new JSONObject(result);
	            instream.close();
	        }
	        

	    } catch (Exception e) {
	    	
	    }
		return jObject;
	}
	
	 private static String convertStreamToString(InputStream is) {
		    /*
		     * To convert the InputStream to String we use the BufferedReader.readLine()
		     * method. We iterate until the BufferedReader return null which means
		     * there's no more data to read. Each line will appended to a StringBuilder
		     * and returned as String.
		     */
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();

		    String line = null;
		    try {
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        try {
		            is.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    return sb.toString();
	 }
}
