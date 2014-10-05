package com.shav.therottengame;

import java.util.Random;

public class Actors {

	public static final String[] actorsList = {
		"Aaron Paul",
		"Adam Sandler",
		"Andrew Garfield",
		"Brad Pitt",
		"Bryan Cranston",
		"Cameron Diaz",
		"Chris Evans",
		"Dave Franco",
		"Drew Barrymore",
		"Dustin Hoffman",
		"Emily Blunt",
		"Emma Stone",
		"Jamie Foxx",
		"Jennifer Garner",
		"Joel McHale",
		"Johnny Depp",
		"Kate Upton",
		"Kate Winslet",
		"Kevin Costner",
		"Leonardo DiCaprio",
		"Mark Wahlberg",
		"Paul Walker",
		"Ray Liotta",
		"Ricky Gervais",
		"Russell Crowe",
		"Samuel L. Jackson",
		"Scarlett Johansson",
		"Sofia Vergara",
		"Tina Fey",
		"Tom Cruise",
		"Tom Hanks",
		"Tom Hardy",
		"Willem Dafoe",
		"Zac Efron",
	};
	
	private String mFirstActor, mLastActor;
	
	public String getFirstActor() {
	    Random rand = new Random();
	    rand.setSeed(System.currentTimeMillis());
	    int randomNum = rand.nextInt(actorsList.length);
	    mFirstActor = actorsList[randomNum];
		return mFirstActor;
	}
	
	public String getLastActor() {
		mLastActor = mFirstActor;
		while(mFirstActor.equals(mLastActor)) {
			Random rand = new Random();
		    rand.setSeed(System.currentTimeMillis());
		    int randomNum = rand.nextInt(actorsList.length);
		    mLastActor = actorsList[randomNum];
		}
		
		return mLastActor;
		
	}

}
