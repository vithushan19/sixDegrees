package com.vithushan.sixdegrees;

import com.vithushan.sixdegrees.activity.GameActivity;
import com.vithushan.sixdegrees.api.IMovieAPIClient;
import com.vithushan.sixdegrees.model.Actor;
import com.vithushan.sixdegrees.model.Cast;
import com.vithushan.sixdegrees.model.IHollywoodObject;
import com.vithushan.sixdegrees.model.MovieCredits;
import com.vithushan.sixdegrees.model.Movie;
import com.vithushan.sixdegrees.model.PopularPeople;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import retrofit.RestAdapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

class MessagingTest {

    @Test
    public void messagingByteTest() {
        GameActivity activity = new GameActivity();
        int x = 48998;
        byte[] res = activity.IntToByteArray(x);
        int y = activity.byteArrayToInt(res);

        Assert.assertEquals(x, y);
    }

    @Test
    public void packedMessagingByte() {
        GameActivity activity = new GameActivity();
        Actor a = new Actor("123", "V", "ggogl.ca");
        Movie b = new Movie("2342352", "ADFSDF", "dsfsdfs.ca");
        Actor c = new Actor("9384503", "L", "dfsdfsd.ca");
        Movie d = new Movie("2342352", "ADFSDF", "dsfsdfs.ca");
        Actor e = new Actor("9384503", "L", "dfsdfsd.ca");

        ArrayList<IHollywoodObject> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);
        list.add(e);

        IHollywoodObject[] arr = new IHollywoodObject[list.size()];
        list.toArray(arr);

        ArrayList<String> idList = new ArrayList<>();

        byte[] resArr = activity.HollywoodListToByteArray(arr);

        for (int i=1; i<resArr.length;) {
            byte[] sub = Arrays.copyOfRange(resArr, i, i + 4);
            int x = activity.byteArrayToInt(sub);
            idList.add(String.valueOf(x));
            i = i+4;
        }

        for (int i=0; i<idList.size(); i++) {
            assertEquals(idList.get(i),"123");
        }
    }



}

