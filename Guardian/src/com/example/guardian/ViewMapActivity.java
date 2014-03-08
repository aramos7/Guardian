package com.example.guardian;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class ViewMapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_map);
		
		// Get a handle to the Map Fragment
		// GoogleMap map = ( (MapFragment) getFragmentManager().findFragmentById(R.id.map).getMap());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_map, menu);
		return true;
	}

}
