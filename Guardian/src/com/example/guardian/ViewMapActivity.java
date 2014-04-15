package com.example.guardian;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService;
import android.util.Log;
import android.view.View;

public class ViewMapActivity extends Activity {

    SharedPreferences prefs;
//    ViewMapActivity mapActivity;

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
}
