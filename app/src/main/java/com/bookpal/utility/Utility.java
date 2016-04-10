package com.bookpal.utility;

import android.content.Context;

/**
 * Created by Asif on 25-03-2016.
 */
public class Utility {

    public static boolean isLoggedIn(Context context) {
        return SharedPreference.getBoolean(context, AppConstants.PREF_KEY_IS_LOGGED_IN);
    }
}
