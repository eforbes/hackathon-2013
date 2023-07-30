package com.shibedev.heresay;

import java.util.ArrayList;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationActivity extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener{

	LocationClient lc;
	Location loc;
	ListView lv;
	ArrayAdapter<String> updateAdapter;
	ArrayList<String> updates = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		
		lc = new LocationClient(this, this, this);
		lv = (ListView) findViewById(R.id.listView);
		updateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, updates);
		updateAdapter.setNotifyOnChange(true);
		lv.setAdapter(updateAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		lc.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		updates.add("connection FAILED" + arg0.getErrorCode());
	}

	@Override
	public void onConnected(Bundle arg0) {
	    loc = lc.getLastLocation();
	    
		LocationRequest lrHigh = LocationRequest.create()
		.setFastestInterval(300000)
		.setInterval(360000)
		.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
		.setSmallestDisplacement(25f);
		lc.requestLocationUpdates(lrHigh, this);
		
		LocationRequest lrMedium = LocationRequest.create()
		.setFastestInterval(60000)
		.setInterval(61000)
		.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
		.setSmallestDisplacement(5f);
		lc.requestLocationUpdates(lrMedium, this);
		
		Log.e("com.shibedev.heresay", "GPS Connected");
		updates.add("GPS CONNECTED");
		updates.add(lc.getLastLocation() + "");
		updateAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDisconnected() {
		updates.add("DISCONNECTED");
	}

	public void onLocationChanged(Location location) {
		updates.add(location.getLatitude() + " " + location.getLongitude() + " " + location.getProvider());
		updateAdapter.notifyDataSetChanged();
	}
	
	public void getLastLocation(View view) {
		updates.add(lc.getLastLocation() + "");
		updateAdapter.notifyDataSetChanged();
	}
}
