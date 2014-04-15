package com.example.guardian;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewMapActivity extends Activity {

    SharedPreferences prefs;
    private ListView locationsList;
    private static ArrayAdapter<Location> listAdapter;
    private static ArrayList<Location> locList;
//    ViewMapActivity mapActivity;

    public ViewMapActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);
        //mapActivity = new ViewMapActivity();
        Log.d("SessionID: ", SessionManager.SESSION.getSessionId());
        if (SessionManager.SESSION.getValidated()) {
            //Put session ID in shared preferences
            prefs = getSharedPreferences("sessionID", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("_id", SessionManager.SESSION.getSessionId());
            editor.commit();
        }
        else {
            //Log.d("Getting to the get Request for ", prefs.getString("_id", ""));
            String sessionID = prefs.getString("_id", "");
            //SessionManager.SESSION.setSessionID(sessionID);
            //RESTfulCommunicator.getSession();
            SessionManager.SESSION = SessionManager.load(getApplicationContext());
        }

        //Load previous updates into the List View
        locationsList = (ListView) findViewById(R.id.locationsList);
        locList = new ArrayList<Location>() {
            @Override
            public boolean add(Location object) {
                super.add(object); // Always returns true for ArrayList
                listAdapter.notifyDataSetChanged();
                return true;
        }};
        listAdapter = new ArrayAdapter<Location>(this, R.layout.location_row, locList);
        //listAdapter = new ArrayAdapter<String>(this, R.layout.location_row, temp);
        locationsList.setAdapter(listAdapter);

        //Start background service
        startService(new Intent(this, LocationService.class));
    }

    public void endSession(View view) {
        Intent intent = new Intent(this, SetUpActivity.class);
        RESTfulCommunicator.deleteSession();
        //SharedPreferences.Editor editor = prefs.edit();
        //editor.remove("_id");
        //editor.commit();
        stopService(new Intent(this, LocationService.class));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SessionManager.SESSION.save(getApplicationContext());
    }

    public static void addLocation(final Location location) {
        locList.add(location);
    }
}
