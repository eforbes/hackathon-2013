package com.shibedev.heresay;

import java.io.Serializable;

public class Locale implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private boolean closed;
	private short numberOfPeople;
	private double latitude;
	private double longitude;
	private int radius;

	public Locale(long id, String name, boolean closed, short numberOfPeople) {
		this.id = id;
		this.name = name;
		this.closed = closed;
		this.numberOfPeople = numberOfPeople;
	}
	
	public Locale(String name, boolean closed, short numberOfPeople) {
		this.id = -1l;
		this.name = name;
		this.closed = closed;
		this.numberOfPeople = numberOfPeople;
	}

	public short getNumberOfPeople() {
		return numberOfPeople;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isClosed() {
		return closed;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void setId(long localeId) {
		this.id = localeId;
		
	}

}
