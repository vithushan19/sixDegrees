package com.vithushan.sixdegrees.model;

public abstract class IHollywoodObject implements Comparable<IHollywoodObject> {

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
		if (o instanceof IHollywoodObject) {
			IHollywoodObject object = (IHollywoodObject)o;
			return getName().equals(object.getName());
		}
		return false;
	}

    @Override
    public int compareTo(IHollywoodObject another) {
        return getName().compareTo(another.getName());
    }
}
