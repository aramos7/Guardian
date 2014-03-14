package com.example.guardian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ViewMapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);
    }

    public void endSession(View view) {
        Intent intent = new Intent(this, SetUpActivity.class);
        RESTfulCommunicator.deleteSession();
        startActivity(intent);
    }
}
