/*
 * project     Earthquake (week3)
 * 
 * package     com.jbentley.earthquakeObj
 * 
 * @author     Jason Bentley
 * 
 * date        Nov 7, 2013
 */

package com.jbentley.earthquakeObj;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.jbentley.connectivityPackage.connectivityClass;
import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class QuakeDownloadService is a service class (IntentService) that
 * handles downloading data NOT on main thread.
 */
public class QuakeDownloadService extends IntentService {

	//
	final connectivityClass connectionCheckr = new connectivityClass();

	static String Tag = "QuakeDLSERVICE";
	public static final String MESSENGER_KEY = "messenger";
	public static final String QUAKEMAG_KEY = "quake";
	public static String responseString = "";
	public static  boolean isDoneLoading = false;

	/**
	 * Instantiates a new quake download service.
	 */
	public QuakeDownloadService() {
		super("QuakeDownloadService");
		// TODO Auto-generated constructor stub
	}

	/*
	 * Receives intent data (getExtras) and changes the URL Request to the USGS
	 * website. Each spinner item in main activity contains a string that is
	 * passed here to change the Magnitude minimum desired to be displayed.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		// TODO Auto-generated method stub
		Log.i("ONHandleINTENT", "STARTED!!!");

		Bundle extras = intent.getExtras();
		Messenger messenger = (Messenger) extras.get(MESSENGER_KEY);
		String magChosen = (String) extras.get(QUAKEMAG_KEY);
		URL passedURL;
		passedURL = null;
		QuakeDownloadService.isDoneLoading = false;
		if (magChosen.equalsIgnoreCase("All Recent Quakes")) {
			try {
				passedURL = new URL(
						"http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson");

				responseString = getQuakesResponse(passedURL);

			} catch (MalformedURLException e) {
				responseString = "Something went wrong";
				Log.e(Tag, "Error", e);
			}

		}
		Log.i(Tag, magChosen);

		// pass the message back to MainActivity --handler
		Message message = Message.obtain();
		message.arg1 = Activity.RESULT_OK;
		message.obj = responseString;

		try {
			messenger.send(message);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets response from URL that is passed from onHandleIntent
	 * 
	 */
	public static String getQuakesResponse(URL url) {

		String response = "";
		try {
			URLConnection connect = url.openConnection();
			BufferedInputStream bufferInputStream = new BufferedInputStream(
					connect.getInputStream());
			byte[] contextBytes = new byte[1024];
			int byteRead = 0;
			StringBuffer responseBuffer = new StringBuffer();
			while ((byteRead = bufferInputStream.read(contextBytes)) != -1) {
				response = new String(contextBytes, 0, byteRead);
				responseBuffer.append(response);
//				Log.i("NO", "not done yet   " + String.valueOf(isDoneLoading));
				
				
			}
			response = responseBuffer.toString();
			QuakeDownloadService.isDoneLoading = true;
//			Log.i("YES", "DONEEEEEEEEEEEE     " + String.valueOf(isDoneLoading));

		} catch (IOException e) {
			response = "NO INFO PASSED";
			Log.e("IOEXCEPTION", response);
		}
		
		return response;

	}
}