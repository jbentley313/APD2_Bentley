package com.jbentley.spotmapper;

import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Preferences extends PreferenceFragment implements OnPreferenceClickListener {



	private static final int CONTACT_PICKER_RESULT = 303;
	String contactNumberString = "";
	String contactNameString ="";
	
	String numberOfContact;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		//set a clicklistener for the contact preference
		Preference contactPick = (Preference)findPreference("contact_pref");
		contactPick.setOnPreferenceClickListener(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = super.onCreateView(inflater, container, savedInstanceState);
		//change bg color of pref frag
		view.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
		return view;


	}

	//preference click handler
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		Log.i("Pref", "contactprefpicker");

		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);

		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		//use a cursor to get contact data
		Cursor cursor = null;

		//get shared prefs
		SharedPreferences mySharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor myEditor = mySharedPrefs.edit();








		if (requestCode == CONTACT_PICKER_RESULT) {
			try {
				Uri result = data.getData();

				// get the contact id from the result uri
				String id = result.getLastPathSegment();

				// query  name number
				cursor = getActivity().getContentResolver().query(Phone.CONTENT_URI,
						null, Phone.CONTACT_ID + "=?", new String[] { id },
						null);

				//name and number int
				int contactNumber = cursor.getColumnIndex(Phone.DATA);
				int contactName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);


				// get name and number from cursor
				if (cursor.moveToFirst()) {
					contactNumberString = cursor.getString(contactNumber);
					contactNameString = cursor.getString(contactName);
					Log.i("Contact: ",  contactNameString + ", " + contactNumberString );
					myEditor.putString("contact:" + contactNumberString, contactNameString);
					myEditor.commit();
					logAllContacts();

				} else {
					Log.i("Prefs", "Cursor empty, no results");
				}
			} catch (Exception e) {
				Log.i("Prefs ExceptionE", "Error getting contact data"+ e.getMessage().toString());
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		}

	}

	private void logAllContacts() {
		// TODO Auto-generated method stub
		SharedPreferences mySharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Map<String,?> prefString = (Map<String, ?>) mySharedPrefs.getAll();

		for(Map.Entry<String,?> entry : prefString.entrySet()){
			//			Log.d("contacts in sharedprefs= ",entry.getKey() + ": " + 
			//					entry.getValue().toString());   
			String name =  entry.getValue().toString();
			String number = entry.getKey().toString();


			//format the number and replace +1, '(', and ')' with ""
			if(number.contains("contact")){
				String formattedNumber1 = PhoneNumberUtils.formatNumber(number
						.replace("+1", "")
						.replace("(", "")
						.replace(")", "")
						);

				//replace dashes with empty space
				String formattedNumberHolder = PhoneNumberUtils.formatNumber(formattedNumber1
						.replaceAll("-", "")
						.replaceAll(" ", ""));
				
				//format the final number using PhoneNumberUtils
				numberOfContact = formattedNumberHolder.replace("contact:", "");
				String numberOfContactFormatted = PhoneNumberUtils.formatNumber(numberOfContact);
				
				
				String nameOfContact = name;

				Log.i("contact list", nameOfContact + ": " + numberOfContactFormatted);
			}
		}
	}
}
