package com.example.guardian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
		
		Intent intent = new Intent(this, SetUpActivity.class);
		
		EditText usernameBox = (EditText) findViewById(R.id.login_username_textbox);
		EditText passwordBox = (EditText) findViewById(R.id.login_password_textbox);
		
		String username = usernameBox.getText().toString();
		String password = passwordBox.getText().toString();
		
		SessionManager.SESSION = RESTfulCommunicator.checkLoginCredentials(username, password);
		
		if (!SessionManager.SESSION.isItValidated()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Invalid User name or password.")
			       .setCancelable(false)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // Nothing, just die. Haha
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			startActivity(intent);
		}
	}

	/**
	 * Shifts the activity to the register activity if the user would like to
	 * register an account with the company.
	 * 
	 * @param view
	 */
	public void moveToRegisterActivity(View view) {
		
		Intent intent = new Intent(this, RegisterActivity.class);
		
		startActivity(intent);		
	}
}