package com.jbentley.earthquake;





import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class PrefDisplayActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i("PrefDispalyActivity", "hello");
		setContentView(R.layout.pref_frag);
	}
}
