package com.shibedev.heresay;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class CreateLocaleActivity extends FragmentActivity {

	public static final int[] RADIUS_VALUES = {64,128,256};
	
	private BroadcastReceiver receiver;
	private MessagingService service;

	Spinner radiusSpinner;
	int radius = RADIUS_VALUES[0];
	boolean locked = false;
	
	private CreateLocaleMapManager mapManager;
	LatLng userLatLng; //LatLng for last gps location
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_locale);

		radiusSpinner = (Spinner) findViewById(R.id.radius_spinner);
		radiusSpinner.setOnItemSelectedListener(spinnerChangeListener);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.radius_names, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		radiusSpinner.setAdapter(adapter);
		
		CheckBox checkbox = (CheckBox) findViewById(R.id.set_closed);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				EditText password = (EditText) findViewById(R.id.password);
				if(isChecked) {
					password.setVisibility(View.VISIBLE);
				}
				else password.setVisibility(View.INVISIBLE);
			}
		});
		
		SupportMapFragment mf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapManager = new CreateLocaleMapManager(getParent(), mf.getMap());
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.shibedev.heresay.locale_created");
		filter.addAction("com.shibedev.heresay.connected");
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.hasExtra("successful_create")) {
					boolean success = intent.getBooleanExtra(
							"successful_create", false);
					if (success) {
						if (locked)
							service.connectToPrivateLocale(service.createdLocale.getId(), service.passwordHash);
						else
							service.connectToPublicLocale(service.createdLocale.getId());
					} else
						Toast.makeText(getBaseContext(), "Failed to create",
								Toast.LENGTH_SHORT).show();
				}
				if (intent.hasExtra("locale")) {
					Locale newLocale = (Locale) intent.getSerializableExtra("locale");
					Intent intentToLocaleMessage = new Intent(getBaseContext(),
							LocaleMessageActivity.class);
					intentToLocaleMessage.putExtra("locale", newLocale);
					startActivity(intentToLocaleMessage);
				}
			}
		};
		registerReceiver(receiver, filter);
		
		Log.e("com.shibedev.heresay", "Service BOUND in create");
		bindService(new Intent(this, MessagingService.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e("com.shibedev.heresay", "Service UNBOUND in create");
		unbindService(mConnection);
		unregisterReceiver(receiver);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//		Log.e("com.shibedev.heresay", "Service STOPPED in create");
//		Intent serviceIntent = new Intent(this, MessagingService.class);
//		this.stopService(serviceIntent);
//		service.serverConnection.close();
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((MessagingService.MyBinder) binder).getService();
			updateLocation();
		}

		public void onServiceDisconnected(ComponentName className) {
			service = null;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.create_locale, menu);
		return true;
	}
	
	private void updateLocation() {
		userLatLng = new LatLng(service.getLatLong()[0], service.getLatLong()[1]);
		mapManager.setLocation(userLatLng);
	}
	
	
	private OnItemSelectedListener  spinnerChangeListener = new OnItemSelectedListener () {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int index,
				long id) {
			radius = RADIUS_VALUES[index];
			mapManager.localeCircle.setRadius(radius);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	public void createLocale(View view) {
		Button createButton = (Button) findViewById(R.id.create_locale);
		createButton.setEnabled(false);
		boolean isChecked = ((CheckBox) findViewById(R.id.set_closed))
				.isChecked();
		String name = ((EditText) findViewById(R.id.locale_name)).getText()
				.toString();

		String password = ((EditText) findViewById(R.id.password)).getText()
				.toString();
		service.passwordHash=password.hashCode();
		if (isChecked) {
			locked = isChecked;
			service.createPrivateLocale(name, mapManager.markerLatLng.latitude, mapManager.markerLatLng.longitude, radius, service.passwordHash);
		} else {
			locked = false;
			service.createPublicLocale(name, mapManager.markerLatLng.latitude, mapManager.markerLatLng.longitude, radius);
		}
	}

}
