package hearserv.io.networking.messages;

import hearserv.app_elements.Post;
import hearserv.io.networking.MessageIDs;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Notifies the client of a new text post
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class OutgoingNewTextPostNoticeMessage implements OutgoingMessage
{
	private Post post;
	
	/**
	 * Notifies the client of a new text post
	 * @param post the post the send the user
	 */
	public OutgoingNewTextPostNoticeMessage(Post post)
	{
		this.post = post;
	}

	@Override
	public void send(DataOutputStream outToClient) throws IOException
	{
		outToClient.writeShort(MessageIDs.NEW_TEXT_POST_NOTICE);
		outToClient.writeLong(post.getDestinationLocale().getLocaleID());
		outToClient.writeShort(post.getSourceUser().getName().length());
		outToClient.writeChars(post.getSourceUser().getName());
		outToClient.writeShort(post.getMessage().length());
		outToClient.writeChars(post.getMessage());
	}	
}
