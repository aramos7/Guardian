package com.example.guardian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class ViewMapActivity extends Activity {

	//SharedPreferences prefs;
	private ListView locationsList;
	private static ArrayAdapter<String> listAdapter;
	private static ArrayList<String> locList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view_map);
		Log.d("SessionID: ", SessionManager.SESSION.getSessionId());

		if (SessionManager.SESSION.getValidated()) {
			// Put session ID in shared preferences
			//prefs = getSharedPreferences("sessionID", 0);
			//SharedPreferences.Editor editor = prefs.edit();
			//editor.putString("_id", SessionManager.SESSION.getSessionId());
			//editor.commit();
		} else {
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
		locationsList.setAdapter(listAdapter);

		// Start background service
		startService(new Intent(this, LocationService.class));
	}

	public void endSession(View view) {
		Intent intent = new Intent(this, SetUpActivity.class);
		RESTfulCommunicator.deleteSession();
		stopService(new Intent(this, LocationService.class));
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SessionManager.SESSION.save(getApplicationContext());
	}
}
