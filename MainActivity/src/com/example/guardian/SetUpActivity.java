package com.example.guardian;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.provider.ContactsContract;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TimePicker;

public class SetUpActivity extends Activity implements OnItemClickListener,
		OnItemSelectedListener {

	// Initialize variables

	AutoCompleteTextView textView = null;
	private ArrayAdapter<String> adapter;

	// Store contacts values in these arraylist
	public static ArrayList<String> phoneValueArr = new ArrayList<String>();
	public static ArrayList<String> nameValueArr = new ArrayList<String>();
	public static ArrayList<String> emailValueArr = new ArrayList<String>();

	// EditText toNumber=null;
	String toNumberValue = "";
	String toEmailValue = "";
	
	Toast loading_contacts_toast;

	public static ArrayList<Guardian> guardians;

	// Linear Layout for adding Guardians
	LinearLayout layoutGuardians;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_up);
		// final Button send = (Button) findViewById(R.id.send);

		// Initialize AutoCompleteTextView values
		textView = (AutoCompleteTextView) findViewById(R.id.setup_contacts_textview);

		// Set it to be unclickable
		textView.setEnabled(false);

		// Create adapter
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				new ArrayList<String>());
		textView.setThreshold(1);

		// Set adapter to AutoCompleteTextView
		textView.setAdapter(adapter);
		textView.setOnItemSelectedListener(this);
		textView.setOnItemClickListener(this);

		// Initialize guardians array
		guardians = new ArrayList<Guardian>();

		// Linear Layout for Guardians
		layoutGuardians = (LinearLayout) findViewById(R.id.guardianLayout);
		

		Context context = getApplicationContext();
		CharSequence text = "Loading Contacts";
		int duration = Toast.LENGTH_LONG;
		loading_contacts_toast = Toast.makeText(context, text, duration);
		loading_contacts_toast.show();
		
		// Read contact data and add data to ArrayAdapter
		// ArrayAdapter used by AutoCompleteTextView
		new ReadContactsTask().execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_up, menu);
		return true;
	}

	/**
	 * Starts the background tracking tasks and sends the user to the view map
	 * activity
	 * 
	 * @param view
	 */
	public void startTracking(View view) {
		Intent intent = new Intent(this, ViewMapActivity.class);

		CalendarView calendar = (CalendarView) findViewById(R.id.set_up_calendar);
		TimePicker timer = (TimePicker) findViewById(R.id.setup_time_picker);

		// Getting variables form the intent via user input
		long endDate = calendar.getDate();
		int hour = timer.getCurrentHour();
		int minutes = timer.getCurrentMinute();

		// Start date
		Date start = new Date();
		Long startDate = start.getTime();

		endDate += (hour * 60 * 60 * 1000) + (minutes * 60 * 1000);

		SessionManager.SESSION.setGuardians(guardians);
		RESTfulCommunicator.createSession(startDate, endDate,
				SessionManager.SESSION.getGuardians());
		startActivity(intent);
	}

	public void endTracking(View view) {

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	// Read phone contact name and phone numbers

	private class ReadContactsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {

			readContactData();
			return null;
		}

	} // End ReadContactsTask

	private void readContactData() {

		try {

			// Reading the name and number for a contact
			String phoneNumber = "";
			String email = "";
			ContentResolver cr = getBaseContext().getContentResolver();

			// Query to get contact name
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
					null, null, null);

			// If data data found in contacts
			if (cur.getCount() > 0) {

				Log.i("AutocompleteContacts", "Reading contacts........");
				int k = 0;
				String name = "";

				while (cur.moveToNext()) {

					String id = cur.getString(cur
							.getColumnIndex(ContactsContract.Contacts._ID));
					name = cur
							.getString(cur
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					// Check if contact has a phone number
					if (Integer
							.parseInt(cur.getString(cur
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						// Create query to get phone number by contact id
						Cursor pCur = cr
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = ?", new String[] { id },
										null);

						int j = 0;
						while (pCur.moveToNext()) {
							// Sometimes get multiple data
							if (j == 0) {
								// Get Phone number
								phoneNumber = ""
										+ pCur.getString(pCur
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								email = "";
								Cursor eCur = cr
										.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
												null,
												ContactsContract.CommonDataKinds.Email.CONTACT_ID
														+ " = ?",
												new String[] { id }, null);
								while (eCur.moveToNext()) {
									email = eCur
											.getString(eCur
													.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
								}
								eCur.close();
								// Add contacts names to adapter
								adapter.add(name);

								// Add ArrayList names to adapter
								// Adds "" in case email does not exist
								emailValueArr.add(email.toString());
								phoneValueArr.add(phoneNumber.toString());
								nameValueArr.add(name.toString());
								// Log.d("Details: ", name.toString() + " : " +
								// phoneNumber.toString() + " : " +
								// email.toString());

								j++;
								k++;
							}
						} // End while loop
						pCur.close();

					} // End if
				} // End while loop

			} // End Cursor value check
			cur.close();
			loading_contacts_toast.cancel();
			AutoCompleteTextView contactsText = (AutoCompleteTextView) findViewById(R.id.setup_contacts_textview);
			contactsText.setEnabled(true);

		} catch (Exception e) {
			Log.i("AutocompleteContacts", "Exception : " + e);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		// Log.d("AutocompleteContacts", "onItemSelected() position " +
		// position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		// Get Array index value for selected name
		int i = nameValueArr.indexOf("" + arg0.getItemAtPosition(arg2));

		// If name exist in name ArrayList
		if (i >= 0) {
			// Get Phone Number
			toNumberValue = phoneValueArr.get(i);
			toEmailValue = emailValueArr.get(i);
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

			// Show Alert
			TextView text = new TextView(this);
			text.setText(arg0.getItemAtPosition(arg2).toString());
			layoutGuardians.addView(text);

			if (!toEmailValue.equals(""))
				guardians.add(new Guardian(arg0.getItemAtPosition(arg2)
						.toString(), toEmailValue, toNumberValue));
			else
				guardians.add(new Guardian(arg0.getItemAtPosition(arg2)
						.toString(), toNumberValue));

		}

	}

}
