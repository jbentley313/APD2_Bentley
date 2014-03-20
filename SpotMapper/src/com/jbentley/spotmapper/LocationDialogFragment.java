/*
 *Author: Jason Bentley
 * 
 * Mar 13, 2014
 * 
 * Project = SpotMapper
 * 
 * Package = com.jbentley.spotmapper
 * 
 * 
 * LocationDialogFragment is a DialogFragment that allows the user to name a location, as well as flag it for geofencing
 */
package com.jbentley.spotmapper;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LocationDialogFragment extends DialogFragment implements OnEditorActionListener, android.view.View.OnClickListener{

	private EditText nameLocation;
	private CheckBox geoCheck;
	private Button dialogSaveBtn;

	public interface LocationDialogFragmentListener {
		void onFinishNavDialogFrag(String inputText, Boolean isTaggedForGeo);
	}

	public LocationDialogFragment() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.location_name_frag, container);

		//location name edit text
		nameLocation = (EditText) view.findViewById(R.id.locName);

		dialogSaveBtn = (Button) view.findViewById(R.id.dialogSaveBtn);
		dialogSaveBtn.setOnClickListener(this);

		//Flag for Geofence checkbox
		geoCheck = (CheckBox) view.findViewById(R.id.flagForGeo);
		getDialog().setTitle("Save current location");

		// Show soft keyboard when save nav pressed
		nameLocation.requestFocus();
		getDialog().getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		nameLocation.setOnEditorActionListener(this);


		return view;


	}

	//return text and boolean (Location name and if Geofence checkbox is checked) back to activity when "done" is pressed
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {

			// Return input text and boolean to activity
			LocationDialogFragmentListener activity = (LocationDialogFragmentListener) getActivity();
			activity.onFinishNavDialogFrag(nameLocation.getText().toString(), geoCheck.isChecked());

			this.dismiss();
			return true;
		}
		return false;
	}

	//save dialog button
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.dialogSaveBtn){
			Log.i("DF", "saved btn pressed!");

			((LocationDialogFragmentListener) getActivity()).onFinishNavDialogFrag(nameLocation.getText().toString(), geoCheck.isChecked());
			
			
			
			this.dismiss();
		}
	}



}
