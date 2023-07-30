package hearserv.app_elements;

import java.util.ArrayList;

//TODO this class must support concurrent access of public methods

public class LocaleManager {  
	public static final double RADIUS_OF_EARTH = 6378137; //in meters

	private ArrayList<Locale> locales;
	
	public LocaleManager(){
		locales = new ArrayList<Locale>();
	}
	
	public Locale lookupLocale(long id) throws LocaleNotFoundException {
		for (Locale l : locales){
			if (l.getLocaleID() == id){
				return l;
			}
		}
		throw new LocaleNotFoundException(""+id);
	}
	
	/**
	 * Add a locale to the the master list of locales
	 * @param newLocale the new locale to add
	 * @return true if success, fail if failure
	 */
	public Locale addLocale(double latitude, double longitude, String name, boolean isPrivate, int key, int radius){
		Locale newLocale;
		if(locales.size() == 0){
			newLocale = new Locale(latitude, longitude, name, isPrivate, key, 0, radius);
		}else{
			newLocale = new Locale(latitude, longitude, name, isPrivate, key, locales.size(), radius);
		}
		locales.add(newLocale);
		return newLocale;
	}
	
	public void removeLocale(long id) throws LocaleNotFoundException {
		locales.remove(lookupLocale(id));
	}
	
	public ArrayList<Locale> getNearbyLocales(double latitude, double longitude){		
		ArrayList<Locale> inRangeLocales = new ArrayList<Locale>();
		for(Locale locale : locales){
			double dLat = (latitude - locale.getLatitude()) * Math.PI / 180;
			double dLon = (longitude - locale.getLongitude()) * Math.PI / 180;
			double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
					+ Math.cos(locale.getLatitude() * Math.PI / 180)
					* Math.cos(latitude * Math.PI / 180)
					* Math.sin(dLon / 2) * Math.sin(dLon / 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			double d = RADIUS_OF_EARTH * c; // distance in meters

			if(d < locale.getRadius()){
				inRangeLocales.add(locale);
			}
		}
//		return inRangeLocales;
		return locales; //FIXME
	}
	
	/**
	 * Gives access to the arraylist containing all of the locales.
	 * @deprecated Nothing should need this data.
	 * @return All of the locales.
	 */
	@Deprecated
	public ArrayList<Locale> getLocales() {
		return locales;
	}
}
