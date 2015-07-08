package com.vithushan.therottengame.model;

public class Actor implements IHollywoodObject{

	private String id;
	private String name;
	private String profilePath;



	public Actor(String id, String name, String imgURL) {
		if ("http://image.tmdb.org/t/p/w92".equals(imgURL)) {
			imgURL = "";
		}
		this.id = id;
		this.name = name;
		this.profilePath = imgURL;
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
		return profilePath;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Actor) {
			Actor a = (Actor) o;
			if (a.getId().equals(getId())) {
				if (a.getName().equals(getName())) {
					return true;
				}
			}
		}
		return false;
		
	}

}



