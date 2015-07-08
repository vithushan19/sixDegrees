import com.vithushan.therottengame.GameModule;
import com.vithushan.therottengame.api.IMovieAPIClient;
import com.vithushan.therottengame.api.MovieAPIClient;
import com.vithushan.therottengame.model.Actor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MovieAPIClientTest {

    @Inject
    IMovieAPIClient mMovieAPIClient;

    @Before
    public void setUp() {
        ObjectGraph.create(new TestModule()).inject(this);
    }

    @Module(
            includes = GameModule.class,
            injects = MovieAPIClientTest.class,
            overrides = true
    )
    static class TestModule {
        @Provides
        @Singleton
        IMovieAPIClient provideIMovieAPIClient() {
            return new MovieAPIClient();
        }
    }

    @Test
    public void testIntentShouldBeCreated() {

        Actor result = mMovieAPIClient.getLastActor();
        assertEquals(result.getName(), "Vithushan");
    }
}

