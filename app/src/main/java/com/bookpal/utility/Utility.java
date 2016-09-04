package com.bookpal.utility;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.bookpal.R;
import com.bookpal.model.Response;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Created by Asif on 25-03-2016.
 */
public class Utility {
    private Context mContext;
    private final int timeoutConnection = 60 * 1000; //in Miliseconds
    public static String TAG = "";

    public Utility(Context context) {
        this.mContext = context;
    }

    public static boolean isLoggedIn(Context context) {
        return SharedPreference.getBoolean(context, AppConstants.PREF_KEY_IS_LOGGED_IN);
    }

    public static void setLoggedIn(Context context) {
        SharedPreference.setBoolean(context, AppConstants.PREF_KEY_IS_LOGGED_IN, true);
    }

    public static void showToastMessage(Context context, String msg) {
        Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
    }

    public Response doPost(String url, JSONObject jsonObject, HashMap<String, String> deviceInfoHeader) {
        Logger.e("url", url);
        Response response = new Response();
        if (!isNetworkConnected(mContext)) {
            String error = mContext.getResources().getString(R.string.no_internet_connection);
            response.setError(true);
            response.setErrorMsg(error);
            return response;
        }
        try {
            String encodeUrl = encodeURL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(encodeUrl).openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            //urlConnection.setRequestProperty("Accept-Language", getLocaleLanguageTag(context));
            //urlConnection.setRequestProperty("Content-Country", getUserViewingLocationId(context));
            //urlConnection.setRequestProperty("Content-Region", getUserViewingLocationId(context));
            urlConnection.setRequestProperty("Accept-Encoding", "gzip");
            if (jsonObject.has("accessToken")) {
                if (!jsonObject.getString("accessToken").isEmpty()) {
                    urlConnection.setRequestProperty("X-Auth-Token", jsonObject.getString("accessToken"));
                    Logger.i("X-Auth-Token", jsonObject.getString("accessToken"));
                }
                jsonObject.remove("accessToken");
            }
            //set device info in header
            if (deviceInfoHeader != null) {
                for (String key : deviceInfoHeader.keySet()) {
                    urlConnection.setRequestProperty(key, deviceInfoHeader.get(key));
                }
            }
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(timeoutConnection);
            printRequestProperties(urlConnection);
            Logger.e("API Url::", url + " " + jsonObject.toString());
            OutputStream os = urlConnection.getOutputStream();
            os.write(jsonObject.toString().getBytes());
            os.flush();
            generateResponse(response, urlConnection);
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            getDefaultResponse(response, mContext.getString(R.string.msg_5xx));
        }
        printResponse(response.getResponse());
        return response;
    }

    public Response doGet(Context context, String url) {
        Logger.e("url", url);
        Response response = new Response();
        if (!isNetworkConnected(mContext)) {
            String error = mContext.getResources().getString(R.string.no_internet_connection);
            response.setError(true);
            response.setErrorMsg(error);
            return response;
        }
        try {
            String encodeUrl = encodeURL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(encodeUrl).openConnection();
            urlConnection.setDoOutput(false);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            //urlConnection.setRequestProperty("Accept-Language", getLocaleLanguageTag(this.context));
            //urlConnection.setRequestProperty("Content-Country", getUserViewingLocationId(this.context));
            //Added for Regionalization
            //urlConnection.setRequestProperty("Content-Region", getUserViewingLocationId(context));
            urlConnection.setRequestProperty("Accept-Encoding", "gzip");
            String xAuthToken = SharedPreference.getString(context, AppConstants.PREF_KEY_ACCESS_TOKEN);
            if (!xAuthToken.isEmpty()) {
                urlConnection.setRequestProperty("X-Auth-Token", xAuthToken);
                Logger.i("X-Auth-Token", xAuthToken);
            }
            urlConnection.setRequestProperty("appKey", ApiDetails.APP_KEY);

            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(timeoutConnection);

            printRequestProperties(urlConnection);
           /* OutputStream os = urlConnection.getOutputStream();
            os.flush();*/
            generateResponse(response, urlConnection);
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            getDefaultResponse(response, mContext.getString(R.string.msg_5xx));
        }
        printResponse(response.getResponse());
        return response;
    }

    public String encodeURL(String url) {
        return url.replaceAll(" ", "%20");
    }

    public void printResponse(String response) {
        Logger.i("Utility.java", "Api Hit Response : " + response);
    }

    private void printRequestProperties(HttpURLConnection httpURLConnection) {
        Map<String, List<String>> requestProperties = httpURLConnection.getRequestProperties();
        Set<String> propertyKeys = requestProperties.keySet();
        Logger.i("--------------------", "--------------------");
        for (String key : propertyKeys) {
            List<String> value = requestProperties.get(key);
            Logger.i("PROPERTY", "Key: " + key + " | Value: " + value);
        }
    }

    private void printHeaderContent(URLConnection urlConnection) {
        Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
        Set<String> headerKeys = headerFields.keySet();

        for (String key : headerKeys) {
            List<String> value = headerFields.get(key);
            Logger.i("HEADER", "Key: " + key + " | Value: " + value);
        }
        Logger.i("--------------------", "--------------------");
    }

    private void generateResponse(Response response, HttpURLConnection urlConnection) throws IOException {
        final int responseCode = urlConnection.getResponseCode();
        printHeaderContent(urlConnection);
        Logger.i(TAG, "Response Code: " + responseCode);
        response.setHttpStatusCode(responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String result = "";
            Logger.e("content encoding", urlConnection.getContentEncoding() + "");
            if (urlConnection.getContentEncoding() != null && urlConnection.getContentEncoding().equalsIgnoreCase("gzip")) {
                result = readStream(new GZIPInputStream(urlConnection.getInputStream()));
            } else {
                result = readStream(urlConnection.getInputStream());
            }

            response.setError(false);
            response.setResponse(result);
        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            getDefaultResponse(response, mContext.getString(R.string.msg_401));
        } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            getDefaultResponse(response, mContext.getString(R.string.msg_404));
        } else if (responseCode >= 400 && responseCode < 500) {
            getDefaultResponse(response, mContext.getString(R.string.msg_4xx));
        } else {
            getDefaultResponse(response, mContext.getString(R.string.msg_5xx));
        }
    }

    public Response getDefaultResponse(Response response, String message) {
        response.setError(true);
        response.setErrorMsg(message);

        return response;
    }

    /*
    *
    * Below Method is used for the utility of the get and post method.
     */
    String readStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        reader.close();
        return out.toString();
    }

    /**
     * To check device has internet
     *
     * @param context
     * @return boolean as per status
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static void saveUserDataToSharedPreference(Context context, String userName, String uid, String userMobile, String userEmail, String userPinCode) {
        SharedPreference.setString(context, AppConstants.USER_NAME, userName);
        SharedPreference.setString(context, AppConstants.USER_ID, uid);
        SharedPreference.setString(context, AppConstants.USER_MOBILE, userMobile);
        SharedPreference.setString(context, AppConstants.USER_EMAIL, userEmail);
        SharedPreference.setString(context, AppConstants.USER_PINCODE, userPinCode);
    }

    public void copyDataBase() throws IOException {
        String DB_NAME = "UserAddress";
        String DB_PATH = mContext.getDatabasePath(DB_NAME).getPath();

        InputStream is = mContext.getAssets().open("UserAddress.db");

        String outFileName = DB_PATH;
        try {
            OutputStream out = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            is.close();
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
