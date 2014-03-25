package com.jbentley.spotmapper;

import com.google.android.gms.location.Geofence;

public class SingleGeoFence {

	private String geoID;
	private Float geoRadius;
	private Double geoLatitude;
	private Double geoLongitude;
	private int geoTranistionType;
	private long geoExpiration;

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
	
	//getters
	public String getID(){
		return geoID;
	}
	
	public Float getRadius(){
		return geoRadius;
	}

	public Double getLat(){
		return geoLatitude;
	}
	
	public Double getLng(){
		return geoLongitude;
	}
	
	public int getTransition(){
		return geoTranistionType;
	}
	
	public Long getExpiration(){
		return geoExpiration;
	}
	
	public Geofence makeGeoFence(){
		return new Geofence.Builder()
		.setRequestId(getID())
		.setCircularRegion(getLat(), getLng(), getRadius())
		.setTransitionTypes(getTransition())
		.setExpirationDuration(getExpiration())
		.build();
	}
}