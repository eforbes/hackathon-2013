package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;


public class OutgoingSendTextPostRequest implements OutgoingMessage{
	private long localeID;
	private String message;
	public OutgoingSendTextPostRequest(long localeID, String message){
		this.localeID = localeID;
		this.message = message;
	}

	public void send(DataOutputStream outToServer) throws IOException {
		outToServer.writeShort(MessageIDs.SEND_TEXT_POST_REQUEST);
		outToServer.writeLong(localeID);
		outToServer.writeShort(message.length());
		outToServer.writeChars(message);
	}
}