package com.shibedev.heresay;

import android.app.Activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CreateLocaleMapManager implements OnMarkerDragListener{

	public static final int MAX_MARKER_CHANGE = 256;
	public static final short ALPHA_MIN = 0x00;
	public static final short ALPHA_MAX = 0x22;
	public static final int ALPHA_STEPS = 50;

	LatLng userLatLng;
	LatLng markerLatLng; //LatLng marker location
	GoogleMap map;
	Circle localeCircle;
	Circle validMarkerCircle;
	Marker marker;
	
	Activity parent;
	
	int currentValidMarkerCircleAlpha;
	
	Thread animationThread;
	
	public CreateLocaleMapManager(Activity parent, GoogleMap map) {
		this.parent = parent;
		this.map=map;
		setUpMap();
	}

	private void setUpMap() {
		map.setOnMarkerDragListener(this);
	}
	
	@Override
	public void onMarkerDrag(Marker marker) {
		if (MessagingService.isWithinLocale(MAX_MARKER_CHANGE, userLatLng, marker.getPosition())) {
			markerLatLng = marker.getPosition();
			localeCircle.setCenter(markerLatLng);
		} else {
			marker.setPosition(markerLatLng);
		}
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		marker.setPosition(markerLatLng);
		validMarkerCircle.setVisible(false);
		/*if (animationThread!=null) {
			animationThread.interrupt();
		}
		animationThread = new Thread(validMarkerCircleFadeOut);
		animationThread.start();*/
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		validMarkerCircle.setVisible(true);
		/*if (animationThread!=null) {
			animationThread.interrupt();
		}
		animationThread = new Thread(validMarkerCircleFadeIn);
		animationThread.start();*/
	}
	
	Runnable validMarkerCircleFadeIn = new Runnable() {
		@Override
		public void run() {
			double step = ((double)(ALPHA_MAX-ALPHA_MIN))/ALPHA_STEPS;
			for(double i=currentValidMarkerCircleAlpha;i<ALPHA_MAX;i+=step) {
				int intI = (int) i;
				currentValidMarkerCircleAlpha = intI;
				parent.runOnUiThread(setValidMarkerCircleColor);
				synchronized(this) {
					try {
						wait(25);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}
	};
	
	Runnable validMarkerCircleFadeOut = new Runnable() {
		@Override
		public void run() {
			double step = ((double)(ALPHA_MAX-ALPHA_MIN))/ALPHA_STEPS;
			for(double i=currentValidMarkerCircleAlpha;i>ALPHA_MIN;i-=step) {
				int intI = (int) i;
				currentValidMarkerCircleAlpha = intI;
				parent.runOnUiThread(setValidMarkerCircleColor);
				synchronized(this) {
					try {
						wait(25);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}
	};
	
	Runnable setValidMarkerCircleColor = new Runnable() {
		@Override
		public void run() {
			int baseAlpha = currentValidMarkerCircleAlpha << 24;
			validMarkerCircle.setFillColor(baseAlpha);//lol
			validMarkerCircle.setStrokeColor(baseAlpha*2);
		}
	};
	
	public void setLocation(LatLng userLatLng) {
		if (this.userLatLng==null){
			this.userLatLng = userLatLng;
			this.markerLatLng = userLatLng;
			CircleOptions circleOptions = new CircleOptions()
		    .center(userLatLng)
		    .radius(50)
		    .fillColor(0x9999CC00)
		    .strokeColor(0xFF669900)
			.strokeWidth(5.0f);
	
		    localeCircle = map.addCircle(circleOptions);
		    
		    circleOptions = new CircleOptions()
		    .center(userLatLng)
		    .radius(MAX_MARKER_CHANGE)
		    .fillColor(0x22000000)
		    .strokeColor(0x55000000)
		    .strokeWidth(2.5f)
		    .visible(false);
		    
		    validMarkerCircle = map.addCircle(circleOptions);
			
			marker = map.addMarker(new MarkerOptions()
	        .position(markerLatLng)
	        .title("Locale")
	        .icon(BitmapDescriptorFactory.defaultMarker(39)) //TODO: custom marker?
	        .draggable(true));
			
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng, 13));
			map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
		} else {
			this.userLatLng = userLatLng;
			this.markerLatLng = userLatLng;
			localeCircle.setCenter(userLatLng);
			validMarkerCircle.setCenter(markerLatLng);
			marker.setPosition(markerLatLng);
		}

	}
}
