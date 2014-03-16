package com.jbentley.spotmapper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class SavedSpotNavigation extends Activity{
	MapView mapS;
	private GoogleMap mMapS;
	private LocationManager locationManagerS;
	private LatLng myLocS;
	private String nameLoc;
	private String latLoc;
	private String longLoc;
	private Boolean geoFenceLoc;
	private String timeLoc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved_spot_navigation);
		
		//get extras passed from MainNavActivity
		Bundle extras = getIntent().getExtras();
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
		myLocS = new LatLng(latFromString, longFromString);
		
		Log.i("EXTRAS", extras.toString());
		
		//Dispaly a snippet on a pin if loc is tagged for geo
				String geoDisplay;

				if (geoFenceLoc){
					geoDisplay = "Tagged for Geofence";
				} else {
					geoDisplay = "Not tagged for Geofence";
				}

				
		
		
		
		
		//get the map fragment 
		mMapS = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		if (mMapS != null) {

			//enable my location
			mMapS.setMyLocationEnabled(true);

			//google map options
			GoogleMapOptions mapOptions = new GoogleMapOptions();
			mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
			.compassEnabled(true);
			
			//add map marker
			Marker savedLocMarker = mMapS.addMarker(new MarkerOptions()
			.position(myLocS)
			.title(nameLoc)
			.snippet(geoDisplay));

			savedLocMarker.showInfoWindow();
			
			//zoom to current location
			mMapS.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocS, 19.0f));
		}
	}

}