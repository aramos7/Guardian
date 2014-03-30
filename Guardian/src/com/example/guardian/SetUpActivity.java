package com.example.guardian;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SetUpActivity extends ListActivity {

    private static final int CONTACT_PICKER_RESULT = 1001;

    //List of Guardian Names
    private List<String> nameList = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                nameList);
        setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_up, menu);
		return true;
	}

    public void pickGuardians(View view) {
        Intent contactPickerIntent = new
                Intent(Intent.ACTION_PICK,
                Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent,
                CONTACT_PICKER_RESULT);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    Cursor cursor = null;
                    String email = "";
                    try {
                        Uri result = data.getData();
                        Log.v("ContactPicker", "Got a contact result:" + result.toString());

                        // get the contact id from the Uri
                        String id = result.getLastPathSegment();
                        String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
                        String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, id};
                        cursor = getContentResolver().query
                                (ContactsContract.Data.CONTENT_URI,
                                 null,
                                 whereName,
                                 whereNameParams,
                                 ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
                        //int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                        // let's just get the first name
                        if (cursor.moveToNext()) {
                            //email = cursor.getString(emailIdx);
                            String display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                            //addItems(display_name);
                            Log.v("ContactPicker", "Got name: " + display_name);
                            EditText text = (EditText) findViewById(R.id.textView1);
                            text.setText(display_name);
                        }
                        else {
                            Log.w("ContactPicker", "No results");
                        }
                    } catch (Exception e) {
                        Log.e("ContactPicker", "Failed to get email data", e);
                    }
//                    finally {
//                        if (cursor != null) {
//                            cursor.close();
//                        }
//                        EditText emailEntry = (EditText) findViewById(R.id.invite_email);
//                        emailEntry.setText(email);
//                        if (email.length() == 0) {
//                            Toast.makeText(this, "No email found for contact.", Toast.LENGTH_LONG).show();
//                        }
//                    }
                    break;
            }
        } else {
            Log.w("ContactPicker", "Warning: activity result not ok");
        }
    }

    /**
     * Method which will handle dynamic insertions
     * @param name
     */
    public void addItems(String name) {
        nameList.add(name);
        adapter.notifyDataSetChanged();
    }

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
