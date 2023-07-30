package hearserv.io.networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Contains an outgoing message of some variety the be sent to the client after it is dequeued
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public interface OutgoingMessage
{	
	/**
	 * Send the message to the client
	 * @param outToClient the DataOutputStream the sends to the client
	 * @throws IOException if the outputStream could not be written to
	 */
	public void send(DataOutputStream outToClient) throws IOException;
}
