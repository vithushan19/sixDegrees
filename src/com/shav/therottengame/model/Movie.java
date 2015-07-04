package com.shav.therottengame.model;

import com.shav.therottengame.util.javatuples.Triplet;

public class Movie implements IHollywoodObject {

	Triplet<String, String, String> data = null;

	public Movie(String id, String name, String imgURL) {
		if ("http://image.tmdb.org/t/p/w92".equals(imgURL)) {
			imgURL = "";
		}
		data = new Triplet<String, String, String>(id, name, imgURL);
	}

	@Override
	public String getId() {
		return data.getValue0();
	}

	@Override
	public String getName() {
		return data.getValue1();
	}

	@Override
	public String getImageURL() {
		return data.getValue2();
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



