package com.example.guardian;

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
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
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

	private final static String BASE_URL = "http://guardian-11570.onmodulus.net/";
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

                    httpContext.setAttribute(ClientContext.COOKIE_STORE, RESTfulCommunicator.cookieStore);

                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("email", email));
                    nameValuePairs.add(new BasicNameValuePair("password", password));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost, httpContext);
                    String responseStr = EntityUtils.toString(response.getEntity());
                    Log.d("ResponseString", responseStr);

                    if (responseStr.equals("true")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("session_id", responseStr);
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

	public static Guardian[] getUserGuardians(String email, String password) {
		return null;
	}

	public JSONArray getResponse(HttpResponse response)
			throws UnsupportedEncodingException, IllegalStateException,
			IOException, JSONException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		for (String line = null; (line = reader.readLine()) != null;) {
			builder.append(line).append("\n");
		}
		JSONTokener tokener = new JSONTokener(builder.toString());
		JSONArray finalResult = new JSONArray(tokener);
		return finalResult;
	}
}
