package com.vithushan.sixdegrees.model;

public abstract class IGameObject implements Comparable<IGameObject> {

	public String getId() {
		return "";
	}

	public String getName() {
		return "";
	}

	public String getImageURL() {
		return "";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof IGameObject) {
			IGameObject object = (IGameObject)o;
			return getName().equals(object.getName());
		}
		return false;
	}

    @Override
    public int compareTo(IGameObject another) {
        return getName().compareTo(another.getName());
    }
}
