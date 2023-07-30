package hearserv.io.networking.messages;
import java.io.DataOutputStream;
import java.io.IOException;

import hearserv.io.networking.MessageIDs;

/**
 * Response to the client's request for a certain nickname
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class OutgoingNicknameSetResponseMessage implements OutgoingMessage
{
	private boolean nicknameAccepted;
	
	/**
	 * Response to the client's request for a certain nickname
	 * @param nicknameAccepted true if we accepted their choice, false otherwise
	 */
	public OutgoingNicknameSetResponseMessage(boolean nicknameAccepted)
	{
		this.nicknameAccepted = nicknameAccepted;
	}
	
	@Override
	public void send(DataOutputStream outToClient) throws IOException
	{
		outToClient.writeShort(MessageIDs.NICKNAME_SET_RESPONSE);
		outToClient.writeBoolean(nicknameAccepted);
	}
}
