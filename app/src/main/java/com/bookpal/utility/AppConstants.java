package com.bookpal.utility;

/**
 * Created by Asif on 25-03-2016.
 */
public interface AppConstants {

    String PREF_KEY_IS_LOGGED_IN = "isLoggedIn";
    String PREF_KEY_IS_ACCOUNT_VERIFIED = "isAccountVerified";
    String PREF_KEY_ACCESS_TOKEN = "accessToken";
    String PREF_KEY_BOOK_COUNT = "bookCount";

    String GOOGLE_LOGIN_SERVER_CLIENT_ID = "802782527209-edc7ejsqkiq51j5cg3u7afgfcdhj6jce.apps.googleusercontent.com";

    String FROM_SIGN_UP_OR_SIGN_IN = "fromSignUpOrSignIn";

    String FLAG_YES = "yes";

    //user's data
    String USER_NAME = "user_name";
    String USER_ID = "user_id";
    String USER_MOBILE = "user_mobile";
    String USER_EMAIL = "user_email";
    String USER_LAT = "user_lat";
    String USER_LONG = "user_long";
    String USER_PINCODE = "user_pin_code";

    String TYPE_PAPERBACK = "Paperback";
    String TYPE_HARDCOVER = "Hardcover";

    // book data
    String BOOK_NAME = "bookName";
    String AUTHOR_NAME = "authorName";
    String ISBN = "isbn";
    String PUBLISHING_YEAR = "publishingYear";
    String LANGUAGE = "language";
    String BOOK_TYPE = "bookType";
}
