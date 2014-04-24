package com.example.guardian;

import android.location.Location;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

//Asynchttpclient
import com.loopj.android.http.*;

/**
 * Created to simply authenticate a session. Thanks.
 * 
 * @author Death (Armando Ramos)
 * @date Feb. 28, 2014
 */
public class RESTfulCommunicator {

	private final static String BASE_URL = "https://guardian-11570.onmodulus.net/";
    public static BasicCookieStore cookieStore = new BasicCookieStore();

    /**
	 * Determines whether a credential is valid or not.
	 * 
	 * @param email
	 *            Email of user.
	 * @param password
	 *            Password of user.
	 * @return True is valid, false if not.
	 */
    public static void checkLoginCredentials(final String email, final String password, final JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setCookieStore(cookieStore);
        client.post(BASE_URL + "api/login", new RequestParams("email", email, "password", password), new AsyncHttpResponseHandler() {
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, String responseBody) {
                Log.d("responseBody", responseBody.toString());
                if (statusCode == 200) {
                    if (responseBody.toString().equals("true")) {
                        if (handler != null) handler.onSuccess();
                        SessionManager.SESSION.setValidated(true);
                        //Save cookie on login to reuse for the session
                        SessionManager.SESSION.setCookieStore(cookieStore);
                    }
                    else
                        if (handler != null) handler.onFailure();
                }
                else
                    if (handler != null) handler.onFailure();
            }
        });

    }
//	public static void checkLoginCredentials(final String email,
//                                                       final String password,
//                                                       final JsonHttpResponseHandler handler) {
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    // Create a new HttpClient and Post Header
//                    HttpClient httpclient = new DefaultHttpClient();
//                    HttpPost httppost = new HttpPost(BASE_URL + "api/login");
//                    HttpContext httpContext = new BasicHttpContext();
//                    //Log.d("Cookie store", cookieStore.toString());
//
//                    httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
//
//                    // Add your data
//                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//                    nameValuePairs.add(new BasicNameValuePair("email", email));
//                    nameValuePairs.add(new BasicNameValuePair("password", password));
//                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                    // Execute HTTP Post Request
//                    HttpResponse response = httpclient.execute(httppost, httpContext);
//                    String responseStr = EntityUtils.toString(response.getEntity());
//
//                    if (responseStr.equals("true")) {
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("session_id", responseStr);
//                        SessionManager.SESSION.setHttpContext(httpContext);
//                        SessionManager.SESSION.setValidated(true);
//                        //Save cookie on login to reuse for the session
//                        SessionManager.SESSION.setCookieStore(cookieStore);
//                        //Log.d("Cookie store after login", cookieStore.toString());
//
//                        if (handler != null) {
//                            handler.onSuccess(jsonObject);
//                        }
//                    }
//                    else {
//                        if (handler != null) {
//                            handler.onFailure();
//                        }
//                    }
//                } catch (ClientProtocolException e) {
//                    // TODO Auto-generated catch block
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                } catch (JSONException e) {}
//            }
//        });
//        thread.start();
//	}

    /**
     * Post request to send Locations of the user
     */
    public static void postLocation (final Location location)
    {
        Thread t = new Thread() {
            public void run() {
                // Create a new HttpClient and Post Header
                String resource = "api/updateLocationsArrayForSession/";
                resource += SessionManager.SESSION.getSessionId();
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(BASE_URL + resource);
                JSONObject json = new JSONObject();

                try {
                    // Add your data
                    JSONObject info = new JSONObject();
                    info.put("latitude", location.getLatitude());
                    info.put("longitude", location.getLongitude());
                    info.put("timeStamp", System.currentTimeMillis());

                    json.put("location", info);

                    //Setting the entities and content
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    httppost.setEntity(se);

                    //JSONObject curr = null;
                    HttpContext httpContext = new BasicHttpContext();
                    httpContext.setAttribute(ClientContext.COOKIE_STORE, SessionManager.SESSION.getCookieStore());
                    HttpResponse response = httpclient.execute(httppost, httpContext);
                    //curr = httpResponseToJSONObject(response);

                    //Log.d("Response ~~~~~~~", curr.toString());
                    //Check the response for the update on the app
                    //String responseStr = EntityUtils.toString(response.getEntity());
                    //if (responseStr.equals("true")) {
                    SessionManager.SESSION.updateLocationsArray(location);
                        // Adding to the ViewMapActivity
//                    }
//                    else
//                    {
//                        //warn user
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    /**
     * Post request to create a session for the user
     */
    public static void createSession(final Long startDate, final Long endDate, final ArrayList<Guardian> guardians) {
        Thread t = new Thread() {

            public void run() {
                // Create a new HttpClient and Post Header
                String resource = "api/createSession";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(BASE_URL + resource);
                JSONObject json = new JSONObject();

                try {
                    // Add your data
                    json.put("startDate", startDate);
                    json.put("endDate", endDate);

                    //Adding the end location data
                    JSONObject endLoc = new JSONObject();
                    endLoc.put("latitude", "37.42291810");
                    endLoc.put("longitude", "-122.08542120");
                    json.put("finalLocation", endLoc);
                    json.put("locationArray", new JSONArray());

                    //Adding the guardian information
                    JSONArray guardiansJSON = new JSONArray();
                    for (Guardian g: guardians) {
                        JSONObject gContact = new JSONObject();
                        gContact.put("name", g.getName());
                        gContact.put("phone", g.getPhoneNumber());
                        gContact.put("email", g.getEmail());
                        gContact.put("status", "pending");
                        gContact.put("smsUpdates", "true");
                        guardiansJSON.put(gContact);
                    }
                    json.put("guardianContactArray", guardiansJSON);

                    //Setting the entities and content
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    httppost.setEntity(se);

                    // Execute HTTP Post Request
                    HttpContext httpContext = new BasicHttpContext();
                    httpContext.setAttribute(ClientContext.COOKIE_STORE, SessionManager.SESSION.getCookieStore());
                    HttpResponse response = httpclient.execute(httppost, httpContext);
                    JSONObject curr = httpResponseToJSONObject(response);

                    //Save the JSON response
                    SessionManager.SESSION.setSessionID(curr.getString("_id"));
                    SessionManager.SESSION.setStartDate(startDate);
                    SessionManager.SESSION.setEndDate(endDate);
                    //Save session ID in shared preference
                    //SessionManager.SESSION.saveSessionID(curr.getString("_id"));
                    Log.d("Session ID", curr.getString("_id"));

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    /**
     * Get Session request
     */
    public static void getSession()
    {
        Thread t = new Thread() {
            public void run() {
                // Create a new HttpClient and Post Header
                String resource = "api/getSession/";

                //Getting session ID from shared preferences when you have logged in and you reopen the app
                String sessionID = SessionManager.SESSION.getSessionId();
                resource += sessionID;

                try {
                    HttpClient httpclient = new DefaultHttpClient();

                    HttpGet request = new HttpGet();
                    URI website = new URI(BASE_URL + resource);
                    request.setURI(website);
                    HttpResponse response = httpclient.execute(request);

                    JSONObject curr = httpResponseToJSONObject(response);
                    Log.d("Get Session Response ~~~~~~~", curr.toString());

                    //Validate the login session the service should be running
                    SessionManager.SESSION.setValidated(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    /**
     * Post request to delete a session for the user
     */
    public static void deleteSession() {
        Thread t = new Thread() {
            public void run() {
                // Create a new HttpClient and Post Header
                String resource = "api/deleteSession/";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(BASE_URL + resource + SessionManager.SESSION.getSessionId());

                try {
                    // Execute HTTP Post Request
                    HttpContext httpContext = new BasicHttpContext();
                    httpContext.setAttribute(ClientContext.COOKIE_STORE, SessionManager.SESSION.getCookieStore());
                    HttpResponse response = httpclient.execute(httppost, httpContext);

                    if (response.equals("Session Removed")) {
                        Log.d("Session Removed", "~~~~~~~~~");
                        //Find way to eliminate session
                        SessionManager.SESSION.setSessionID("");
                        //Delete Session form shared prefs
                        //SessionManager.SESSION.deleteSessionID();
                    }

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    public static JSONObject httpResponseToJSONObject(HttpResponse response) {
        JSONObject curr = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            curr = new JSONObject(builder.toString());
        }
        catch (Exception e) {

        }
        return curr;
    }
}
