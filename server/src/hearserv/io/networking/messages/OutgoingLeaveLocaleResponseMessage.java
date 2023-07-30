package hearserv.io.networking.messages;

import hearserv.app_elements.Locale;
import hearserv.io.networking.MessageIDs;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * response to the user leaving a locale
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class OutgoingLeaveLocaleResponseMessage implements OutgoingMessage
{
	private Locale leftLocale;
	
	/**
	 * response to the user leaving a locale
	 * @param leftLocale the locale the user has left
	 */
	public OutgoingLeaveLocaleResponseMessage(Locale leftLocale)
	{
		this.leftLocale = leftLocale;
	}
	
	@Override
	public void send(DataOutputStream outToClient) throws IOException
	{
		outToClient.writeShort(MessageIDs.LEAVE_LOCALE_RESPONSE);
		outToClient.writeLong(leftLocale.getLocaleID());
	}	
}
