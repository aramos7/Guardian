package com.example.guardian;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.os.AsyncTask;

/**
 * Created to simply authenticate a session. Thanks.
 * 
 * @author Death (Armando Ramos)
 * @date Feb. 28, 2014
 */
public class RESTfulCommunicator {

	private final static String API_URL = "htpp://localhost.com:3000/api/";

	/**
	 * Determines whether a credential is valid or not.
	 * 
	 * @param email
	 *            Email of user.
	 * @param password
	 *            Password of user.
	 * @return True is valid, false if not.
	 */
	public static SessionManager checkLoginCredentials(String email,
			String password) {

		String urlString = "";
		// new CallAPI().execute(urlString);

		return new SessionManager(email, password, false);
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
