package com.jbentley.spotmapper;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.jbentley.spotmapper.LocationDataBaseHelper;

public class ListNavFragment extends ListFragment implements OnItemLongClickListener{

	LocationDataBaseHelper locDb;
	List<LocationInfo> locs;
	Cursor myCursor;
	String locationName;
	String locationSaved;
	ListView myListView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("LISTFRAG", "hello!");
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.list_nav_frag, container, true);

		init(view);



		return view;

	}

	public void init(View view) {
		// TODO Auto-generated method stub
		Log.i("[X][X]", "INIT!!!!!");

		//get all locations to be displayed
		locDb = new LocationDataBaseHelper(getActivity());

		locs = locDb.getAllLocs();


		ArrayList<String> allLocInfo = new ArrayList<String>();

		//loop thru db to add each entry to adapter
		for (LocationInfo locationsaved : locs) {

			allLocInfo.add(locationsaved.getlocName() + "\n" + 
					locationsaved.getSavedDateTime());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, allLocInfo);

		setListAdapter(adapter);

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		//attach long click listener to listview
		ListView myListView = this.getListView();
		myListView.setOnItemLongClickListener(this);
	}

	//list click handler
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		Intent mapNavIntent = new Intent(getActivity(), SavedSpotNavigation.class);
		String locName = l.getItemAtPosition(position).toString();

		String[] splitString1 = locName.split("\\n");
		Log.i("APlit", splitString1[0]);

		String locationName = splitString1[0];
		String locationSaved = splitString1[1];

		LocationInfo mySavedLOC = locDb.getSingleLocation(locationName, locationSaved);
		mapNavIntent.putExtra("idSavedLoc", mySavedLOC.getId());
		mapNavIntent.putExtra("nameSavedLoc", mySavedLOC.getlocName());
		mapNavIntent.putExtra("latSavedLoc", mySavedLOC.getlocLatitude());
		mapNavIntent.putExtra("longSavedLoc", mySavedLOC.getlocLongitude());
		mapNavIntent.putExtra("geoFenceSavedLoc", mySavedLOC.gettaggedForGeo().booleanValue());
		mapNavIntent.putExtra("timeSavedLoc", mySavedLOC.getSavedDateTime());

		Log.i("Extras LNF", mapNavIntent.getExtras().toString());

		startActivity(mapNavIntent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> lv, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		Log.v("long clicked","pos: " + position);
		String locName = lv.getItemAtPosition(position).toString();

		String[] splitString1 = locName.split("\\n");
		Log.i("APlit", splitString1[0]);

		locationName = splitString1[0];
		locationSaved = splitString1[1];



		deleteItem(position);
		return true;

	}

	private void deleteItem(final int position) {
		// TODO Auto-generated method stub
		//alert for confirmation of delete
		new AlertDialog.Builder(getActivity())

		.setTitle("Delete location " + "\"" + locationName + "\" ?")
		.setMessage("This cannot be undone!")
		.setPositiveButton("Delete", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				LocationDataBaseHelper dbhelp = new LocationDataBaseHelper(getActivity().getApplicationContext());	
				LocationInfo singleLoc = dbhelp.getSingleLocation(locationName, locationSaved);

				dbhelp.deleteLocationFromDB(singleLoc.getId());
				
				LocationDataBaseHelper dbhelp2 = new LocationDataBaseHelper(getActivity().getApplicationContext());	
				int locLength = dbhelp2.getAllLocs().size();
				Log.i("size", String.valueOf(locLength));
				
				init(getView());

				SharedPreferences mySharedPrefs2 = getActivity().getSharedPreferences("mySharedGeoPrefs", Context.MODE_PRIVATE);
				Editor myEditor = mySharedPrefs2.edit();
				
				myEditor.remove(locationName + "_transitionKey");
				myEditor.remove(locationName + "_expirationKey");
				myEditor.remove(locationName + "_radiusKey");
				myEditor.remove(locationName + "_longitudeKey");
				myEditor.remove(locationName + "_latitudeKey");
				myEditor.commit();
			}
		})

		.setCancelable(true)
		.setNegativeButton("Cancel", null)
		.show();

	}

}
