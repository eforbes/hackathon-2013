package com.shibedev.heresay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	/**Receives notifications from service*/
	private BroadcastReceiver receiver;
	
	/**Service for connecting to Michael's server*/
	private MessagingService service;
	
	/**We successfully registered with the service*/
	private boolean registered = false;
	
	/**User has been located*/
	private boolean located = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		//Retrieve the saved username if it exists
		EditText usernameEditText = (EditText) findViewById(R.id.username);
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		usernameEditText.setText(sharedPref.getString(getString(R.string.saved_username), ""));
		
		//Setup the receiver for the service
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.shibedev.heresay.registered");
		filter.addAction("com.shibedev.heresay.locale_left"); //TODO: What is this doing?
		filter.addAction("com.shibedev.heresay.found_location");
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.hasExtra("successful_register")) {
					boolean success = intent.getBooleanExtra("successful_register", false);
					if(success) {
						registered = true;
						Log.i("shibe", "registered with server");
					}
					else { 
						//TODO: fix this dialog?
				    	/*new AlertDialog.Builder(LoginActivity.this)
				        .setTitle("Connection error")
				        .setMessage("Lost connection to server.")
				        .setNegativeButton("Exit", dialogKillButtonSelected)
				        .setPositiveButton("Retry", retryClicked).show();*/
						progressDialog.dismiss();
						Toast.makeText(getBaseContext(), "Invalid username or network error", Toast.LENGTH_LONG).show();
					}
				}
				else if(intent.getAction().contains("found_location")) {
					located = true;
					Log.i("shibe", "found location");
				}
					
				if(registered && located)
					goToConnect();
			}
		};
		registerReceiver(receiver, filter);
		
		Log.e("com.shibedev.heresay", "Service STARTED");
		Intent serviceIntent = new Intent(this, MessagingService.class);
		this.startService(serviceIntent);
	}
	
	
	  @Override
	  protected void onResume() {
	    super.onResume();
	    //Start the service and connect it to this activity
	    Log.e("com.shibedev.heresay", "Service BOUND in login");
	    bindService(new Intent(this, MessagingService.class), mConnection,
	        Context.BIND_AUTO_CREATE);
	  }

	  @Override
	  protected void onPause() {
	    super.onPause();
	    Log.e("com.shibedev.heresay", "Service UNBOUND in login");
	    unbindService(mConnection);
	    unregisterReceiver(receiver);
	    finish();
	  }
	  
		@Override
		public void onDestroy() {
			super.onDestroy();
			if(progressDialog != null)
				progressDialog.dismiss();
			//TODO: check this doesn't cause problems
//			service.serverConnection.close();
		}

	/**
	 * Connects to the service
	 */
	  private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder binder) {
	      service = ((MessagingService.MyBinder) binder).getService();
	    }
	    public void onServiceDisconnected(ComponentName className) {
	    	new AlertDialog.Builder(getBaseContext())
	        .setTitle("Connection error")
	        .setMessage("Lost connection to server.")
	        .setNeutralButton("Okay", dialogKillButtonSelected).show();
	    }
	  };

	  /**
	   * Handle dialog button presses that kill the app
	   */
	  private OnClickListener dialogKillButtonSelected = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			finish();
		}
	};
	
	  /**
	   * Handle dialog button presses that try to reconnect to server
	   */
	private OnClickListener retryClicked = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			EditText usernameText = (EditText) findViewById(R.id.username);
			String username = usernameText.getText().toString();
			service.startNetworkingAndSendUsername(username);
			
			service.startNetworking();

			progressDialog = ProgressDialog.show(getBaseContext(), "Logging In", "Please wait.");
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	private ProgressDialog progressDialog;
	
	public void register(View view) {
		EditText usernameText = (EditText) findViewById(R.id.username);
		String username = usernameText.getText().toString();
		
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.saved_username), username);
		editor.commit();
		
		service.startNetworkingAndSendUsername(username);
		
		service.startNetworking();

		progressDialog = ProgressDialog.show(this, "Logging In", "Please wait.");
	}
	
	private void goToConnect() {
		progressDialog.dismiss();
		Intent connectIntent = new Intent(this, ConnectActivity.class);
		startActivity(connectIntent);
	}
	
	public void killService(View view) {
		unbindService(mConnection);
		stopService(new Intent(this, MessagingService.class));
	}
	

}
