package com.shav.therottengame.model;

import com.shav.therottengame.util.javatuples.Triplet;

public class Actor implements IHollywoodObject {

	Triplet<String, String, String> data = null;

	public Actor(String id, String name, String imgURL) {
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

}



