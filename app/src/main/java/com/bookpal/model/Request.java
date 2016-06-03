package com.bookpal.model;

import com.bookpal.utility.ApiDetails;
import com.bookpal.utility.Logger;

import java.util.HashMap;

public class Request {

    private final int id;
    private String dialogMessage;
    private boolean showDialog = true;
    private boolean isDialogCancelable = true;
    private String url;
    private HashMap<String, String> deviceInfoHeader = null;
    private HashMap<String, String> paramMap;
    private HttpRequestType requestType;

    public Request(ApiDetails.ACTION_NAME actionName) {
        Logger.i("ordinal value", actionName.ordinal() + "");
        id = actionName.ordinal();
    }

    public boolean isDialogCancelable() {
        return isDialogCancelable;
    }

    public void setIsDialogCancelable(boolean isDialogCancelable) {
        this.isDialogCancelable = isDialogCancelable;
    }

    public int getId() {
        return id;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(HashMap<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public HttpRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(HttpRequestType requestType) {
        this.requestType = requestType;
    }

    public HashMap<String, String> getDeviceInfoHeader() {
        return deviceInfoHeader;
    }

    public void setDeviceInfoHeader(HashMap<String, String> deviceInfoHeader) {
        this.deviceInfoHeader = deviceInfoHeader;
    }

    public enum HttpRequestType {
        GET, POST, CLOUDINARY
    }
}
