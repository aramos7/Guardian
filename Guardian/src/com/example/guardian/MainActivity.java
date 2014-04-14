package com.example.guardian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        if (!RESTfulCommunicator.cookieStore.toString().equals("[]")) {
            Intent intent = new Intent(this, ViewMapActivity.class);
            startActivity(intent);
            finish();
        }
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

		final Intent intent = new Intent(this, SetUpActivity.class);

		EditText usernameBox = (EditText) findViewById(R.id.login_username_textbox);
		EditText passwordBox = (EditText) findViewById(R.id.login_password_textbox);

		final String email = usernameBox.getText().toString();
		String password = passwordBox.getText().toString();
		
		SessionManager.SESSION = new SessionManager(email, password, false);
		RESTfulCommunicator.checkLoginCredentials(email, password,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject object) {
						SessionManager.SESSION.setValidated(true);
						intent.setClass(MainActivity.this, SetUpActivity.class);
						startActivity(intent);
						finish();
					}

					@Override
					public void onFailure() {
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast toast = Toast.makeText(MainActivity.this,
										"Invalid Username or Password",
										Toast.LENGTH_LONG);
								toast.show();
							}
						});
					}
				});
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