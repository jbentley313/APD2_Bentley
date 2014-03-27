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

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
