package com.vithushan.sixdegrees.model.movie;

import com.vithushan.sixdegrees.model.IGameObject;

public class Actor extends IGameObject {

	private String id;
	private String name;
	private String profile_path;

	public Actor(String id, String name, String imgURL) {
		if ("http://image.tmdb.org/t/p/w92".equals(imgURL)) {
			imgURL = "";
		}
		this.id = id;
		this.name = name;
		this.profile_path = imgURL;
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
		String res = "http://image.tmdb.org/t/p/w154" +  profile_path;
        return res;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{ ID: " ).append(id).append(", NAME: ").append(name).append(" }");
		return builder.toString();
	}

}



