package hearserv.io.networking.messages;

import hearserv.app_elements.Locale;
import hearserv.io.networking.MessageIDs;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Response to the user requesting to join a locale
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class OutgoingJoinLocaleResponseMessage implements OutgoingMessage
{
	private long joinedLocaleID;
	private boolean successfulJoin;
	
	/**
	 * Response to the user requesting to join a locale
	 * @param joinedLocale locale the user joined
	 * @param successfulJoin if the locale was joined successfully
	 */
	public OutgoingJoinLocaleResponseMessage(Locale joinedLocale, boolean successfulJoin)
	{
		this.joinedLocaleID = joinedLocale.getLocaleID();
		this.successfulJoin = successfulJoin;
	}
	
	/**
	 * Response to the user requesting to join a locale. This constructor is intended for use for when the client
	 * attempts to join a locale ID that the server does not now about
	 * @param joinedLocaleID ID of the locale the user asked to join
	 * @param successfulJoin if the locale was joined successfully
	 */
	public OutgoingJoinLocaleResponseMessage(long joinedLocaleID, boolean successfulJoin)
	{
		this.joinedLocaleID = joinedLocaleID;
		this.successfulJoin = successfulJoin;
	}
	
	@Override
	public void send(DataOutputStream outToClient) throws IOException
	{
		outToClient.writeShort(MessageIDs.JOIN_LOCALE_RESPONSE);
		outToClient.writeLong(joinedLocaleID);
		outToClient.writeBoolean(successfulJoin);
		if (successfulJoin)
		{
			outToClient.writeShort(0); // TODO: implement sending of recent messages
		}
	}	
}
