package com.jbentley.spotmapper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.jbentley.spotmapper.LocationDataBaseHelper;

public class ListNavFragment extends ListFragment  {

	LocationDataBaseHelper locDb;
	List<LocationInfo> locs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("LISTFRAG", "hello!");
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.list_nav_frag, container, false);
		//		ListFragment.setListAdapter(null);
		ListView listView = (ListView) view.findViewById(android.R.id.list);
		TextView noSavedLocsText = (TextView) view.findViewById(android.R.id.empty);





		//get all locations to be displayed
		 locDb = new LocationDataBaseHelper(getActivity());


		//get all locations to be displayed
		 locs = locDb.getAllLocs();  

		ArrayList<String> allLocInfo = new ArrayList<String>();

		//loop thru db to add each entry to adapter
		for (LocationInfo locationsaved : locs) {

			allLocInfo.add(locationsaved.getlocName() + "\n" + 
					locationsaved.getSavedDateTime());
		}


		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, allLocInfo);


		this.setListAdapter(adapter);


		return view; 
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}


	//list click handler
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		Log.i("LISTCLICK", String.valueOf(position + 1));
		Intent mapNavIntent = new Intent(getActivity(), SavedSpotNavigation.class);
		
		
		LocationInfo mySavedLOC = locDb.getSingleLocation(position + 1);
		
		mapNavIntent.putExtra("nameSavedLoc", mySavedLOC.getlocName());
		mapNavIntent.putExtra("latSavedLoc", mySavedLOC.getlocLatitude());
		mapNavIntent.putExtra("longSavedLoc", mySavedLOC.getlocLongitude());
		mapNavIntent.putExtra("geoFenceSavedLoc", mySavedLOC.gettaggedForGeo().booleanValue());
		mapNavIntent.putExtra("timeSavedLoc", mySavedLOC.getSavedDateTime());
		
		
		
		Log.i("Extras LNF", mapNavIntent.getExtras().toString());
		
		startActivity(mapNavIntent);

	}




}
