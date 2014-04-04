package com.example.guardian;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ViewMapActivity extends Activity {

    LocationListener onLocationChange=new LocationListener() {
        public void onLocationChanged(Location location) {
            //updateLocation(location);
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

    private LocationManager locMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);

        //Location Manager
        locMgr=(LocationManager)getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d("Request update", "~~~~~~~~~~~~~~");
        locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0,onLocationChange);
    }

    @Override
    public void onPause() {
        super.onPause();
        locMgr.removeUpdates(onLocationChange);
    }

    public void endSession(View view) {
        Intent intent = new Intent(this, SetUpActivity.class);
        RESTfulCommunicator.deleteSession();
        startActivity(intent);
    }

    private void updateLocation(Location loc) {
        try {
            RESTfulCommunicator.postLocation(loc);
        }
        catch (Throwable t) {
            android.util.Log.e("SendingLocation", "Exception fetching data", t);
            Toast.makeText(this, "Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void sendUpdateLocation(View view) {
        Location location = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        try {
            RESTfulCommunicator.postLocation(location);
        }
        catch (Throwable t) {
            android.util.Log.e("SendingLocation", "Exception fetching data", t);
            Toast.makeText(this, "Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
