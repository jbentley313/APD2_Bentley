package com.jbentley.spotmapper;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LocationDialogFragment extends DialogFragment implements OnEditorActionListener{

	private EditText nameLocation;
	private CheckBox geoCheck;
	
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

}
