package com.shibedev.heresay;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class LocaleMessageActivity extends Activity {


	ArrayList<Message> messages = new ArrayList<Message>();
	MessageArrayAdapter messageAdapter;
	ListView messagesListView;

	Locale locale;

	private BroadcastReceiver receiver;
	private MessagingService service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locale_message);
		if(getIntent().hasExtra("locale")) {
			locale = (Locale) getIntent().getSerializableExtra("locale");
			this.setTitle(locale.getName());
		}
		if (locale==null) {
			Log.e("shibe","locale null");
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.shibedev.heresay.message_received");
		filter.addAction("com.shibedev.heresay.locale_left");
		filter.addAction("com.shibedev.heresay.new_location");
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.hasExtra("message")) {
					Message newMessage = (Message) intent.getSerializableExtra("message");
					messages.add(newMessage);
					Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					v.vibrate(500);
					runOnUiThread(updateMessageListRunnable);
				}
				else if(intent.getAction().equals("com.shibedev.heresay.locale_left")) {
					progressDialog.dismiss();
					Intent goToConnect = new Intent(getBaseContext(), ConnectActivity.class);
					startActivity(goToConnect);
				}
				else if(intent.getAction().contains("new_location")) {
					LatLng latLngTest = new LatLng(service.getLatLong()[0], service.getLatLong()[1]);
					LatLng latLngLocale = new LatLng(locale.getLatitude(), locale.getLongitude());

					if(!MessagingService.isWithinLocale(locale.getRadius(), latLngLocale, latLngTest)) {
						Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
						vibe.vibrate(1000);
						service.leaveLocale(locale.getId());
						progressDialog = ProgressDialog.show(LocaleMessageActivity.this, "Out of Locale", "You left the area of the locale");
					}
				}
			}
		};
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("com.shibedev.heresay", "Service BOUND in messaging");
		bindService(new Intent(this, MessagingService.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		Log.e("com.shibedev.heresay", "Service UNBOUND in messaging");
//		unbindService(mConnection);
//		unregisterReceiver(receiver);
//		finish();
	}
	
	ProgressDialog progressDialog;
	/**
	 * Intercepts the back button and sends you back to the connect
	 * otherwise, you might go to whatever the last screen was
	 */
	@Override
	public void onBackPressed() {
		service.leaveLocale(locale.getId());
		progressDialog = ProgressDialog.show(this, "Leaving", "Leaving this locale");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//		service.serverConnection.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.locale_message, menu);
		return true;
	}

	public void sendMessage(View view) {
		Button sendButton = (Button) findViewById(R.id.send_button);
		sendButton.setEnabled(false);
		Timer reEnableButton = new Timer();
		reEnableButton.schedule(new ReEnableButtonTask(), 2000);
		EditText messageEditText = (EditText) findViewById(R.id.message_text);
		service.sendMessage(locale.getId(), messageEditText.getText().toString());
		messageEditText.setText("");
	}
	
	private class ReEnableButtonTask extends TimerTask {

		@Override
		public void run() {
			Button sendButton = (Button) findViewById(R.id.send_button);
			sendButton.setEnabled(true);
		}
		
	}
	
	Handler UIHandler = new Handler();
	Runnable ButtonToggler = new Runnable() {
		
		@Override
		public void run() {
			Button sendButton = (Button) findViewById(R.id.send_button);
			sendButton.setEnabled(!sendButton.isEnabled());
		}
	};

	private void updateMessageList() {
		messagesListView.setAdapter(new MessageArrayAdapter(this, messages));
		messagesListView.setSelection(messageAdapter.getCount() - 1);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((MessagingService.MyBinder) binder).getService();
			
			//TODO: Get Messages from somewhere
//			ArrayList<Message> pastMessages = service.getServerConnection().getIncomingMessageReaderThread().getMessagesList();
			ArrayList<Message> pastMessages = new ArrayList<Message>();
			if (pastMessages!=null)
			for (Message message : pastMessages) {
				messages.add(message);
			}
			
//			locale = service.getServerConnection().getIncomingMessageReaderThread().getCurrentLocale();

			messagesListView = (ListView) findViewById(R.id.message_list);

			messageAdapter = new MessageArrayAdapter(getBaseContext(), messages); 

			messagesListView.setAdapter(messageAdapter);
			messagesListView.setSelection(messageAdapter.getCount() - 1);
		}

		public void onServiceDisconnected(ComponentName className) {
			service = null;
		}
	};


	Runnable updateMessageListRunnable = new Runnable() {
		@Override
		public void run() {
			updateMessageList();
		}
	};



class MessageArrayAdapter extends ArrayAdapter<Message> {
	  private final Context context;
	  private final Message[] messages;

	  public MessageArrayAdapter(Context context, ArrayList<Message> messagesList) {
	    super(context, R.layout.message_row_layout, messagesList);
	    this.context = context;
	    this.messages = new Message[messagesList.size()];
	    for (int i=0;i<messages.length;i++) {
	    	messages[i] = messagesList.get(i);
	    }
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.message_row_layout, parent, false);
	    TextView message = (TextView) rowView.findViewById(R.id.message);
	    TextView sender = (TextView) rowView.findViewById(R.id.sender);
	    message.setText(messages[position].getMessage());
	    sender.setText(messages[position].getSender());

	    return rowView;
	  }
	} 
}