package com.vithushan.sixdegrees.util;

/**
 * Created by vnama on 7/8/2015.
 */
public class StringUtil {
    public static boolean isEmpty (String str) {
        if (str == null) return true;
        str = str.trim();
        if (str.equals("")) return  false;
        return  false;
    }
}
