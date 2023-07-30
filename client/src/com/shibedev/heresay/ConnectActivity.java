package com.shibedev.heresay;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectActivity extends Activity {

	//TODO: somewhere in this class: handle joining private locales: pop up, ask for password, call service.connectToPrivateLocale

	ArrayList<Locale> locales;
	ListView localeList;
	MessagingService service;
	BroadcastReceiver receiver;
	ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);

		locales = new ArrayList<Locale>();
	}

	@Override
	public void onResume() {
		super.onResume();

		//Listen for getting locale list
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.shibedev.heresay.got_locales");
		filter.addAction("com.shibedev.heresay.connected");
		filter.addAction("com.shibedev.heresay.locale_left");
		receiver = new BroadcastReceiver() {
			@SuppressWarnings("unchecked")
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.hasExtra("locales")) {
					progressDialog.dismiss();
					
					//Yeah, there's a warning
					//the only other option is have ArrayList<obj> which is weird
					locales = (ArrayList<Locale>) intent.getSerializableExtra("locales");
					if(locales.isEmpty()) {
						AlertDialog.Builder alert = new AlertDialog.Builder(getBaseContext());

						alert.setTitle("No locales found nearby.");
						alert.setMessage("Refresh?");
						alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								double[] latLng = service.getLatLong();
								service.requestLocales(latLng[0], latLng[1]);
							}
						});

						alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// Canceled.
							}
						});

						alert.show();
					}
					else 
						updateLocaleList(locales);
				}
				else if(intent.hasExtra("locale")) {
					boolean success = intent.getBooleanExtra("success", false);
					if (success) {
						Locale connectedLocale = (Locale) intent.getSerializableExtra("locale");
						goToMessaging(connectedLocale);
						localeGetDialog.dismiss();
					} else {
						Toast.makeText(getBaseContext(), "Invalid password", Toast.LENGTH_SHORT).show();
						localeGetDialog.dismiss();
					}
					
				}
				else if(intent.getAction().contains("locale_left")) {
					//TODO: notify that locale successfully left
				}
			}
		};
		registerReceiver(receiver, filter);

		progressDialog = ProgressDialog.show(this, "Loading", "Getting nearby locales");
		
		Log.e("com.shibedev.heresay", "Service BOUND in connect");
		bindService(new Intent(this, MessagingService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		if(service != null) {
			double[] latLng = service.getLatLong();
			service.requestLocales(latLng[0], latLng[1]);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("com.shibedev.heresay", "Service STOPPED in connect");
		Intent serviceIntent = new Intent(this, MessagingService.class);
		this.stopService(serviceIntent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.e("com.shibedev.heresay", "Service UNBOUND in connect");
		unbindService(mConnection);
		unregisterReceiver(receiver);
	}
	
	/**
	 * Intercepts the back button and asks the user if they want to quit
	 */
	@Override
	public void onBackPressed() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Exit?");
		alert.setMessage("Are you sure you want to exit?");
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);
				finish();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}


	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((MessagingService.MyBinder) binder).getService();
			double[] latLng = service.getLatLong();
			service.requestLocales(latLng[0], latLng[1]);
		}

		public void onServiceDisconnected(ComponentName className) {
			service = null;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connect, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
			service.requestLocales(service.getLatLong()[0], service.getLatLong()[1]);
			return true;
		case R.id.testGPS:
			Intent intent = new Intent(this, LocationActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Grabs locales from the service and updates UI
	 */
	private void updateLocaleList(ArrayList<Locale> locales) {
		localeList = (ListView) findViewById(R.id.localities);
		localeList.setOnItemClickListener(clickListener);
		LocaleArrayAdapter localeAdapter = new LocaleArrayAdapter(getBaseContext(), locales);
		localeList.setAdapter(localeAdapter);
	}
	

	private OnItemClickListener clickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			selectLocale(arg2);
		}
	};

	int desiredLocale;

	ProgressDialog localeGetDialog;
	private void selectLocale(int which) {
		desiredLocale = which;
		if(locales.get(which).isClosed()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Password");
			alert.setMessage("Enter Password: ");

			// Set an EditText view to get user input 
			final EditText password = new EditText(this);
			//set it hide the text like a password field should
			password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			alert.setView(password);
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String passwordStr = password.getText().toString();
					service.connectToPrivateLocale(locales.get(desiredLocale).getId(), passwordStr.hashCode());
					localeGetDialog = ProgressDialog.show(ConnectActivity.this, "Loading", "Connecting to locale");
				}
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			alert.show();

		}
		else  {
			service.connectToPublicLocale(locales.get(which).getId());
			localeGetDialog = ProgressDialog.show(this, "Loading", "Connecting to locale");
		}
	}


	private void goToMessaging(Locale connectedLocale) {
		Intent messagingIntent = new Intent(this, LocaleMessageActivity.class);
		messagingIntent.putExtra("locale", connectedLocale);
		startActivity(messagingIntent);
	}

	public void createLocale(View view) {
		Intent createIntent = new Intent(this, CreateLocaleActivity.class);
		startActivity(createIntent);
	}
}

class LocaleArrayAdapter extends ArrayAdapter<Locale> {
	  private final Context context;
	  private final Locale[] locales;

	  public LocaleArrayAdapter(Context context, ArrayList<Locale> localeList) {
	    super(context, R.layout.message_row_layout, localeList);
	    this.context = context;
	    this.locales = new Locale[localeList.size()];
	    for (int i=0;i<locales.length;i++) {
	    	locales[i] = localeList.get(i);
	    }
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.connect_row_layout, parent, false);
	    TextView localeName = (TextView) rowView.findViewById(R.id.locale_name2);
	    TextView numberOfPeople = (TextView) rowView.findViewById(R.id.numberOfPeople);
	    ImageView locked = (ImageView) rowView.findViewById(R.id.lock);
	    
	    localeName.setText(locales[position].getName());
	    numberOfPeople.setText(locales[position].getNumberOfPeople()+"");

	    if (!locales[position].isClosed()) {
	    	//View.GONE?
	    	locked.setVisibility(View.INVISIBLE);
	    }
	    
	    return rowView;
	  }
	} 