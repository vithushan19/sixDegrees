package com.vithushan.sixdegrees.util;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

import com.vithushan.sixdegrees.model.IHollywoodObject;

import java.nio.ByteBuffer;

/**
 * Created by vnama on 10/10/2015.
 */
public class SixDegreesUtils {

    public static int byteArrayToInt(byte[] arr) {
        ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
        int num = wrapped.getInt(); // 1
        return num;
    }

    public static byte[] IntToByteArray(int num) {
        ByteBuffer dbuf = ByteBuffer.allocate(4);
        dbuf.putInt(num);
        byte[] bytes = dbuf.array(); // { 0, 1 }
        return bytes;
    }

    // Takes in a list of hollywood objects and produces a byte[] for messaging
    // The format starts with a 'W' to signal win game, after that every four
    // bytes represents the integer id of a hollywood object from the list
    public static byte[] HollywoodListToByteArray(IHollywoodObject[] list) {

        int[] idList = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            idList[i] = Integer.valueOf(list[i].getId());
        }

        int idListLength = idList.length;
        byte[] dst = new byte[(idListLength * 4) + 1];
        dst[0] = 'W';
        int j = 1;
        for (int i = 0; i < idListLength; i++) {
            int x = idList[i];
            byte[] xArr = IntToByteArray(x);
            dst[j] = (byte) (xArr[0]);
            dst[j + 1] = (byte) (xArr[1]);
            dst[j + 2] = (byte) (xArr[2]);
            dst[j + 3] = (byte) (xArr[3]);
            j = j + 4;
        }
        return dst;
    }


    // Sets the flag to keep this screen on. It's recommended to do that during
    // the handshake when setting up a game, because if the screen turns off, the
    // game will be cancelled.
    public static void keepScreenOn(Activity a) {
        a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    public static void stopKeepingScreenOn(Activity a) {
        a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
