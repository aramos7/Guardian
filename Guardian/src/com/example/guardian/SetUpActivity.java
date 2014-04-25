package com.example.guardian;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.text.format.DateFormat;
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

	public static ArrayList<Guardian> guardians;

	// Linear Layout for adding Guardians
	LinearLayout layoutGuardians;

	// Variables for the date and time formats
	private static long date = -1;
	private static int hour = -1;
	private static int minutes = -1;
	private static Button timePickerButton;
	private static Button datePickerButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_up);
		// final Button send = (Button) findViewById(R.id.send);

		// Initialize AutoCompleteTextView values
		textView = (AutoCompleteTextView) findViewById(R.id.setup_contacts_textview);

		// Set it to be disable
		textView.setEnabled(false);

		// Get buttons
		timePickerButton = (Button) findViewById(R.id.setup_pick_time_button);
		datePickerButton = (Button) findViewById(R.id.setup_pick_date_button);

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

		if (hour == -1 || minutes == -1 || date == -1) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("You must pick an end time or end date.")
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
			return;
		}

		long endDate = date;

		// Start date
		Date start = new Date();
		Long startDate = start.getTime();

		endDate += (hour * 60 * 60 * 1000) + (minutes * 60 * 1000);

		if (endDate <= startDate) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Your session must end after today at this time.")
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
			return;
		}

		SessionManager.SESSION.setGuardians(guardians);
		RESTfulCommunicator.createSession(startDate, endDate,
				SessionManager.SESSION.getGuardians());
		startActivity(intent);
		finish();
	}

	public void endSetUpActivity(View view) {

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
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
			textView.setEnabled(true);

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

	/**
	 * Creates and displays a time picker dialog, which then saves the user
	 * defined information in the activity and redefines the text on the button
	 * to the new date.
	 */
	public void showTimePickerDialog(View view) {

		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	/**
	 * Creates and displays a date picker dialog, the result of which changes
	 * the button text to match the date chosen by the user.
	 */
	public void showDatePickerDialog(View view) {

		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	/**
	 * This class is setup to create a time picker dialog for this activity, to
	 * use in creating the session for the user.
	 */
	public static class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Use the current time as the default values for the picker
			int hour, minute;

			if (SetUpActivity.hour == -1 || SetUpActivity.minutes == -1) {
				final Calendar c = Calendar.getInstance();
				hour = c.get(Calendar.HOUR_OF_DAY);
				minute = c.get(Calendar.MINUTE);
			} else {
				hour = SetUpActivity.hour;
				minute = SetUpActivity.minutes;
			}

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			SetUpActivity.hour = hourOfDay;
			SetUpActivity.minutes = minute;

			String timeformat = "";
			String hourformat;
			String minuteformat;
			boolean am = true;

			// Properly formats the hour
			if (hourOfDay == 0) {
				hourformat = "12";
			} else if (hourOfDay < 10) {
				hourformat = String.format("0%d", hourOfDay);
			} else if (hourOfDay < 12) {
				hourformat = String.format("%d", hourOfDay);
			} else {
				int newhour = hourOfDay - 12;
				hourformat = String.format("%d", newhour);
				am = false;
			}

			// Properly formats the minutes
			if (minute < 10) {
				minuteformat = String.format("0%d", minute);
			} else {
				minuteformat = String.format("%d", minute);
			}

			timeformat += hourformat + ":" + minuteformat;

			// Determines whether the hour is AM or PM
			if (am) {
				timeformat += " AM";
			} else {
				timeformat += " PM";
			}

			timePickerButton.setText(timeformat);
		}
	}

	/**
	 * This class managed the date picker dialog for this activity, to get the
	 * end date' information when the user wants to end their session.
	 */
	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Use the current date as the default date in the picker
			int year, month, day;
			final Calendar c = Calendar.getInstance();

			if (SetUpActivity.date == -1) {

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
			} else {

				c.setTime(new Date(date));
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
			}

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {

			Date choice = new Date(view.getCalendarView().getDate());
			choice.setMinutes(0);
			choice.setSeconds(0);
			choice.setHours(0);
			
			SetUpActivity.date = choice.getTime();
			
			String calendarDate = java.text.DateFormat.getDateInstance(
					java.text.DateFormat.LONG).format(SetUpActivity.date);

			String know = java.text.DateFormat.getDateTimeInstance().format(SetUpActivity.date);
			Toast.makeText(getActivity(), know, Toast.LENGTH_LONG).show();
			datePickerButton.setText(calendarDate);
		}
	}

}