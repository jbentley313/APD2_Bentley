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
 * MainNavActivity is the main class for the application "Spot Mapper."  This activity shows a GoogleMap with the current 
 * device location.  A listview will be implemented for Milestone 2 to display saved locations.
 */
package com.jbentley.spotmapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jbentley.spotmapper.LocationDialogFragment.LocationDialogFragmentListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainNavActivity extends FragmentActivity implements  android.location.LocationListener, LocationDialogFragmentListener{
	private static final String Tag = "[X][X] MainNavActivity ";
	MenuItem settingsIcon;
	MenuItem addNavIcon;

	MapView map;
	GoogleMap mMap;
	LocationManager locationManager;
	LatLng myLoc;
	List<Address> addresses;
	TextView addressTextResults;
	static final int GOOGLE_SERVICES_RESULT = 9000;
	private SingleGeoFence singleGeoFence;
	private SingleGeoFenceStore singleGeoStore;
	final String LAT_KEY = "latitudeKey";
	 final String LNG_KEY = "longitudeKey";
	 final String RADIUS_KEY = "radiusKey";
	 final String TRANSITION_KEY = "transitionKey";
	 final String EXPIRATION_KEY = "expirationKey";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_nav);

		//action bar icon home button
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);


		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		addressTextResults = (TextView) findViewById(R.id.resultTextAddress);
		
		

		//start location listen
		startLocationListen();
		
//		SharedPreferences mySharedPrefs2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//		
		
		SharedPreferences mySharedPrefs2 = this.getSharedPreferences("mySharedGeoPrefs", Context.MODE_PRIVATE);
		
		Map<String,?> keys = mySharedPrefs2.getAll();

		for(Map.Entry<String,?> entry : keys.entrySet()){
		            Log.i("map values",entry.getKey() + ": " + 
		                                   entry.getValue().toString());            
		 }
		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_action_bar, menu);

		settingsIcon = menu.findItem(R.id.action_settings);
		addNavIcon = menu.findItem(R.id.addNavIcon).setVisible(false);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		int itemId = item.getItemId();
		if (itemId == R.id.settingsIcon) {
			Intent settingsIntent = new Intent (this, PreferenceDisplayActivity.class);
			startActivity(settingsIntent);
		} else if (itemId == R.id.addNavIcon) {
			Log.i(Tag, "addNav");
			saveLocation();
		} else if (itemId == R.id.emergencyIcon){

			new AlertDialog.Builder(this)
			.setTitle("Warning! Emergency Contacts Will Be Notified!")
			.setMessage("Are you sure you want to send an emergency alert with your location?")
			.setPositiveButton("Yes,  send now!", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					sendMessagetoContacts();
				}
			})
			.setNegativeButton("Cancel", null)
			.setCancelable(true)
			.show();

		}

		return super.onOptionsItemSelected(item);

	}

	private void sendMessagetoContacts() {

		ArrayList<String> contactsList = new ArrayList<String>();
		SharedPreferences mySharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);


		//geocoder to get address based on location
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {

			this.addresses = geocoder.getFromLocation(myLoc.latitude, myLoc.longitude, 1);

			Address address = addresses.get(0);

			Log.i("address", addresses.toString());

			if (address.toString() == "") {
				addressTextResults.setText("Address Unavailable");
			} else {
				int i = 0;
				while (address.getAddressLine(i) != null) {
					addressTextResults.append("\n");
					addressTextResults.append(address.getAddressLine(i++));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(Tag, e.getMessage().toString());
		}


		Map<String,?> prefString = (Map<String, ?>) mySharedPrefs.getAll();

		for(Map.Entry<String,?> entry : prefString.entrySet()){

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
				String numberOfContact = formattedNumberHolder.replace("contact:", "");
				String numberOfContactFormatted = PhoneNumberUtils.formatNumber(numberOfContact);

				String nameOfContact = name;

				Log.i("contact list", nameOfContact + ": " + numberOfContactFormatted);
				contactsList.add(nameOfContact + ": " + numberOfContactFormatted);

				String linkToMySavedLoc = "http://maps.google.com/maps?q=loc:" + myLoc.latitude + "," + myLoc.longitude;

				String addressFormatted = addressTextResults.getText().toString();
				Log.i("addFORM", addressFormatted);

				String outGoingMessage = "TEST!!!!.  My location: " +
						linkToMySavedLoc + ". " + "Nearest address: " + addressFormatted  + "." ;

				Log.i("SMS", numberOfContactFormatted);

				android.telephony.SmsManager smsMgr =  android.telephony.SmsManager.getDefault();
				smsMgr.sendTextMessage(numberOfContactFormatted, null, outGoingMessage, null, null);
			}
		}
	}

	//save location
	private void saveLocation() {
		// TODO Auto-generated method stub
		Log.i(Tag, myLoc.toString());

		//display dialog frament
		showLocNameDialog();

	}

	//show the LocationDialogFragment
	private void showLocNameDialog() {
		FragmentManager fragMgr = getSupportFragmentManager();
		LocationDialogFragment locFrag = new LocationDialogFragment();
		locFrag.show(fragMgr, "location_name_frag");
	}

	//get values from DialogFragment (Loc name and tagged for geo boolean) then save to db
	public void onFinishNavDialogFrag(String locNameText, Boolean isTaggedForGeo) {
		Log.i(Tag,  (locNameText + ", " + isTaggedForGeo.toString() + ", " + myLoc));

		LocationDataBaseHelper locDb = new LocationDataBaseHelper(this);

		//Dispaly a snippet on a pin if loc is tagged for geo
		String geoDisplay;

		//tagged for geofence
		if (isTaggedForGeo){
			geoDisplay = "Tagged for Geofence";

			singleGeoFence = new SingleGeoFence(locNameText, 10.0f, myLoc.latitude, myLoc.longitude, Geofence.GEOFENCE_TRANSITION_ENTER, Geofence.NEVER_EXPIRE);
			singleGeoStore = new SingleGeoFenceStore(this);
			
			singleGeoStore.setGeoFence(locNameText, singleGeoFence);
			
			
			//geofence create

		} else {
			geoDisplay = "Not tagged for Geofence";
		}

		//add map marker
		Marker savedLocMarker = mMap.addMarker(new MarkerOptions()
		.position(myLoc)
		.title(locNameText)
		.snippet(geoDisplay));

		savedLocMarker.showInfoWindow();

		//Split the lat and long into two strings that are saved on the db
		Double latDouble = myLoc.latitude;
		Double longDouble = myLoc.longitude;
		String latString = latDouble.toString();
		String longString = longDouble.toString();
		String savedDateAndTime = getCurrentTime();


		if (!locNameText.isEmpty()) {
			Log.i("NO", "not empty");
			//add location to the database
			locDb.addLocationtoDB(new LocationInfo(locNameText, latString, longString, isTaggedForGeo, savedDateAndTime));


			//get listNavFrag
			ListNavFragment lnf = (ListNavFragment) getFragmentManager().findFragmentById(R.id.listNavFrag);

			//get listview from listnavfrag
			ListView lv = lnf.getListView();

			//get arrayadapter from the listview in listnavfrag
			ArrayAdapter<?> myArrAdptr = (ArrayAdapter<?>) lv.getAdapter();

			//notify the adapter data has changed
			myArrAdptr.notifyDataSetChanged();

			//call init on listnavfrag
			lnf.init(lv);
		}
	}

	//on pause remove location updates
	@Override
	protected void onPause() {
		super.onPause();

		Log.i(Tag, "onPause");
		locationManager.removeUpdates(this);
	}

	//onLocationChanged
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		myLoc = (new LatLng(location.getLatitude(), location.getLongitude()));

		//animate map to zoom to current location
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 19.0f));

		//enable add nav icon after a location is obtained
		addNavIcon.setVisible(true);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	//display alert if location is turned off
	private void displayLocationSettingsAlert() {
		new AlertDialog.Builder(this)

		.setTitle("Location is turned off!")
		.setMessage("Would you like to go to location settings to turn location on?")
		.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				//send user to settings/location to be able to turn on
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);

				//dismiss the dialog
				dialog.dismiss();

			}
		})
		.setNegativeButton("No", null)
		.setCancelable(true)
		.show();
	}

	@Override
	public void onResume() {
		Log.d(Tag, "onResume");
		// TODO Auto-generated method stub
		super.onResume();

		//check to see if google play service available
		googleServiceAvailable();

		startLocationListen();

		mMap.clear();

		//get listNavFrag
		ListNavFragment lnf = (ListNavFragment) getFragmentManager().findFragmentById(R.id.listNavFrag);

		//get listview from listnavfrag
		ListView lv = lnf.getListView();

		//get arrayadapter from the listview in listnavfrag
		ArrayAdapter<?> myArrAdptr = (ArrayAdapter<?>) lv.getAdapter();

		//notify the adapter data has changed
		myArrAdptr.notifyDataSetChanged();

		//call init on listnavfrag
		lnf.init(lv);
	}

	//start location listener
	private void startLocationListen(){
		Log.d(Tag, "startLocationListen");

		boolean GPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean NETWORKendabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		SharedPreferences mySharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		String mapType = mySharedPrefs.getString("mapDisplayPref", "map");
		Log.i("MapType", mapType);

		if (GPSenabled) {

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,  1000 /*1 second*/  , 0, this);

			//get the map fragment 
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				//enable my location
				mMap.setMyLocationEnabled(true);

				if(mapType.equalsIgnoreCase("1")){
					Log.i("MAPTYPE", "Normal map");
					mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

				} else if(mapType.equalsIgnoreCase("2")){
					Log.i("MAPTYPE", "Satellite");
					mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				} else if(mapType.equalsIgnoreCase("3")){
					Log.i("MAPTYPE", "Hybrid");
					mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				}

			}
		} else {

			displayLocationSettingsAlert();
		}
		if (NETWORKendabled) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000/*10 seconds*/, 0, this);
		}
	}

	//get current time
	private String getCurrentTime() {
		// TODO Auto-generated method stub
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE  MMM dd, yyyy ' at' h:mm:a", Locale.getDefault());
		String formattedDate = dateFormatter.format(new Date());
		return formattedDate;
	}

	//check if google services is available
	private boolean googleServiceAvailable(){

		int googleResultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		//google play service is installed
		if(ConnectionResult.SUCCESS == googleResultCode) {
			Log.i(Tag, "GooglePlayServices SUCCESS");
			return true;
		} else {
			ErrorFragDialog errorFrag = new ErrorFragDialog();
			errorFrag.setDialog(GooglePlayServicesUtil.getErrorDialog(googleResultCode, this, GOOGLE_SERVICES_RESULT));
			errorFrag.show(getFragmentManager(), "GooglePlayServicesError");

			return false;
		}

	}

	//dialog fragment for googledialog error
	public static class ErrorFragDialog extends DialogFragment {

		private Dialog googleErrorDialog;

		public ErrorFragDialog(){
			super();
			googleErrorDialog = null;
		}

		public void setDialog(Dialog dialog) {
			googleErrorDialog = dialog;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return googleErrorDialog;
		}
	}



	public void setGeofence(String id, SingleGeoFence geofence){
		
		 
		
	}

	
	
	

	
	
	
	

	@Override
	protected void onActivityResult(
			int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {

		case GOOGLE_SERVICES_RESULT :

			switch (resultCode) {

			case Activity.RESULT_OK :

				break;
			}
		}
	}
}
