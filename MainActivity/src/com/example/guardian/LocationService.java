package com.example.guardian;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.location.Address;

/**
 * Created by Samarth on 4/13/14.
 */
public class LocationService extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private static final String TAG = "Location Service";
	// private LocationManager locMgr;
	private LocationClient locationClient;
	private LocationRequest locationRequest;
	private Geocoder geocoder;

	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;

	Notification note;

	public static final String BROADCAST_ACTION = "com.websmithing.broadcasttest.displayevent";
	private final Handler handler = new Handler();
	Intent intent;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();

		// Location Client
		locationClient = new LocationClient(this, this, this);

		locationRequest = new LocationRequest();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(UPDATE_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);

		locationClient.connect();

		intent = new Intent(BROADCAST_ACTION);

		// Geocoder
		geocoder = new Geocoder(this, Locale.getDefault());

		// Notification
		note = new Notification(R.drawable.ic_launcher, "Session Started",
				SessionManager.SESSION.getStartDate());
		Intent i = new Intent(this, ViewMapActivity.class);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

		DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
		String date = df.format(Calendar.getInstance().getTime());
		note.setLatestEventInfo(this, "Session Running",
				"Last Update: " + date, pi);
		note.flags |= Notification.FLAG_NO_CLEAR;

		startForeground(1337, note);
	}

	@Override
	public void onStart(Intent intent, int startId) {
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");

		// locMgr.removeUpdates(onLocationChange);
		locationClient.removeLocationUpdates(this);
	}

	@SuppressWarnings("deprecation")
	private void updateLocation(final Location loc) {
		try {
			// Post Location Request
			// Log.d("Executing post request", "~~~~~~~~");
			RESTfulCommunicator.postLocation(this, loc,
					new AsyncResponseHandler() {
						@Override
						public void onSuccess(JSONObject object) {

						}

						@Override
						public void onSuccess() {
							displayLoggingInfo(loc);
						}

						@Override
						public void onFailure() {

						}
					});
			note = new Notification(R.drawable.ic_launcher, "Session Started",
					SessionManager.SESSION.getStartDate());
			Intent i = new Intent(this, ViewMapActivity.class);

			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

			DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
			String date = df.format(Calendar.getInstance().getTime());
			note.setLatestEventInfo(this, "Session Running", "Last Update: "
					+ date, pi);
			note.flags |= Notification.FLAG_NO_CLEAR;

			startForeground(1337, note);
		} catch (Throwable t) {
			android.util.Log.e("SendingLocation", "Exception fetching data", t);
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		updateLocation(arg0);

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		locationClient.requestLocationUpdates(locationRequest, this);

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	private void displayLoggingInfo(Location location) {
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(location.getLatitude(),
					location.getLongitude(), 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// building the address as a string
		Address address = addresses.get(0);
		String addressText = String.format("%s,  %s, %s", address
				.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
				address.getLocality(), address.getCountryName());

		intent.putExtra("location", addressText);
		sendBroadcast(intent);
	}
}
