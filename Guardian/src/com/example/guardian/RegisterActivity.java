package com.example.guardian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	}

	/**
	 * Creates a new user for use with the app. Thanks.
	 * 
	 * @param view
	 */
	public void registerUser(View view) {

		EditText choosePasswordBox = (EditText) findViewById(R.id.register_password_box);
		EditText reenterPasswordBox = (EditText) findViewById(R.id.register_reenter_box);

		String password = choosePasswordBox.getText().toString();
		String repassword = reenterPasswordBox.getText().toString();

		if (!password.equals(repassword)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Passwords do not match.")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Nothing, just die. Haha
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			Intent intent = new Intent(this, SetUpActivity.class);

			EditText usernameBox = (EditText) findViewById(R.id.register_username_box);
			EditText nameBox = (EditText) findViewById(R.id.register_name_textbox);
			EditText emailBox = (EditText) findViewById(R.id.register_email_textbox);
			EditText homeAddressBox = (EditText) findViewById(R.id.register_address_textbox);

			String username = usernameBox.getText().toString();
			String name = nameBox.getText().toString();
			String email = emailBox.getText().toString();
			String address = homeAddressBox.getText().toString();

			SessionManager.SESSION = new SessionManager(username, email, true);
			SessionManager.SESSION.addMoreInfo(email, address, name);

			Guardian[] guardians = new Guardian[3];
			guardians[0] = new Guardian("Maria del Carmen",
					"maricarmen@falsify.com", "770-555-5555");
			guardians[1] = new Guardian("Joe Divers", "jdriver334@falsify.com",
					"770-555-5556");
			guardians[2] = new Guardian("Yael Naor",
					"maricarmen@falsify.com", "770-555-5557");

			SessionManager.SESSION.setGuardians(guardians);

			startActivity(intent);
		}

	}

}
