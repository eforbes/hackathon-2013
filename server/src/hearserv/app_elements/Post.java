package hearserv.app_elements;

/**
 * A text post a user sent to a locale
 */
public class Post
{
	private String message;
	private User sourceUser;
	private Locale destinationLocale;
	
	/**
	 * A text post a user sent to a locale
	 * @param message The text in the message
	 * @param sourceUser The user who sent the post
	 * @param destinationLocale The locale the message was sent to
	 */
	public Post(String message, User sourceUser, Locale destinationLocale)
	{
		this.message = message;
		this.sourceUser = sourceUser;
		this.destinationLocale = destinationLocale;
	}
	
	/**
	 * Get the textual message
	 * @return the textual message
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Get the source user
	 * @return the source user
	 */
	public User getSourceUser()
	{
		return sourceUser;
	}
	
	/**
	 * Get the destination locale
	 * @return the destination locale
	 */
	public Locale getDestinationLocale()
	{
		return destinationLocale;
	}
	
	/**
	 * Send the post to its destination
	 */
	public void send()
	{
		destinationLocale.forwardPost(this);
	}
}
