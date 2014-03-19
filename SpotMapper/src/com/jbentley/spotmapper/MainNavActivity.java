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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.internal.db;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jbentley.spotmapper.LocationDialogFragment.LocationDialogFragmentListener;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainNavActivity extends FragmentActivity implements  android.location.LocationListener, LocationDialogFragmentListener{
	private static final String Tag = "[X][X] MainNavActivity ";
	MenuItem settingsIcon;
	MenuItem addNavIcon;

	MapView map;
	private GoogleMap mMap;
	LocationManager locationManager;
	LatLng myLoc;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_nav);

		//action bar icon home button
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);


		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		//start location listen
		startLocationListen();



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
			Intent intent2 = new Intent(this, SettingsActivity.class);
			startActivity(intent2);
		} else if (itemId == R.id.addNavIcon) {
			Log.i(Tag, "addNav");
			saveLocation();
		}

		return super.onOptionsItemSelected(item);

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

		if (isTaggedForGeo){
			geoDisplay = "Tagged for Geofence";
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

			
			LocationDataBaseHelper locDb2 = new LocationDataBaseHelper(this);

			List<LocationInfo> contacts = locDb2.getAllLocs();       

			for (LocationInfo loc : contacts) {
				String log = "Id: "+loc.getId()+" ,Name: " + loc.getlocName();

				Log.d("Log: ", log);
			}

		}



	}

	//on pause remove location updates
	@Override
	protected void onPause() {

		Log.i(Tag, "onPause");
		locationManager.removeUpdates(this);

		super.onPause();
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

		startLocationListen();

	}

	//start location listener
	private void startLocationListen(){
		Log.d(Tag, "startLocationListen");

		boolean GPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean NETWORKendabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (GPSenabled) {

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,  5000 /*5 seconds*/  , 0, this);

			//get the map fragment 
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null) {

				//enable my location
				mMap.setMyLocationEnabled(true);

				//google map options
				GoogleMapOptions mapOptions = new GoogleMapOptions();
				mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
				.compassEnabled(true);
			}
		} else {

			displayLocationSettingsAlert();
		}
		if (NETWORKendabled) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000/*10 seconds*/, 0, this);
		}

	}

	private String getCurrentTime() {
		// TODO Auto-generated method stub
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE  MMM dd, yyyy ' at' h:mm:a", Locale.getDefault());
		String formattedDate = dateFormatter.format(new Date());
		return formattedDate;
	}


}
