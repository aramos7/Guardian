package com.example.guardian;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import org.json.JSONObject;

public class ViewMapActivity extends Activity {

	// SharedPreferences prefs;
	private ListView locationsList;
	private static ArrayAdapter<String> listAdapter;
	private static ArrayList<String> locList;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}
	};

	public void updateUI(Intent intent) {
		String location = intent.getStringExtra("location");
		locList.add(location);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view_map);

		if (!SessionManager.SESSION.getValidated()) {
			SessionManager.SESSION = SessionManager
					.load(getApplicationContext());
		}
		
		//Receiver registration
		registerReceiver(broadcastReceiver, new IntentFilter(LocationService.BROADCAST_ACTION));

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
		finish();
	}
	
	public void alertPanic(View view) {
		RESTfulCommunicator.alertPanic(new AsyncResponseHandler() {
			@Override
			public void onSuccess(JSONObject object) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(ViewMapActivity.this,
						"Panic Alert sent",
						Toast.LENGTH_LONG);
				toast.show();
				
			}
			@Override
			public void onFailure() {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SessionManager.SESSION.save(getApplicationContext());
	}
}
