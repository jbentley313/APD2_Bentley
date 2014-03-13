package com.jbentley.spotmapper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocationDataBaseHelper extends SQLiteOpenHelper{

	//db version, name, and table name
	private static final int DATABASE_VERSION = 1;
	private static final String LOCATION_DATABASE_NAME = "locationDatabase";
	private static final String LOCATION_DATABASE_TABLE_NAME = "locations";

	//db column names
	private static final String LOC_ID = "id";
	private static final String LOC_NAME = "location_name";
	private static final String LOC_LATITUDE = "location_latitude";
	private static final String LOC_LONGITUDE = "location_longitude";
	private static final String LOC_GEOFENCE = "location_geoFence";

	//Location db table create
	private static final String LOCATION_DATABASE_TABLE_CREATE =
			"CREATE TABLE " + LOCATION_DATABASE_TABLE_NAME + " (" +
					LOC_ID + " INTEGER PRIMARY KEY, " +
					LOC_NAME + " TEXT, " + 
					LOC_LATITUDE + " TEXT, " + 
					LOC_LONGITUDE + " TEXT, " + 
					LOC_GEOFENCE + " TEXT" + ");";

	public LocationDataBaseHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, LOCATION_DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(LOCATION_DATABASE_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + LOCATION_DATABASE_TABLE_NAME);

		// Create the new tables 
		onCreate(db);
	}


	//add location to DB
	public void addLocationtoDB(LocationInfo locInfo){
		SQLiteDatabase locDb = this.getWritableDatabase();
		ContentValues cValues = new ContentValues();
		cValues.put(LOC_NAME, locInfo.getlocName());
		cValues.put(LOC_LATITUDE, locInfo.getlocLatitude());
		cValues.put(LOC_LONGITUDE, locInfo.getlocLongitude());
		cValues.put(LOC_GEOFENCE, locInfo.gettaggedForGeo());
		
		//insert location info to db
		locDb.insert(LOCATION_DATABASE_TABLE_NAME, null, cValues);
		
		Log.i("ADD to DATABASE", cValues.toString());
		
		//close the db
		locDb.close();
			
	}

	//get all locations from DB
	public List<LocationInfo> getAllLocs() {
	    List<LocationInfo> locList = new ArrayList<LocationInfo>();
	    
	    // Select All Query
	    String selectAllLocations = "SELECT  * FROM " + LOCATION_DATABASE_TABLE_NAME;
	 
	    //get writable database
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectAllLocations, null);
	 
	    //loop thru database and build a list of saved locations
	    if (cursor.moveToFirst()) {
	        do {
	            LocationInfo loc = new LocationInfo();
	            loc.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(LOC_ID))));
	            loc.setlocName(cursor.getString(cursor.getColumnIndex(LOC_NAME)));
	            loc.setlocLatitude(cursor.getString(cursor.getColumnIndex(LOC_LATITUDE)));
	            loc.setlocLongitude(cursor.getString(cursor.getColumnIndex(LOC_LONGITUDE)));
	            loc.settaggedForGeo(cursor.getInt(cursor.getColumnIndex(LOC_GEOFENCE))>0);/*get boolean value*/
	            
	            //add location to location list
	            locList.add(loc);
	            
	        } while (cursor.moveToNext());
	        
	        cursor.close();
	    }
	 
	    return locList;
	}
	
}
