package com.example.guardian;

import org.json.JSONObject;

/**
 * Handler for requests to the API
 */
public interface JsonHttpResponseHandler {

    public void onSuccess(JSONObject object);
    public void onFailure();

}
