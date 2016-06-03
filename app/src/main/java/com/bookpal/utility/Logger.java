package com.bookpal.utility;

import android.util.Log;

public class Logger {
    private static boolean DEBUG = true;

    /*
     Print error log in console
     */
    public static void e(String Tag, String msg) {
        if (DEBUG) {
            if (msg != null)
                Log.e(Tag, msg);
        }
    }

    /*
    Print Debug log in console
     */
    public static void d(String Tag, String msg) {
        if (DEBUG) {
            if (msg != null)
                Log.e(Tag, msg);
        }
    }

    /*
Print Information log in console
 */
    public static void i(String Tag, String msg) {
        if (DEBUG) {
            if (msg != null)
                Log.e(Tag, msg);
        }
    }
}
