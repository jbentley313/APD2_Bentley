/*
 *Author: Jason Bentley
 * 
 * Mar 27, 2014
 * 
 * Project = SpotMapper
 * 
 * Package = com.jbentley.spotmapper
 * 
 * 
 * SingleGeoFence.java is a custom class for a single geo fence.
 */
package com.jbentley.spotmapper;

import android.util.Log;

import com.google.android.gms.location.Geofence;

public class SingleGeoFence {

	private String geoID;
	private Float geoRadius;
	private Double geoLatitude;
	private Double geoLongitude;
	private int geoTranistionType;
	private long geoExpiration;

	//empty constructor for a single geofence
	public SingleGeoFence(){

	}

	//single geofence constructor
	public SingleGeoFence(String gId, Float gRadius, Double gLat, Double gLong, int gTransition, long gExpiration) {

		this.geoID = gId;
		this.geoRadius = gRadius;
		this.geoLatitude = gLat;
		this.geoLongitude = gLong;
		this.geoTranistionType = gTransition;
		this.geoExpiration = gExpiration;
	}

	//getters////
	//get id
	public String getID(){
		return geoID;
	}

	//get radius
	public Float getRadius(){
		return geoRadius;
	}

	//get latitude
	public double getLat(){
		return geoLatitude;
	}

	//get longitude
	public double getLng(){
		return geoLongitude;
	}

	//get transition type
	public int getTransition(){
		return geoTranistionType;
	}

	//get expiration 
	public long getExpiration(){
		return geoExpiration;
	}

	//make a new geofence
	public Geofence makeGeoFence(){
		Log.i("Geofence", "builder");
		return new Geofence.Builder()
		.setRequestId(getID())
		.setCircularRegion(getLat(), getLng(), getRadius())
		.setTransitionTypes(getTransition())
		.setExpirationDuration(getExpiration())
		.build();


	}


	
}
