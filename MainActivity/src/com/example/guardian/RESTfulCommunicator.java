package com.example.guardian;

import android.location.Location;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
	public static void checkLoginCredentials(final String email,
                                                       final String password,
                                                       final JsonHttpResponseHandler handler) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create a new HttpClient and Post Header
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(BASE_URL + "api/login");
                    HttpContext httpContext = new BasicHttpContext();

                    httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("email", email));
                    nameValuePairs.add(new BasicNameValuePair("password", password));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost, httpContext);
                    String responseStr = EntityUtils.toString(response.getEntity());

                    if (responseStr.equals("true")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("session_id", responseStr);
                        SessionManager.SESSION.setHttpContext(httpContext);
                        if (handler != null) {
                            handler.onSuccess(jsonObject);
                        }
                    }
                    else {
                        if (handler != null) {
                            handler.onFailure();
                        }
                    }
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                } catch (JSONException e) {}
            }
        });
        thread.start();
	}

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
                    Log.d("Latitude", Double.toString(location.getLatitude()));
                    Log.d("Longitude", Double.toString(location.getLongitude()));
                    Log.d("timeStamp", Long.toString(System.currentTimeMillis()));

                    JSONObject info = new JSONObject();
                    info.put("latitude", location.getLatitude());
                    info.put("longitude", location.getLongitude());
                    info.put("timeStamp", System.currentTimeMillis());

                    json.put("location", info);

                    //Setting the entities and content
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    httppost.setEntity(se);

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost, SessionManager.SESSION.getHttpContext());
                    JSONObject curr = httpResponseToJSONObject(response);

                    Log.d("Response ~~~~~~~", curr.toString());

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
                    HttpResponse response = httpclient.execute(httppost, SessionManager.SESSION.getHttpContext());
                    JSONObject curr = httpResponseToJSONObject(response);

                    //Save the JSON response
                    SessionManager.SESSION.setSessionID(curr.getString("_id"));

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
                    HttpResponse response = httpclient.execute(httppost, SessionManager.SESSION.getHttpContext());
                    if (response.equals("Session Removed")) {
                        Log.d("Session Removed", "~~~~~~~~~");
                        //Find way to eliminate session
                        SessionManager.SESSION.setSessionID("");
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
