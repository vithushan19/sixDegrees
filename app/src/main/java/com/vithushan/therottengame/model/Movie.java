package com.vithushan.therottengame.model;

import com.vithushan.therottengame.util.javatuples.Triplet;


public class Movie implements IHollywoodObject {

	private String id;
	private String name;
	private String posterPath;

	public Movie(String id, String name, String imgURL) {
		if ("http://image.tmdb.org/t/p/w92".equals(imgURL)) {
			imgURL = "";
		}

		this.id = id;
		this.name = name;
		this.posterPath = imgURL;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getImageURL() {
		return posterPath;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Movie) {
			Movie a = (Movie) o;
			if (a.getId().equals(getId())) {
				if (a.getName().equals(getName())) {
					return true;
				}
			}
		}
		return false;
		
	}
}



