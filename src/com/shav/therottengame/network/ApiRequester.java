package com.shav.therottengame.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class ApiRequester {
	private static final String MOVIE_NOT_FOUND = "!@#NotFound$%^";
	public static final String API_KEY = "hj8fvx6ujvzc5pdqqjddhy43";
	public static final String BASE_URL = "http://api.rottentomatoes.com/api/public/v1.0/";
	
	public List<String> getMoviesForActor (String actor) {
		List<String> movies = new ArrayList<String>();
        for (int i = 1; i < actor.length(); i++) {
            if (Character.isUpperCase(actor.charAt(i)) && !Character.isWhitespace(actor.charAt(i - 1))) {
                actor = actor.substring(0, i) + " " + actor.substring(i, actor.length());
                i++;
            }
        }
		actor = actor.replaceAll("\\s+","_").toLowerCase();
//        actor = actor.replace(" ", "_").toLowerCase();l
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
        	e.printStackTrace();
        }
        
        return movies;
	}
	
	public String getMovieId (String movie) {
		try {
			movie = URLEncoder.encode(movie, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		JSONObject jResponse = makeRequest(BASE_URL + "movies.json?apikey=" + API_KEY + "&q=" + movie + "&page_limit=1");
		try {
			String selfUrl = jResponse.getJSONArray("movies").getJSONObject(0).getJSONObject("links").getString("self");
			int movieIdIndex = selfUrl.indexOf("movies/") + 6;
			return selfUrl.substring(movieIdIndex, selfUrl.length() - ".json".length());
		} catch (JSONException e) {
			return MOVIE_NOT_FOUND;
		}
	}
	
	public List<String> getActorsForMovies (String movie) {
		
		String movieId = getMovieId(movie);
		if (movieId.equals(MOVIE_NOT_FOUND))
			return null;
		
		JSONObject jResponse = makeRequest(BASE_URL + "movies/" + movieId + "/cast.json?apikey=" + API_KEY);
		List<String> cast = new ArrayList<String>();
		try {
			JSONArray jCast = jResponse.getJSONArray("cast");
			for (int i = 0; i < jCast.length();i++) {
				cast.add(((JSONObject)jCast.get(i)).getString("name"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return cast;
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
	    	e.printStackTrace();
	    }
		return jObject;
	}
	
	 private static String convertStreamToString(InputStream is) {
		    /*
		     * To convert the InputStream to String we use the BufferedReader.readLine()
		     * method. We iterate until the BufferedReader return null which means
		     * there's no more data to read. Each line will appended to a StringBuilder
		     * and returned as String.l
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
