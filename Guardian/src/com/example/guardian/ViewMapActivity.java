package com.example.guardian;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewMapActivity extends Activity {

	SharedPreferences prefs;
	private ListView locationsList;
	private static ArrayAdapter<String> listAdapter;
	private static ArrayList<String> locList;

	private BroadcastReceiver receiver;

	// ViewMapActivity mapActivity
	public ViewMapActivity() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view_map);
		Log.d("SessionID: ", SessionManager.SESSION.getSessionId());

		if (SessionManager.SESSION.getValidated()) {
			// Put session ID in shared preferences
			prefs = getSharedPreferences("sessionID", 0);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("_id", SessionManager.SESSION.getSessionId());
			editor.commit();
		} else {
			// Log.d("Getting to the get Request for ", prefs.getString("_id",
			// ""));
			String sessionID = prefs.getString("_id", "");
			// SessionManager.SESSION.setSessionID(sessionID);
			// RESTfulCommunicator.getSession();
			SessionManager.SESSION = SessionManager
					.load(getApplicationContext());
		}

		// Load previous updates into the List View
		locationsList = (ListView) findViewById(R.id.locationsList);
		locList = new ArrayList<String>() {
			@Override
			public boolean add(String object) {
				super.add(object); // Always returns true for ArrayList
				listAdapter.notifyDataSetChanged();
				return true;
			}
		};
		listAdapter = new ArrayAdapter<String>(this, R.layout.location_row,
				locList);
		// listAdapter = new ArrayAdapter<String>(this, R.layout.location_row,
		// temp);
		locationsList.setAdapter(listAdapter);

		// Sets up the receiver to retrieve the latitude and longitude
		// information from the broadcast
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				double latitude = 0;
				double longitude = 0;
				intent.getDoubleExtra(LocationService.LATITUDE, latitude);
				intent.getDoubleExtra(LocationService.LONGITUDE, longitude);

				String newLocation = "";

				newLocation += "Coordinates: ("
						+ latitude
						+ ", "
						+ longitude
						+ ")\nTime: "
						+ java.text.DateFormat.getDateTimeInstance().format(
								Calendar.getInstance().getTime());

				listAdapter.add(newLocation);
			}
		};

		// Start background service
		startService(new Intent(this, LocationService.class));
	}

	public void endSession(View view) {
		Intent intent = new Intent(this, SetUpActivity.class);
		RESTfulCommunicator.deleteSession();
		// SharedPreferences.Editor editor = prefs.edit();
		// editor.remove("_id");
		// editor.commit();
		stopService(new Intent(this, LocationService.class));
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SessionManager.SESSION.save(getApplicationContext());
	}

	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
				new IntentFilter(LocationService.LOCATION_UI_UPDATE));
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onStop();
	}
}
