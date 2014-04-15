package com.example.guardian;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Samarth on 4/13/14.
 */
public class LocationService extends Service {

    private static final String TAG = "Location Service";
    private LocationManager locMgr;
    Notification note;

    LocationListener onLocationChange = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }

        public void onProviderDisabled(String provider) {
            // required for interface, not used
        }

        public void onProviderEnabled(String provider) {
            // required for interface, not used
        }

        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            // required for interface, not used
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");

        //Location Manager
        locMgr = (LocationManager)getSystemService(LOCATION_SERVICE);
        locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, onLocationChange);

        note = new Notification(R.drawable.ic_launcher,
                "Session Started",
                SessionManager.SESSION.getStartDate());
        Intent i = new Intent(this, ViewMapActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi=PendingIntent.getActivity(this, 0,
                i, 0);

        DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        note.setLatestEventInfo(this, "Session Running",
                "Last Update: " + date,
                pi);
        note.flags|=Notification.FLAG_NO_CLEAR;

        startForeground(1337, note);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");

        locMgr.removeUpdates(onLocationChange);
    }

    private void updateLocation(Location loc) {
        try {
            //Post Location Request
            RESTfulCommunicator.postLocation(loc);
            note = new Notification(R.drawable.ic_launcher,
                    "Session Started",
                    SessionManager.SESSION.getStartDate());
            Intent i = new Intent(this, ViewMapActivity.class);

            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pi=PendingIntent.getActivity(this, 0,
                    i, 0);

            DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            note.setLatestEventInfo(this, "Session Running",
                    "Last Update: " + date,
                    pi);
            note.flags|=Notification.FLAG_NO_CLEAR;

            startForeground(1337, note);
        }
        catch (Throwable t) {
            android.util.Log.e("SendingLocation", "Exception fetching data", t);
        }
    }
}
