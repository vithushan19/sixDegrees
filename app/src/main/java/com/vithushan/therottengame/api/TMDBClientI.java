package com.vithushan.therottengame.api;


public class TMDBClientI {
/*
	private TheMovieDbApi tmdb;
	private String API_KEY = "4e83b0a69397058d51b07371e1eb131a";
	private String BEN_AFFLECK = "880";
	private String GONE_GIRL = "210577";
	private List<Actor> mPopularActors;

	public TMDBClientI() {
        try {
            tmdb = new TheMovieDbApi(API_KEY, new DefaultHttpClient());
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
    }

	@Override
	public List<IHollywoodObject> getCastForMedia(int mediaID)
			throws MovieDbException {

		List<MediaCreditCast> cast = tmdb.getMovieCredits(mediaID).getCast();
		List<IHollywoodObject> castNames = new ArrayList<IHollywoodObject>();
		for (MediaCreditCast person : cast) {
			URL url = tmdb.createImageUrl(person.getArtworkPath(), "w92");
			Actor item = new Actor(String.valueOf(person.getId()),
					person.getName(), url.toString());
			if (!castNames.contains(item)) {
				castNames.add(item);
			}
		}

		return castNames;
	}

	@Override
	public List<IHollywoodObject> getMediaForActor(int actorId)
			throws MovieDbException {
		List<CreditBasic> movies = tmdb.getPersonCombinedCredits(actorId, "").getCast();

		List<IHollywoodObject> movieList = new ArrayList<IHollywoodObject>();
		for (CreditBasic movie : movies) {
			URL url = tmdb.createImageUrl(movie.getArtworkPath(), "w92");
            String title="";
            if (movie instanceof CreditMovieBasic) {
                title = ((CreditMovieBasic)movie).getTitle();
            } else if (movie instanceof CreditTVBasic) {
                title = ((CreditTVBasic)movie).getName();
            }
			Movie item = new Movie(String.valueOf(movie.getId()),
					title, url.toString());
			if (!movieList.contains(item)) {
				movieList.add(item);
			}
		}

		return movieList;

	}

	public List<Actor> getPopularActors() {

        if (this.mPopularActors == null) {
            try {
                List<PersonFind> popularPeople = tmdb.getPersonPopular(0).getResults();
                List<Actor> popularPeopleList = new ArrayList<Actor>();
                for (PersonFind popularPerson : popularPeople) {
                    String id = String.valueOf(popularPerson.getId());
                    URL url = tmdb
                            .createImageUrl(popularPerson.getProfilePath(), "w92");
                    Actor item = new Actor(id, popularPerson.getName(), url.toString());

                    if (!popularPeopleList.contains(item)) {
                        popularPeopleList.add(item);
                    }
                }

                return popularPeopleList;
            } catch (MovieDbException e) {
                e.printStackTrace();
            }
        }

        return this.mPopularActors;

	}

	public String getActorName(int id) throws MovieDbException {
		String name = tmdb.getPersonInfo(id, "").getName();
		return name;
	}

	// TODO: Rand this
	@Override
	public Actor getFirstActor() {
		return getPopularActors().get(4);
	}

	@Override
	public Actor getLastActor() {
		return mPopularActors.get(14);

	}
*/
}
