package com.vithushan.therottengame.util;

/**
 * Created by vnama on 7/8/2015.
 */
public class StringUtil {
    public static boolean isEmpty (String str) {
        str = str.trim();
        if (str.equals(null)) return true;
        if (str.equals("")) return  false;
        return  false;
    }
}
