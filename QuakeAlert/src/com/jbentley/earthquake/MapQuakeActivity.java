/*
 * 
 */
package com.jbentley.earthquake;

import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MapQuakeActivity extends Activity {
	JSONObject passedQuakeObj;
	GoogleMap mMap;
	SharedPreferences mySharedPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_quake);
		// TextView locationTextView = (TextView) findViewById(R.id.locDisplay);
		
		mySharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		

		//get quakeObject extras from main activity
		Bundle data = getIntent().getExtras();
		if (data != null) {
			
			Object quakeOBJ = data.getSerializable("quakeObject");

			//parsing the object to get latitude and longitude to display on a map
			//will find more efficient way for week4 turn in
			String[] fullQuakeObjectString = quakeOBJ.toString().split("lat");
			String firstHalf = fullQuakeObjectString[0];
			String secondHalf = fullQuakeObjectString[1];
			Log.i("FIRSTH", firstHalf);
			Log.i("SECONDH", secondHalf);

			String[] stripCommaSign = secondHalf.split(",");
			String firstLat = stripCommaSign[0];
			String secondLong = stripCommaSign[1];

			Log.i("FIRSTL", firstLat);
			Log.i("SECONDL", secondLong);

			String lastLat = firstLat.replace("=", "");
			Log.i("sd", lastLat);

			String longitudeTemp = secondLong.replace("longC=", "");
			String lastLong = longitudeTemp.replace("}", "");
			Log.i("ds", lastLong);

			double lat = Double.parseDouble(lastLat);
			double longC = Double.parseDouble(lastLong);

			
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			//
			
			mMap.addMarker(new MarkerOptions().position(new LatLng(lat, longC))
					.title(""));
			
			
			// Instantiates a new CircleOptions object and defines the center and radius
			CircleOptions circleOptions = new CircleOptions()
			    .center(new LatLng(lat, longC))
			    .radius(1000); // In meters

			
			mMap.addCircle(circleOptions);
			

			CameraPosition quakeCam = new CameraPosition.Builder()
					.target(new LatLng(lat, longC)).zoom(10) // Sets the zoom
					.bearing(0) // Sets the orientation of the camera (degrees)
					.tilt(30) // Sets the tilt of the camera to 30 degrees
					.build();
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(quakeCam));

			// Button backButton = (Button) findViewById(R.id.mapBackButton);

		}
	}

	
	
	

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		getMenuInflater().inflate(R.menu.quake_activity_menu, menu);
		
		return true;
	}







	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		
		int itemId = item.getItemId();
		
		if (itemId ==  R.id.action_settings) {
			Intent settingsIntent = new Intent (this, PrefDisplayActivity.class);
			startActivity(settingsIntent);


		}
		return false;
		
		
	}







	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String mapType = mySharedPrefs.getString("mapDisplayPref", "map");
		
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
	
	

}
