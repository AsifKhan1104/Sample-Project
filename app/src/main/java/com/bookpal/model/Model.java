package com.bookpal.model;

/**
 * Base class for all the Model, It has status and message variable which we are getting common in all API response.
 */
public class Model {
    private int status, code;
    private String message;
    private boolean isAccountVerified;
    private int httpStatusCode;

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAccountVerified() {
        return isAccountVerified;
    }

    public void setAccountVerified(boolean isAccountVerified) {
        this.isAccountVerified = isAccountVerified;
    }
}
