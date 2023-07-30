package hearserv.app_elements;

import hearserv.Logger;
import hearserv.io.networking.ClientConnection;
import hearserv.io.networking.messages.OutgoingNewTextPostNoticeMessage;

public class User
{	
	private long userID;
	private String name;
	private Locale currentLocale;
	private ClientConnection connection;
	
	/**
	 * Note: if you want to manage several users use the UserManager class
	 * @param uID a unique user id managed by the UserManager
	 * @param nm nickname of the user
	 * @param cn ClientConnection 
	 */
	public User(long uID, String nm, ClientConnection cn){
		connection = cn;
		userID = uID;
		name = nm;
	}
	
	public void joinLocale(Locale loc){
		currentLocale = loc;
	}
		
	public void recievePost(Post post){
		Logger.log("forwarding " + post.getSourceUser().getName() + "'s post \"" + post.getMessage() + "\" to "
				+ getName(), Logger.DEBUG);
		connection.sendMessage(new OutgoingNewTextPostNoticeMessage(post));
	}

	public long getUserID() {
		return userID;
	}

	public String getName() {
		return name;
	}

	public Locale getCurrentLocale() {
		return currentLocale;
	}

	public ClientConnection getConnection() {
		return connection;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (userID ^ (userID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		User other = (User) obj;
		if (userID != other.userID) return false;
		return true;
	}

	public void leaveAllLocales()
	{
		// TODO IMPLEMENT THIS
		
	}
	
	
}
