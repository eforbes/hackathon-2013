package hearserv.io.networking.messages;

import hearserv.app_elements.Locale;
import hearserv.io.networking.MessageIDs;
import hearserv.util.Util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Response to the user asking about nearby locales
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class OutgoingNearbyLocaleResponseMessage implements OutgoingMessage
{
	private ArrayList<Locale> locales;
	
	/**
	 * Response to the user asking about nearby locales
	 * @param locales A list of locales near this user
	 */
	public OutgoingNearbyLocaleResponseMessage(ArrayList<Locale> locales)
	{
		this.locales = locales;
	}
	
	@Override
	public void send(DataOutputStream outToClient) throws IOException
	{
		outToClient.writeShort(MessageIDs.NEARBY_LOCALE_RESPONSE);
		outToClient.writeShort((short)locales.size());
		for (Locale locale : locales)
		{
			outToClient.writeLong(locale.getLocaleID());
			outToClient.writeByte(Util.getByte(locale.isPrivate()));
			outToClient.writeShort((short)locale.getName().length());
			outToClient.writeChars(locale.getName());
			outToClient.writeShort(locale.getNumberOfUsers());
		}
	}	
}
