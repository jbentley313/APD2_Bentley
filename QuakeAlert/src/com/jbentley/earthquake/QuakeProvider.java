/*
 * project     QuakeAlert
 * 
 * package     com.jbentley.earthquake
 * 
 * @author     Jason Bentley
 * 
 * date        Nov 7, 2013
 */
package com.jbentley.earthquake;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jbentley.earthquakeObj.FileManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class QuakeProvider returns desired data based on URI passed here.
 */
public class QuakeProvider extends ContentProvider {
	static String Tag = "QUAKEPROVIDER!!!!";
	FileManager fileManager;
	Context mContext;
	String fileName = "quakeFile";

	public static final String AUTHORITY = "com.jbentley.earthquake.QuakeProvider";

	/**
	 * The Class QuakeData declares projection for info.
	 */
	public static class QuakeData implements BaseColumns {

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.jbentley.earthquake.quake";

		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.quake/vnd.jbentley.earthquake.quake";

		// Define the columns
		public static final String MAGNITUDE_COLUMN = "magnitude";
		public static final String DESCRIPTION_COLUMN = "description";
		public static final String TIME_COLUMN = "time";
		public static final String LAT_COLUMN = "lat";
		public static final String LONG_COLUMN = "longC";

		public static final String[] PROJECTION = { "_Id", MAGNITUDE_COLUMN,
				LAT_COLUMN, LONG_COLUMN, TIME_COLUMN, DESCRIPTION_COLUMN };

		/**
		 * Instantiates a new quake data.
		 */
		private QuakeData() {
		};

	}

	public static final int QUAKES = 1;
	public static final int QUAKES45 = 2;
	public static final int QUAKES25 = 3;
	public static final int QUAKES10 = 4;
	public static final int SEARCHQUAKES = 5;

	private static final UriMatcher URIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		URIMatcher.addURI(AUTHORITY, "/allQuakes", QUAKES);
		URIMatcher.addURI(AUTHORITY, "/quakeMag4.5", QUAKES45);
		URIMatcher.addURI(AUTHORITY, "/quakeMag2.5", QUAKES25);
		URIMatcher.addURI(AUTHORITY, "/quakeMag1.0", QUAKES10);
		URIMatcher.addURI(AUTHORITY, "/search/*", SEARCHQUAKES);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 * android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 * java.lang.String[], java.lang.String, java.lang.String[],
	 * java.lang.String)
	 */

	// Cursor function to sort our the from a saved file
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		MatrixCursor result = new MatrixCursor(QuakeData.PROJECTION);

		String JSONString = FileManager.readStringFile(getContext(), fileName);
		// Log.i(Tag, JSONString);

		Log.i("URI STRING", uri.toString());

		JSONObject jsonob = null;

		JSONArray quakeArray = null;

		JSONObject properties = null;

		JSONObject geometry = null;

		JSONArray coordinates = null;

		Double latitude = null;
		Double longitude = null;

		try {
			jsonob = new JSONObject(JSONString);
			quakeArray = jsonob.getJSONArray("features");
			// Log.i("CASEQUAKES", quakeArray.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (quakeArray == null) {
			Log.i("PROVIDER", "QuakeArray is NULL!!");
			return result;
		}

		// URIMatcher to match URI cases
		switch (URIMatcher.match(uri)) {

		// add all quakes
		case QUAKES:
			Log.i("CASE QUAKES!", "ALL QUAKES!");

			JSONArray feature = null;

			try {
				jsonob = new JSONObject(JSONString);
				feature = jsonob.getJSONArray("features");
				int featureSize = feature.length();

				// loop through JSONobject and add columns and rows with the
				// data
				for (int i = 0; i < featureSize; i++) {

					try {
						properties = quakeArray.getJSONObject(i).getJSONObject(
								"properties");

						geometry = quakeArray.getJSONObject(i).getJSONObject(
								"geometry");

						coordinates = geometry.getJSONArray("coordinates");

						latitude = coordinates.getDouble(1);

						longitude = coordinates.getDouble(0);

						String time = properties.getString("time");

						long passedEpoc = Long.parseLong(time);

						SimpleDateFormat sdf = new SimpleDateFormat(
								"MMM d, yyyy 'at' h:mm a", Locale.US);

						Date resultdate = new Date(passedEpoc);

						// String passDate = resultdate.toString();

						String formattedDate = sdf.format(resultdate);

						result.addRow(new Object[] { i + 1, latitude,
								longitude, properties.get("place"),
								formattedDate, properties.get("mag") });
					} catch (JSONException e) {
						Log.e(Tag, e.toString());
					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		// only add 4.5+ magnitude quakes
		case QUAKES45:
			Log.i("CASE QUAKE45!", "4.5 mag QUAKES!");

			try {
				jsonob = new JSONObject(JSONString);
				quakeArray = jsonob.getJSONArray("features");
				int featureSize = quakeArray.length();

				// loop through JSONobject to parse and place into a listview
				for (int i = 0; i < featureSize; i++) {

					try {
						properties = quakeArray.getJSONObject(i).getJSONObject(
								"properties");

						geometry = quakeArray.getJSONObject(i).getJSONObject(
								"geometry");

						coordinates = geometry.getJSONArray("coordinates");

						latitude = coordinates.getDouble(1);

						longitude = coordinates.getDouble(0);

						String time = properties.getString("time");

						long passedEpoc = Long.parseLong(time);

						SimpleDateFormat sdf = new SimpleDateFormat(
								"MMM d, yyyy 'at' h:mm a", Locale.US);

						Date resultdate = new Date(passedEpoc);

						// String passDate = resultdate.toString();

						String formattedDate = sdf.format(resultdate);
						double quakeMagpass4 = properties.getDouble("mag");
						if (quakeMagpass4 >= 4.5) {
							result.addRow(new Object[] { i + 1, latitude,
									longitude, properties.get("place"),
									formattedDate, properties.get("mag") });
						}

					} catch (JSONException e) {
						Log.e(Tag, e.toString());
					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		// only add 2.5+ magnitude quakes
		case QUAKES25:
			Log.i("CASE QUAKE25!", "2.5 mag QUAKES!");

			try {
				jsonob = new JSONObject(JSONString);
				quakeArray = jsonob.getJSONArray("features");
				int featureSize = quakeArray.length();

				// loop through JSONobject to parse and place into a listview
				for (int i = 0; i < featureSize; i++) {

					try {
						properties = quakeArray.getJSONObject(i).getJSONObject(
								"properties");

						geometry = quakeArray.getJSONObject(i).getJSONObject(
								"geometry");

						coordinates = geometry.getJSONArray("coordinates");

						latitude = coordinates.getDouble(1);

						longitude = coordinates.getDouble(0);
						String time = properties.getString("time");

						long passedEpoc = Long.parseLong(time);

						SimpleDateFormat sdf = new SimpleDateFormat(
								"MMM d, yyyy 'at' h:mm a", Locale.US);

						Date resultdate = new Date(passedEpoc);

						String formattedDate = sdf.format(resultdate);

						double quakeMagpass = properties.getDouble("mag");
						if (quakeMagpass >= 2.5) {
							result.addRow(new Object[] { i + 1, latitude,
									longitude, properties.get("place"),
									formattedDate, properties.get("mag") });
						}

					} catch (JSONException e) {
						Log.e(Tag, e.toString());
					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		// only add 1.0+ magnitude quakes
		case QUAKES10:
			Log.i("CASE QUAKE15!", "1.0 mag QUAKES!");

			try {
				jsonob = new JSONObject(JSONString);
				quakeArray = jsonob.getJSONArray("features");
				int featureSize = quakeArray.length();

				// loop through JSONobject to parse and place into a listview
				for (int i = 0; i < featureSize; i++) {

					try {
						properties = quakeArray.getJSONObject(i).getJSONObject(
								"properties");

						geometry = quakeArray.getJSONObject(i).getJSONObject(
								"geometry");

						coordinates = geometry.getJSONArray("coordinates");

						latitude = coordinates.getDouble(1);

						longitude = coordinates.getDouble(0);

						String time = properties.getString("time");

						long passedEpoc = Long.parseLong(time);

						SimpleDateFormat sdf = new SimpleDateFormat(
								"MMM d, yyyy 'at' h:mm a", Locale.US);

						Date resultdate = new Date(passedEpoc);

						String formattedDate = sdf.format(resultdate);

						double quakeMagpass = properties.getDouble("mag");
						if (quakeMagpass >= 1.0) {
							result.addRow(new Object[] { i + 1, latitude,
									longitude, properties.get("place"),
									formattedDate, properties.get("mag") });
						}

					} catch (JSONException e) {
						Log.e(Tag, e.toString());
					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		// custom search
		case SEARCHQUAKES:
			Log.i("CASE QUAKES!", "Search QUAKES!");

			try {
				jsonob = new JSONObject(JSONString);
				feature = jsonob.getJSONArray("features");
				int featureSize = feature.length();

				String quakeRequested = uri.getLastPathSegment();

				// loop through JSONobject and add columns and rows with the
				// data
				for (int i = 0; i < featureSize; i++) {

					try {
						properties = quakeArray.getJSONObject(i).getJSONObject(
								"properties");

						geometry = quakeArray.getJSONObject(i).getJSONObject(
								"geometry");

						coordinates = geometry.getJSONArray("coordinates");

						latitude = coordinates.getDouble(1);

						longitude = coordinates.getDouble(0);

						String time = properties.getString("time");

						long passedEpoc = Long.parseLong(time);

						SimpleDateFormat sdf = new SimpleDateFormat(
								"MMM d, yyyy 'at' h:mm a", Locale.US);

						Date resultdate = new Date(passedEpoc);

						// String passDate = resultdate.toString();
						mContext = getContext();
						String formattedDate = sdf.format(resultdate);
						// if (passedTextSearch == null) {
						// Toast.makeText(mContext, "Null passed text",
						// Toast.LENGTH_LONG).show();
						// }

						Log.i("PAss", quakeRequested);
						String placeDescription = properties.getString("place");

						placeDescription.replaceAll(" ", "");

						if (placeDescription
								.replaceAll(" ", "")
								.toLowerCase(Locale.getDefault())
								.contains(
										quakeRequested.toLowerCase(Locale
												.getDefault()))) {
							result.addRow(new Object[] { i + 1, latitude,
									longitude, properties.get("place"),
									formattedDate, properties.get("mag") });
						}

					} catch (JSONException e) {
						Log.e(Tag, e.toString());
					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		}

		// TODO Auto-generated method stub

		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#update(android.net.Uri,
	 * android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
