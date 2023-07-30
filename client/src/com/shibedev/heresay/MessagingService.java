package com.shibedev.heresay;

import java.io.IOException;
import java.util.ArrayList;

import networking.MessageIDs;
import networking.ServerConnection;
import networking.messages.OutgoingCreatePrivateLocaleRequest;
import networking.messages.OutgoingCreatePublicLocaleRequest;
import networking.messages.OutgoingJoinPrivateLocaleRequest;
import networking.messages.OutgoingJoinPublicLocaleRequest;
import networking.messages.OutgoingKeepAliveNotice;
import networking.messages.OutgoingLeaveLocaleRequest;
import networking.messages.OutgoingNearbyLocaleRequest;
import networking.messages.OutgoingSendTextPostRequest;
import networking.messages.OutgoingVersionCheckRequest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

public class MessagingService extends Service implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{

	public Locale createdLocale;
	public int passwordHash;

	ServerConnection serverConnection;
	private String username;

	/** Allows the UI to be tested
	 *  Blocks all interactions with server
	 *  inserts dummy server responses
	 *   should be false to allow normal functioning */
	private final boolean debugging = false;
	
	private double latitude;
	private double longitude;
	private boolean hasLocation = false;
	private Location currLocation;
	public static final double RADIUS_OF_EARTH = 6378137; // Radius of earth in m

	private boolean networkingStartedSuccessfully;
	
	LocationListener locationListener = new LocationListener() {
		// Called when a new location is found by the network location
		// provider.
		public void onLocationChanged(Location location) {
			//0.0,0.0 is returned sometimes when there isn't a valid new location
			if (!(location.getLatitude() == 0.0 && location.getLongitude() == 0.0)) {
				hasLocation = true;
				if (isBetterLocation(location, currLocation)) {
					currLocation = location;
					latitude = currLocation.getLatitude();
					longitude = currLocation.getLongitude();
					broadcastNewLocationSet();
					Log.i("com.shibedev.heresay", "better location found " + latitude + ", "
							+ longitude);
				} else {
					Log.i("com.shibedev.heresay", "location changed, location not better "+currLocation.getLatitude()+", "+currLocation.getLongitude());
				}
				broadcastGotLocation();
			} else {
				Log.i("com.shibedev.heresay", "0,0 location... still waiting");
			}
		}
	};

	private final IBinder mBinder = new MyBinder();
	private LocationClient locationClient;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		if(locationClient == null) {
			locationClient = new LocationClient(this, this, this);
			locationClient.connect();
		}
		return mBinder;	
	}

	public class MyBinder extends Binder {
		MessagingService getService() {
			return MessagingService.this;
		}
	}

	public void broadcastNewLocationSet() {
		Intent intent = new Intent();
		intent.setAction("com.shibedev.heresay.new_location");
		sendBroadcast(intent);
	}
	public void broadcastMessageReceived(Message msg) {
		Intent intent = new Intent();
		intent.setAction("com.shibedev.heresay.message_received");
		intent.putExtra("message", msg);
		sendBroadcast(intent);
	}


	public void broadcastLocalesAvailable(ArrayList<Locale> locales) {
		Intent intent = new Intent();
		intent.setAction("com.shibedev.heresay.got_locales");
		intent.putExtra("locales", locales);
		sendBroadcast(intent);
	}


	public void broadcastUsernameRegistered(boolean registered) {
		Intent intent = new Intent();
		intent.setAction("com.shibedev.heresay.registered");
		intent.putExtra("successful_register", registered);
		sendBroadcast(intent);
	}

	public void broadcastConnectedToLocale(boolean success, Locale connectedLoc) {
		Intent intent = new Intent();
		intent.setAction("com.shibedev.heresay.connected");
		intent.putExtra("success", success);
		intent.putExtra("locale", connectedLoc);
		sendBroadcast(intent);
	}


	public void broadcastLocaleCreated(boolean successful) {
		Intent intent = new Intent();
		intent.setAction("com.shibedev.heresay.locale_created");
		intent.putExtra("successful_create", successful);
		sendBroadcast(intent);
	}

	public void broadcastLeaveLocale(){
		Intent intent = new Intent();
		intent.setAction("com.shibedev.heresay.locale_left");		
		sendBroadcast(intent);
	}

	public void broadcastNetworkProtocolVersionMismatch(){
		serverConnection.close();
		Intent intent = new Intent();
		intent.setAction("com.shibedev.heresay.networking_version_mismatch"); 
		//TODO make a toast or something saying that the network protocol version is not the same as the servers		
		sendBroadcast(intent);
	}

	public void broadcastNetworkError(String errorMessage){
		Intent intent = new Intent();
		intent.setAction("com.shibedev.heresay.networking_error"); 
		intent.putExtra("network_error", errorMessage);
		//TODO make a toast or something saying that a network error occured		
		sendBroadcast(intent);
	}

	public void broadcastGotLocation() {
		Intent intent = new Intent();
		intent.setAction("com.shibedev.heresay.found_location");
		sendBroadcast(intent);
	}

	/**
	 * This MUST be called first or none of the other methods will work.
	 * @param username the username you'll probably get
	 */
	public void startNetworkingAndSendUsername(String username) {
		this.username = username;
	}

	/**
	 * DON'T CALL THIS unless startNetworkingAndSendUsername() has already been called once
	 */
	public void startNetworking()
	{
		networkingStartedSuccessfully = true;
		if(debugging) {
			networkingStartedSuccessfully = true;
			broadcastUsernameRegistered(true);
		}
		else {
			(new Thread() {
				public void run() {
					try {
						serverConnection = new ServerConnection(MessagingService.this, username);
						new Thread(keepAliveRunnable).start();
					} catch (IOException e) {
						networkingStartedSuccessfully = false;
						Log.e("com.shibedev.heresay", "Server connect error: "+e.getMessage());
					}	
				}
			}).start();
			if (!networkingStartedSuccessfully) {
				Toast.makeText(getBaseContext(), "Error establishing server connection", Toast.LENGTH_LONG).show();
				broadcastUsernameRegistered(false);
			}
		}
	}

	public void requestLocales(double latitude, double longitude) {
		if(debugging) {
			ArrayList<Locale> locales = new ArrayList<Locale>();
			locales.add(new Locale(1L, "Keplinger", false, (short) 10));
			locales.add(new Locale(2L, "Will's Study Group", true, (short) 10));
			broadcastLocalesAvailable(locales);
		}
		else 
			serverConnection.sendMessage(new OutgoingNearbyLocaleRequest(latitude, longitude));
	}

	public void connectToPublicLocale(long id) {
		if(debugging)
			broadcastConnectedToLocale(true, new Locale(1, "David's Locales", false, (short) 6));
		else 
			serverConnection.sendMessage(new OutgoingJoinPublicLocaleRequest(id));
	}

	public void connectToPrivateLocale(long id, int passwordHash) {
		if(debugging)
			broadcastConnectedToLocale(true, new Locale(1, "David's Locales", false, (short) 6));
		else {
			serverConnection.sendMessage(new OutgoingJoinPrivateLocaleRequest(id, passwordHash));
		}
	}	

	public void sendMessage(long localeId, String messageText)
	{
		if(debugging)
			broadcastMessageReceived(new Message("David", "Hello"));
		else
			serverConnection.sendMessage(new OutgoingSendTextPostRequest(localeId, messageText));
	}

	public void leaveLocale(long localeId) {
		if(debugging) {
			broadcastLeaveLocale();
		}
		else
			serverConnection.sendMessage(new OutgoingLeaveLocaleRequest(localeId));
	}

	public void createPublicLocale(String name, double latitude,
			double longitude, int radius) {
		if(debugging) 
			broadcastConnectedToLocale(true, new Locale(1L, "David's Party", false, (short)2));
		else {
			createdLocale = new Locale(-1l,name,false,(short)0);
			createdLocale.setLatitude(latitude);
			createdLocale.setLongitude(longitude);
			createdLocale.setRadius(radius);
			serverConnection.sendMessage(new OutgoingCreatePublicLocaleRequest(name, latitude, longitude, radius));
		}	
	}

	public void createPrivateLocale(String name, double latitude,
			double longitude, int radius, int passwordHash) {
		createdLocale = new Locale(-1l,name,true,(short)0);
		createdLocale.setLatitude(latitude);
		createdLocale.setLongitude(longitude);
		createdLocale.setRadius(radius);
		serverConnection.sendMessage(new OutgoingCreatePrivateLocaleRequest(name, latitude, longitude, radius, passwordHash));
	}

	public void checkNetworkingProtocolVersion(){
		serverConnection.sendMessage(new OutgoingVersionCheckRequest(MessageIDs.PROTOCOL_VERSION));
	}

	public ServerConnection getServerConnection() {
		return serverConnection;
	}

	/** Determines whether one Location reading is better than the current Location fix
	 * @param location  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > 120000;
		boolean isSignificantlyOlder = timeDelta < -120000;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} 
		return false;
	}

	public double[] getLatLong() {
		double[] latLng = {latitude, longitude};
		return latLng;
	}

	public boolean getHasLocation() {
		return hasLocation;
	}

	public void keepAlive(){
		serverConnection.sendMessage(new OutgoingKeepAliveNotice());
	}

	Runnable keepAliveRunnable = new Runnable() {
		@Override
		public void run() {
			while(true) {
				keepAlive();
				synchronized(this) {
					try {
						wait(10000);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}
	};

	public static boolean isWithinLocale(double radius, LatLng latLngLocale, LatLng latLngTest) {

		double dLat = (latLngTest.latitude - latLngLocale.latitude) * Math.PI / 180;
		double dLon = (latLngTest.longitude - latLngLocale.longitude) * Math.PI / 180;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(latLngLocale.latitude * Math.PI / 180)
				* Math.cos(latLngTest.latitude * Math.PI / 180)
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = RADIUS_OF_EARTH * c; // distance in meters

		return d < radius;
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode =
				GooglePlayServicesUtil.
				isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.e("Location Updates",
					"Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			Log.e("com.shibedev.heresay", "Google Play Services not available");
			return false;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.e("com.shibedev.heresay", "GPS connection failed");
		Toast.makeText(this, "Location connection FAILED " + arg0.getErrorCode(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle arg0) {
		currLocation = locationClient.getLastLocation();
		latitude = currLocation.getLatitude();
		longitude = currLocation.getLongitude();
		broadcastGotLocation();
		
		LocationRequest lrHigh = LocationRequest.create()
		.setFastestInterval(300000)
		.setInterval(360000)
		.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
		.setSmallestDisplacement(25f);
		locationClient.requestLocationUpdates(lrHigh, locationListener);
		
		LocationRequest lrMedium = LocationRequest.create()
		.setFastestInterval(60000)
		.setInterval(61000)
		.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
		.setSmallestDisplacement(5f);
		locationClient.requestLocationUpdates(lrMedium, locationListener);
		
		Log.e("com.shibedev.heresay", "Location Client Connected "+currLocation.getLatitude()+", "+currLocation.getLongitude());
	}

	@Override
	public void onDisconnected() {
		Log.e("com.shibedev.heresay", "Location Client Disconnected");
	}
}
