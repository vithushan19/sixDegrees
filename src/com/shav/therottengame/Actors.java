package com.shav.therottengame;

import java.util.Calendar;
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
	
	Random rand;
	
	private String mFirstActor, mLastActor;
	
	public Actors() {
		rand = new Random();
		Calendar now = Calendar.getInstance();
		int month = now.get(Calendar.MINUTE);
		rand.setSeed(month);
	}
	
	public String getFirstActor() {
	    int randomNum = rand.nextInt(actorsList.length);
	    mFirstActor = actorsList[randomNum];
		return mFirstActor;
	}
	
	public String getLastActor() {
		mLastActor = mFirstActor;
		while(mFirstActor.equals(mLastActor)) {
		    int randomNum = rand.nextInt(actorsList.length);
		    mLastActor = actorsList[randomNum];
		}
		
		return mLastActor;
		
	}

}
