package com.bookpal.utility;

/**
 * Created by Asif on 25-03-2016.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    public static final String APP_PREF = "BookPal Pref";

    public static long getLong(Context context, String key) {
        SharedPreferences preference = context.getSharedPreferences(
                APP_PREF, Context.MODE_PRIVATE);
        return preference.getLong(key, 0l);
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences preference = context.getSharedPreferences(
                APP_PREF, Context.MODE_PRIVATE);
        return preference.getBoolean(key, false);
    }

    public static void setBoolean(Context context, String key,
                                  boolean value) {
        SharedPreferences preference = context
                .getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences preference = context.getSharedPreferences(
                APP_PREF, Context.MODE_PRIVATE);
        return preference.getInt(key, defaultValue);
    }

    public static int getInt(Context context, String key) {
        return getInt(context, key, 0);
    }

    public static void setInt(Context context, String key,
                              int value) {
        SharedPreferences preference = context
                .getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences preference = context.getSharedPreferences(
                APP_PREF, Context.MODE_PRIVATE);
        return preference.getString(key, "");
    }

    public static void setString(Context context, String key,
                                 String value) {
        SharedPreferences preference = context
                .getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void clearAll(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    public static void clearLoggedInInfo(Context context) {

        SharedPreference.setBoolean(context, AppConstants.PREF_KEY_IS_LOGGED_IN, false);
        SharedPreference.setBoolean(context, AppConstants.PREF_KEY_IS_ACCOUNT_VERIFIED, false);
        /*SharedPreference.setString(context, AppConstants.PREF_KEY_ACCESS_TOKEN, "");
        SharedPreference.setString(context, AppConstants.PREF_KEY_EMAIL_ID, "");
        SharedPreference.setString(context, AppConstants.PREF_KEY_USER_ID, "");*/
    }

    /*public static void updateLoggedInInfo(Context context, Login login, String medium) {
        LocalyticsUtils.updateCustomDimensionRegistrationStatus(context);

        SharedPreference.setString(context, AppConstants.PREF_KEY_REGISTRATION_MEDIUM, medium);
        SharedPreference.setString(context, AppConstants.PREF_KEY_FAME_NAME, login.getFameName());
        SharedPreference.setString(context, AppConstants.PREF_KEY_USER_IMAGE_NAME, login.getImageName());
        SharedPreference.setString(context, AppConstants.PREF_KEY_EMAIL_ID, login.getEmail());
        SharedPreference.setString(context, AppConstants.PREF_KEY_USER_ROLE, login.getRoles());
        SharedPreference.setString(context, AppConstants.PREF_KEY_ACCESS_TOKEN, login.getAccessToken());
        SharedPreference.setString(context, AppConstants.PREF_KEY_USER_ID, login.getUserId());
        SharedPreference.setBoolean(context, AppConstants.PREF_KEY_IS_LOGGED_IN, true);
        SharedPreference.setBoolean(context, AppConstants.PREF_KEY_IS_ACCOUNT_VERIFIED, login.getIsAccountVerified());
        if (login.getDefaultCountryId() > 0)
            SharedPreference.setInt(context, AppConstants.PREF_KEY_DEFAULT_COUNTRY_ID, login.getDefaultCountryId());
        SharedPreference.setInt(context, AppConstants.PREF_KEY_TIME_ZONE_ID, login.getTimeZoneId());
        SharedPreference.setString(context, AppConstants.PREF_KEY_TIME_ZONE_NAME, login.getTimeZoneName());
        PubnubUtils.gcmAddChannel(context, login.getUserChannel());
        SharedPreference.setString(context, AppConstants.PREF_KEY_CHANNEL_NAME, login.getUserChannel());
    }*/
}

