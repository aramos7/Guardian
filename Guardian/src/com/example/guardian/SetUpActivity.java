package com.example.guardian;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.provider.ContactsContract;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;


public class SetUpActivity extends Activity implements OnItemClickListener, OnItemSelectedListener {

    //private static final int CONTACT_PICKER_RESULT = 1001;

//    //List of Guardian Names
//    private List<String> nameList = new ArrayList<String>();

    // Initialize variables

    AutoCompleteTextView textView=null;
    private ArrayAdapter<String> adapter;

    // Store contacts values in these arraylist
    public static ArrayList<String> phoneValueArr = new ArrayList<String>();
    public static ArrayList<String> nameValueArr = new ArrayList<String>();
    public static ArrayList<String> emailValueArr = new ArrayList<String>();

    EditText toNumber=null;
    String toNumberValue="";
    String toEmailValue="";

    //Linear Layout for adding Guardians
    LinearLayout layoutGuardians;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        //final Button send = (Button) findViewById(R.id.send);

        // Initialize AutoCompleteTextView values
        textView = (AutoCompleteTextView) findViewById(R.id.toNumber);

        //Create adapter
        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        textView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        textView.setAdapter(adapter);
        textView.setOnItemSelectedListener(this);
        textView.setOnItemClickListener(this);

        //Linear Layout for Guardians
        layoutGuardians = (LinearLayout) findViewById(R.id.guardianLayout);

        // Read contact data and add data to ArrayAdapter
        // ArrayAdapter used by AutoCompleteTextView
        readContactData();

        /********** Button Click pass textView object ***********/
        //send.setOnClickListener(buttonListener(textView));

    }

//    private OnClickListener buttonListener(final AutoCompleteTextView toNumber) {
//        return new OnClickListener() {
//            public void onClick(View v) {
//
//                String nameSel = toNumber.getText().toString();
//                final String ToNumber = toNumberValue;
//
//
//                if (ToNumber.length() == 0 ) {
//                    Toast.makeText(getBaseContext(), "Please fill phone number",
//                            Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    Toast.makeText(getBaseContext(), nameSel+" : "+toNumberValue,
//                            Toast.LENGTH_LONG).show();
//                }
//
//            }
//        };
//    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_up, menu);
		return true;
	}

//    public void pickGuardians(View view) {
//        Intent contactPickerIntent = new
//                Intent(Intent.ACTION_PICK,
//                Contacts.CONTENT_URI);
//                startActivityForResult(contactPickerIntent,
//                CONTACT_PICKER_RESULT);
//    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case CONTACT_PICKER_RESULT:
//                    Cursor cursor = null;
//                    String email = "";
//                    try {
//                        Uri result = data.getData();
//                        Log.v("ContactPicker", "Got a contact result:" + result.toString());
//
//                        // get the contact id from the Uri
//                        String id = result.getLastPathSegment();
//                        String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
//                        String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, id};
//                        cursor = getContentResolver().query
//                                (ContactsContract.Data.CONTENT_URI,
//                                 null,
//                                 whereName,
//                                 whereNameParams,
//                                 ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
//                        //int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
//                        // let's just get the first name
//                        if (cursor.moveToNext()) {
//                            //email = cursor.getString(emailIdx);
//                            String display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
//                            //addItems(display_name);
//                            Log.v("ContactPicker", "Got name: " + display_name);
//                            TextView text = (TextView) findViewById(R.id.textView1);
//                            text.setText(display_name);
//                        }
//                        else {
//                            Log.w("ContactPicker", "No results");
//                        }
//                    } catch (Exception e) {
//                        Log.e("ContactPicker", "Failed to get email data", e);
//                    }
////                    finally {
////                        if (cursor != null) {
////                            cursor.close();
////                        }
////                        EditText emailEntry = (EditText) findViewById(R.id.invite_email);
////                        emailEntry.setText(email);
////                        if (email.length() == 0) {
////                            Toast.makeText(this, "No email found for contact.", Toast.LENGTH_LONG).show();
////                        }
////                    }
//                    break;
//            }
//        } else {
//            Log.w("ContactPicker", "Warning: activity result not ok");
//        }
//    }

	/**
	 * Starts the background tracking tasks and sends the user to the view map
	 * activity
	 * 
	 * @param view
	 */
	public void startTracking(View view) {
		Intent intent = new Intent(this, ViewMapActivity.class);
        RESTfulCommunicator.createSession();
		startActivity(intent);
	}

	public void endTracking(View view) {

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

    // Read phone contact name and phone numbers

    private void readContactData() {

        try {

            //Reading the name and number for a contact

            String phoneNumber = "";
            String email = "";
            ContentResolver cr = getBaseContext().getContentResolver();

            //Query to get contact name
            Cursor cur = cr.query(
                            ContactsContract.Contacts.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

            // If data data found in contacts
            if (cur.getCount() > 0) {

                Log.i("AutocompleteContacts", "Reading   contacts........");
                int k = 0;
                String name = "";

                while (cur.moveToNext())
                {

                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    //Check if contact has a phone number
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {
                        //Create query to get phone number by contact id
                        Cursor pCur = cr.query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ?",
                                        new String[] { id },
                                        null);
//                        Cursor eCur = cr.query(
//                                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                                        null,
//                                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                                        new String[]{id},
//                                        null);

                        int j=0;
                        while (pCur.moveToNext())
                        {
                            // Sometimes get multiple data
                            if(j==0)
                            {
                                // Get Phone number
                                phoneNumber = "" + pCur.getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                                email = eCur.getString(eCur
//                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                                // Add contacts names to adapter
                                adapter.add(name);

                                // Add ArrayList names to adapter
                                emailValueArr.add(email.toString());
                                phoneValueArr.add(phoneNumber.toString());
                                nameValueArr.add(name.toString());

                                j++;
                                k++;
                            }
                        }  // End while loop
                        pCur.close();


                    } // End if
                }  // End while loop

            } // End Cursor value check
            cur.close();


        } catch (Exception e) {
            Log.i("AutocompleteContacts","Exception : "+ e);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub
        //Log.d("AutocompleteContacts", "onItemSelected() position " + position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

        InputMethodManager imm = (InputMethodManager) getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

        // Get Array index value for selected name
        int i = nameValueArr.indexOf(""+arg0.getItemAtPosition(arg2));

        // If name exist in name ArrayList
        if (i >= 0) {
            // Get Phone Number
            toNumberValue = phoneValueArr.get(i);
            toEmailValue = emailValueArr.get(i);
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            // Show Alert
            TextView text = new TextView(this);
            text.setText(arg0.getItemAtPosition(arg2).toString());
            layoutGuardians.addView(text);
            Toast.makeText(getBaseContext(),
                    "Position:" + arg2 + " Name:" + arg0.getItemAtPosition(arg2) + " Number:" + toNumberValue + " Email: "
                    + toEmailValue,
                    Toast.LENGTH_LONG).show();

            Log.d("Size of Array: ", Integer.toString(phoneValueArr.size()));

        }

    }

//    public void pickGuardians(View view) {
//
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//        builder1.setTitle("Pick a Guardian to track you.")
//                .setMultiChoiceItems(R.array.guardians, null,
//                        new DialogInterface.OnMultiChoiceClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which, boolean isChecked) {
//                                // TODO Auto-generated method stub
//
//                            }
//                        })
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // Nothing, just die. Haha
//                    }
//                });
//        AlertDialog alert = builder1.create();
//        alert.show();
//    }

}
