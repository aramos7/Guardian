package com.example.guardian;

import org.json.JSONObject;

/**
 * Handler for requests to the API
 */
public interface AsyncResponseHandler {

    public void onSuccess(JSONObject object);
    public void onSuccess();
    public void onFailure();

}
