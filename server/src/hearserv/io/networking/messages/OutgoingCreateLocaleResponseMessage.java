package hearserv.io.networking.messages;
import java.io.DataOutputStream;
import java.io.IOException;

import hearserv.app_elements.Locale;
import hearserv.io.networking.MessageIDs;

/**
 * Response to the client creating a locale
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class OutgoingCreateLocaleResponseMessage implements OutgoingMessage
{
	private boolean localeSuccessfullyCreated;
	private Locale createdLocale;
	
	/**
	 * Response to the client creating a locale
	 * @param localeSuccessfullyCreated true if locale was created, false otherwise
	 * @param locale created locale or null if no locale created
	 */
	public OutgoingCreateLocaleResponseMessage(boolean localeSuccessfullyCreated, Locale createdLocale)
	{
		this.localeSuccessfullyCreated = localeSuccessfullyCreated;
		this.createdLocale = createdLocale;
	}

	@Override
	public void send(DataOutputStream outToClient) throws IOException
	{
		outToClient.writeShort(MessageIDs.CREATE_LOCALE_RESPONSE);
		outToClient.writeBoolean(localeSuccessfullyCreated);
		if (localeSuccessfullyCreated)
		{
			outToClient.writeLong(createdLocale.getLocaleID());
		}
	}	
}
