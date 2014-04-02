package com.jbentley.earthquake;

public class SingleQuake {
	int _id;
	String _locationDescription;
	String _locationLatitude;
	String _locationLongitude;
	String _savedDateTime;
	String _mag;


	public SingleQuake(){

	}
	
	public SingleQuake(String desc, String lat, String lng, String saveTime, String mag){
		this._locationDescription = desc;
		this._locationLatitude = lng;
		this._savedDateTime = saveTime;
		this._mag = mag;
	}
	
	public String getDesc(){
		return _locationDescription;
	}

	public String getLat(){
		return _locationLatitude;
	}
	
	public String getLng(){
		return _locationLongitude;
	}
	
	public String getTime(){
		return _savedDateTime;
	}
}
