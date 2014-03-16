package com.jbentley.spotmapper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.jbentley.spotmapper.LocationDataBaseHelper;

public class ListNavFragment extends ListFragment {



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
		LocationDataBaseHelper locDb = new LocationDataBaseHelper(getActivity());


		//get all locations to be displayed
		List<LocationInfo> locs = locDb.getAllLocs();  

		ArrayList<String> allLocInfo = new ArrayList<String>();

		//loop thru db to add each entry to adapter
		for (LocationInfo locationsaved : locs) {

			allLocInfo.add(locationsaved.getlocName() + "\n" + 
					locationsaved.getId());
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


}
