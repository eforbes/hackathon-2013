package hearserv;

/**
 * Logging class
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class Logger
{	
	private static int
		NOTHING = 0;
	
	public static int
		SEVERE = 1,
		ERROR = 2,
		WARNING = 3,
		INFO = 4,
		DEBUG = 5;
		
	private static int verbosity = DEBUG;
	
	/**
	 * None shall construct a logger
	 */
	private Logger() {}
	
	/**
	 * Log a string as INFO
	 * @param message the string to log
	 */
	public static void log(String message)
	{
		log(message, INFO);
	}
	
	/**
	 * Log a string at a specified logLevel
	 * @param message the string to log
	 * @param logLevel the specified logLevel
	 */
	public static void log(String message, int logLevel)
	{
		if (verbosity >= logLevel)
		{
			if (logLevel <= NOTHING)
			{
				// do nothing
			}
			else if (logLevel == SEVERE)
			{
				System.err.println("SEVERE: " + message);
			}
			else if (logLevel == ERROR)
			{
				System.err.println("ERROR: " + message);
			}
			else if (logLevel == WARNING)
			{
				System.out.println("WARNING: " + message);
			}
			else if (logLevel == INFO)
			{
				System.out.println("INFO: " + message);
			}
			else if (logLevel >= DEBUG)
			{
				System.out.println("DEBUG: " + message);
			}
		}
	}
	
	/**
	 * Log an exception (as a WARNING)
	 * @param e the exception to log
	 */
	public static void log(Exception e)
	{
		if (verbosity >= WARNING)
		{
			System.err.println("WARNING:");
			e.printStackTrace();
		}
	}
	
	/**
	 * Log an exception at the specified logLevel
	 * @param e the exception to log
	 * @param logLevel the specified logLevel
	 */
	public static void log(Exception e, int logLevel)
	{
		if (verbosity > 0 && verbosity >= logLevel)
		{
			if (logLevel == SEVERE)
			{
				System.err.println("SEVERE:");
			}
			else if (logLevel == ERROR)
			{
				System.err.println("ERROR: ");
			}
			else if (logLevel == WARNING)
			{
				System.out.println("WARNING:");
			}
			else if (logLevel == INFO)
			{
				System.out.println("INFO:");
			}
			else if (logLevel >= DEBUG)
			{
				System.out.println("DEBUG:");
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * Print an object as DEBUG
	 * @param o The object to print
	 */
	public static void log(Object o)
	{
		if (verbosity >= DEBUG)
		{
			System.out.println(o);
		}
	}

	/**
	 * Get the verbosity level of the logger
	 * @return the verbosity level of the logger
	 */
	public static int getVerbosity()
	{
		return verbosity;
	}

	/**
	 * Set the verbosity of the logger
	 * @param verbosity the new verbosity for the logger
	 */
	public static void setVerbosity(int verbosity)
	{
		Logger.verbosity = verbosity;
	}
	
	
}
