package com.bookpal.parser;


import com.bookpal.model.Model;

import org.json.JSONException;
import org.json.JSONObject;

public interface Parser<T extends Model> {
    T parse(JSONObject json) throws JSONException;
}
