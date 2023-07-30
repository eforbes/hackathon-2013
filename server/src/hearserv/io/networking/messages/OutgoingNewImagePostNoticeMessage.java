package hearserv.io.networking.messages;

import hearserv.io.networking.MessageIDs;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Notifies the client of a new image post
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class OutgoingNewImagePostNoticeMessage implements OutgoingMessage //TODO: implement image posts
{
	private OutgoingNewImagePostNoticeMessage(){}; // prevent instance of the class from being created
	
	@Override
	public void send(DataOutputStream outToClient) throws IOException
	{
		//TODO: implement image posts
		outToClient.writeShort(MessageIDs.NEW_IMAGE_POST_NOTICE);
	}	
}
