package com.jbentley.spotmapper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SavedSpotNavigation extends FragmentActivity implements android.location.LocationListener, SensorEventListener{
	MapView mapS;
	private GoogleMap mMapS;
	private LocationManager locationManagerS;
	private LatLng mySavedLoc;
	private Location myCurrentLocation;
	private LatLng myCurrentLatLng;
	private String nameLoc;
	private String latLoc;
	private String longLoc;
	private int idLoc;
	private Boolean geoFenceLoc;
	private String timeLoc;
	private SensorManager sensorMngr;
	MenuItem share;
	MenuItem delete;
	MenuItem compass;
	private static String Tag = "SavedSpotNavigationActivity";


	Sensor magnomtr;
	private float bearingFlt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved_spot_navigation);

		//action bar icon home button
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);



		locationManagerS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		locationManagerS.requestLocationUpdates(LocationManager.GPS_PROVIDER,  3000 /*5 seconds*/  , 0, this);

		sensorMngr = (SensorManager) getSystemService(SENSOR_SERVICE);

		magnomtr = sensorMngr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		if(sensorMngr !=null){

			sensorMngr.registerListener(this, magnomtr, SensorManager.SENSOR_DELAY_UI);
		}




		//get extras passed from MainNavActivity
		Bundle extras = getIntent().getExtras();
		idLoc = extras.getInt("idSavedLoc");
		nameLoc = extras.getString("nameSavedLoc");
		latLoc = extras.getString("latSavedLoc");
		longLoc = extras.getString("longSavedLoc");
		geoFenceLoc = extras.getBoolean("geoFenceSavedLoc");
		timeLoc = extras.getString("timeSavedLoc");

		//convert lat and long strings to long for use with LatLng

		Double latFromString;
		Double longFromString; 

		latFromString = Double.valueOf(latLoc);
		longFromString = Double.valueOf(longLoc);

		//passed location coordinates
		mySavedLoc = new LatLng(latFromString, longFromString);


		Log.i("EXTRAS", extras.toString());

		//Dispaly a snippet on a pin if loc is tagged for geo
		String geoDisplay;

		if (geoFenceLoc){
			geoDisplay = "Tagged for Geofence";
		} else {
			geoDisplay = "Not tagged for Geofence";
		}

		//set action bar title to saved location name
		setTitle(nameLoc);



		//get the map fragment 
		mMapS = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		if (mMapS != null) {

			//enable my location
			mMapS.setMyLocationEnabled(true);

			//google map options
			GoogleMapOptions mapOptions = new GoogleMapOptions();
			mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL);

			mapOptions.compassEnabled(true);

			UiSettings uiSettings = mMapS.getUiSettings();
			uiSettings.setCompassEnabled(true);


			//add map marker
			Marker savedLocMarker = mMapS.addMarker(new MarkerOptions()
			.position(mySavedLoc)
			.title(nameLoc)
			.snippet(geoDisplay));

			savedLocMarker.showInfoWindow();

			//zoom to current location
			mMapS.moveCamera(CameraUpdateFactory.newLatLngZoom(mySavedLoc, 19.0f));


		}
	}





	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.saved_spot_action_bar, menu);

		share = (MenuItem) findViewById(R.id.shareIcon);
		delete = (MenuItem) findViewById(R.id.deleteIcon);
		compass = (MenuItem) findViewById(R.id.compassIcon);


		return true;
	}





	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		int itemId = item.getItemId();

		//delete icon pressed
		if(itemId == R.id.deleteIcon) {
			Log.i(Tag, "delete icon clicked");


			deleteLocaton();

			//share location
		} else if(itemId == R.id.shareIcon) {
			Log.i(Tag, "share icon clicked");

			String linkToMySavedLoc = "http://maps.google.com/maps?q=loc:" + mySavedLoc.latitude + "," + mySavedLoc.longitude;

			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "See the location I shared with you on Spot Mapper!");
			shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm sharing the location of " + "\"" + nameLoc + "\"" + " via Spot Mapper!" + "\n" +
					linkToMySavedLoc);
			shareIntent.setType("text/plain");
			startActivity(shareIntent);
		} else if(itemId == R.id.compassIcon) {
			Log.i (Tag, "compass icon clicked");
		}

		return true;
	}

	// delete saved location from the database
	private void deleteLocaton() {

		new AlertDialog.Builder(this)

		.setTitle("Delete location " + "\"" + nameLoc + "\" ?")
		.setMessage("This cannot be undone!")
		.setPositiveButton("Delete", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				LocationDataBaseHelper dbhelp = new LocationDataBaseHelper(getApplicationContext());	
				dbhelp.deleteLocationFromDB(idLoc);

				finish();
			}
		})

		.setCancelable(true)
		.setNegativeButton("Cancel", null)
		.show();
	}



	//position camera behind current location pointing towards target location
	public void updateCameraforCompassBearing(float bearing) {

		CameraPosition camPos = new CameraPosition.Builder()
		.target(myCurrentLatLng)
		.bearing(bearing)
		.tilt(60.0f)
		.zoom(19f)
		.build();
		mMapS.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));

	}

	@Override
	public void onLocationChanged(Location location) {

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


	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		//		float[] gravitys = event.values;
		//		Log.i("CH", String.valueOf(gravitys[0]));


	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sensorMngr != null) {
			sensorMngr.unregisterListener(this);
		}
	}
}
