package hearserv.app_elements;

import java.util.ArrayList;

public class Locale
{	
	private ArrayList<User> users;
	private double latitude;
	private double longitude;
	private int radius;
	private String name;
	private long localeID;
	private boolean isPrivate;
	private int key;
	
	/**
	 * 
	 * @param lat latitude
	 * @param lon longitude
	 * @param nm name
	 * @param priv if the locale is private
	 * @param k key
	 */
	public Locale(double lat, double lon, String nm, boolean priv, int k, long id, int radius){
		localeID = id;
		latitude = lat;
		longitude = lon;
		name = nm;
		isPrivate = priv;
		key = k;
		this.radius = radius;
		users = new ArrayList<User>();
	}
	
	public void forwardPost(Post p){
		for (User u : users){
			u.recievePost(p);
		}
	}

	public ArrayList<User> getUsers() {
		return users;
	}	

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public int getRadius() {
		return radius;
	}

	public String getName() {
		return name;
	}

	public long getLocaleID() {
		return localeID;
	}
	
	public void addUser(User u){
		users.add(u);
	}

	public void removeUser(User u)
	{
		users.remove(u);
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}

	public int getKey() {
		return key;
	}
	
	public short getNumberOfUsers(){
		return (short)users.size();
	}
}
