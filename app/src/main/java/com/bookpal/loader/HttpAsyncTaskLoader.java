package com.bookpal.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.bookpal.R;
import com.bookpal.model.Model;
import com.bookpal.model.Request;
import com.bookpal.model.Response;
import com.bookpal.parser.Parser;
import com.bookpal.utility.ApiDetails;
import com.bookpal.utility.AppConstants;
import com.bookpal.utility.Logger;
import com.bookpal.utility.SharedPreference;
import com.bookpal.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HttpAsyncTaskLoader extends AsyncTaskLoader<Model> {
    private Request request;
    private Parser parser;
    private Context context;
    private Response serverResponse;

    public HttpAsyncTaskLoader(Context context) {
        super(context);
    }

    public HttpAsyncTaskLoader(Context context, Request request, Parser parser) {
        super(context);
        this.context = context;
        this.request = request;
        this.parser = parser;
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public Model loadInBackground() {
        Utility utility = new Utility(context);
        switch (request.getRequestType()) {
            case POST:
                serverResponse = utility.doPost(request.getUrl(), getJsonParam(request.getParamMap(), true), request.getDeviceInfoHeader());
                break;
            case CLOUDINARY:
                /*String action = request.getParamMap().get("action");
                Request tempRequest = request;
                tempRequest.getParamMap().remove("action");
                if (ApiDetails.ACTION_NAME.UPLOAD.name().equals(action)) {
                    String imagePath = tempRequest.getParamMap().get("imagePath");
                    tempRequest.getParamMap().remove("imagePath");
                    try {
                        serverResponse = utility.uploadOnCloudinary(tempRequest.getParamMap(), imagePath);
                    } catch (Exception ex) {
                    }
                } else {
                    serverResponse = utility.destroyImage(tempRequest.getParamMap());
                }*/
                break;
            case GET:
                StringBuilder urlBuilder = new StringBuilder(request.getUrl());
                if (request.getParamMap() != null && !request.getParamMap().isEmpty()) {
                    urlBuilder.append('?')
                            .append(urlEncodeUTF8(request.getParamMap()));
                }
                serverResponse = utility.doGet(context, urlBuilder.toString());
                break;
        }
        if (serverResponse != null) {
            Model model = parseResponse(serverResponse);
            return model;
        } else {
            return serverResponse;
        }
    }

    private Model parseResponse(Response serverResponse) {
        if (serverResponse.isError()) {
            Model model = new Model();
            model.setStatus(0);
            model.setMessage(serverResponse.getErrorMsg());
            model.setHttpStatusCode(serverResponse.getHttpStatusCode());
            return model;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(serverResponse.getResponse());
                Model model = parser.parse(jsonObject);
                return model;
            } catch (JSONException e) {
                e.printStackTrace();
                Logger.i("exception", e.toString());
                Model model = new Model();
                model.setStatus(0);
                model.setMessage(context.getString(R.string.msg_5xx));
                return model;
            }
        }
    }

    @Override
    public void deliverResult(Model data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(Model apps) {
        super.onCanceled(apps);
        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
    }

    /*
        convert a hashmap into a jSonObject(recusively).
        A hashmap object is converted into JSonObject
        A ArrayList<Hashmap> is converted into a jSonArray of JSonObjects
        To pass the jsonarray inside the jsonobject put the arraylist in hashmap.
     */

    public JSONObject getJsonParam(HashMap<String, ?> paramMap, boolean addKeys) {
        String temp;
        if (paramMap == null) {
            return null;
        }
        JSONObject jsonObject;
        if (addKeys) {
            jsonObject = getKeysJson();
        } else {
            jsonObject = new JSONObject();
        }
        for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
            try {

                if (entry.getValue() instanceof String) {
                    temp = (String) entry.getValue();
                    if (temp != null) {
                        if (temp.startsWith("[")
                                && temp.endsWith("]")) {
                            JSONArray jsonArray = new JSONArray(temp);
                            jsonObject.put(entry.getKey(), jsonArray);
                        } else {
                            jsonObject.put(entry.getKey(), temp);
                        }
                    }
                } else if (entry.getValue() instanceof HashMap) {
                    jsonObject.put(entry.getKey(), getJsonParam((HashMap) entry.getValue(), false));
                } else if (entry.getValue() instanceof ArrayList) {
                    temp = (String) entry.getKey();
                    ArrayList list = (ArrayList) entry.getValue();
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < list.size(); i++) {
                        jsonArray.put(getJsonParam((HashMap) list.get(i), false));
                    }
                    jsonObject.put(entry.getKey(), jsonArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private JSONObject getKeysJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("apiVersion", ApiDetails.API_VERSION);
            jsonObject.put("appKey", ApiDetails.APP_KEY);
            jsonObject.put(ApiDetails.REQUEST_KEY_ACCESS_TOKEN, SharedPreference.getString(context, AppConstants.PREF_KEY_ACCESS_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return s;
        }
    }

    private StringBuilder urlEncodeUTF8(Map<?, ?> map) {
        StringBuilder paramBuilder = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (paramBuilder.length() > 0) {
                paramBuilder.append("&");
            }
            paramBuilder.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return paramBuilder;
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(Model model) {
        model = null;
    }
}
