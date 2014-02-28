package com.example.guardian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Attempts to log user into app. If successful, it will move on the set up
	 * activity. If not successful, it will ask the user to try again.
	 * 
	 * @param view
	 */
	public void checkLogIn(View view) {

	}

	/**
	 * Shifts the activity to the register activity if the user would like to
	 * register an account with the company.
	 * 
	 * @param view
	 */
	public void moveToRegisterActivity(View view) {

	}
}
