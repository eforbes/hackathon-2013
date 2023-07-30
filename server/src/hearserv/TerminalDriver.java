package hearserv;

import java.io.IOException;

import hearserv.app_elements.*;
import hearserv.io.networking.*;

/**
 * The primary driver for the entire server
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class TerminalDriver
{	
	// static access to cross-application classes
	private static UserManager userManager;
	private static LocaleManager localeManager;
	private static Server listeningServer;
	
	/**
	 * The long awaited main method
	 * @param args Arguments from program start
	 */
	public static void main(String[] args)
	{
		userManager = new UserManager();
		localeManager = new LocaleManager();
		System.setSecurityManager(new ServerSecurityManager());
		
		listeningServer = null;
		try
		{
			listeningServer = new Server();
			listeningServer.start();
		}
		catch (IOException e)
		{	// if port could not be bound
			Logger.log(e, Logger.SEVERE);
			System.exit(1);
		}
	}

	/**
	 * Gets the main UserManager
	 * @return the main UserManager
	 */
	public static UserManager getUserManager()
	{
		return userManager;
	}

	/**
	 * Gets the main LocaleManager
	 * @return the main LocaleManager
	 */
	public static LocaleManager getLocaleManager()
	{
		return localeManager;
	}
	
	
}
