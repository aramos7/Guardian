package com.example.guardian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class SetUpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_up);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_up, menu);
		return true;
	}

	public void pickGuardians(View view) {

		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setTitle("Pick a Guardian to track you.")
				.setMultiChoiceItems(R.array.guardians, null,
	                      new DialogInterface.OnMultiChoiceClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								// TODO Auto-generated method stub
								
							}
						})
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								// Nothing, just die. Haha
							}
						});
		AlertDialog alert = builder1.create();
		alert.show();
	}

	public void startTracking(View view) {

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	public void endTracking(View view) {

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

}
