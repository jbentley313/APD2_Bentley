/*
 *Author: Jason Bentley
 * 
 * Mar 27, 2014
 * 
 * Project = SpotMapper
 * 
 * Package = com.jbentley.spotmapper
 */
package com.jbentley.spotmapper;
import android.util.Log;

public class LocationInfo {

	int _id;
	String _locationName;
	String _locationLatitude;
	String _locationLongitude;
	Boolean _taggedForGeo;
	String _savedDateTime;


	public LocationInfo(){

	}

	//constructor for locationInfo
	public LocationInfo(int id, String locName, String locLatitude, String locLongitude, Boolean taggedForGeo, String savedDateTime){
		this._id = id;
		this._locationName = locName;
		this._locationLatitude = locLatitude;
		this._locationLongitude = locLongitude;
		this._taggedForGeo = taggedForGeo;
		this._savedDateTime = savedDateTime;

	}

	//constructor for locationInfo
	public LocationInfo(String locName, String locLatitude, String locLongitude, Boolean taggedForGeo, String savedDateTime){
		this._locationName = locName;
		this._locationLatitude = locLatitude;
		this._locationLongitude = locLongitude;
		this._taggedForGeo = taggedForGeo;
		this._savedDateTime = savedDateTime;

	}

	//GETTERS///////////////
	//get id
	public int getId(){
		return this._id;
	}

	//get location name
	public String getlocName(){
		return this._locationName;
	}

	//get location latitude
	public String getlocLatitude(){
		return this._locationLatitude;
	}

	//get location longitude
	public String getlocLongitude(){
		return this._locationLongitude;
	}

	//get taggedForGeo Boolean
	public Boolean gettaggedForGeo(){
		return this._taggedForGeo;
	}

	//get saved time
	public String getSavedDateTime(){
		Log.d("DATETIME", "[X]" + _savedDateTime);
		 
		
		return this._savedDateTime;
	}


	//SETTERS//////////////
	//set id
	public void setId(int id){
		this._id = id;
	}

	//set location name
	public void setlocName(String locName){
		this._locationName = locName;
	}

	//set location latitude
	public void setlocLatitude(String locLat){
		this._locationLatitude = locLat;
	}

	//set location longitude
	public void setlocLongitude(String locLong){
		this._locationLongitude = locLong;
	}

	//set taggedForGeo Boolean
	public void settaggedForGeo(Boolean taggedForGeo){
		this._taggedForGeo = taggedForGeo;
	}

	//set date and time
	public void setSavedDateTime(String dateTime){
		this._savedDateTime = dateTime;
	}

	



}
