import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestRunner;

import com.vithushan.sixdegrees.activity.GameActivity;

import org.junit.Before;

/**
 * Created by vnama on 10/18/2015.
 */
public class EspressoTest extends ActivityInstrumentationTestCase2<GameActivity> {

    private GameActivity mActivity;

    public EspressoTest() {
        super(GameActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(getInstrumentation());
        mActivity = getActivity();
    }


}
