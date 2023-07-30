package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;


public class OutgoingJoinPrivateLocaleRequest implements OutgoingMessage{
	private long localeID;
	private int key;
	public OutgoingJoinPrivateLocaleRequest(long localeID, int key){
		this.localeID = localeID;
		this.key = key;
	}

	@Override
	public void send(DataOutputStream outToServer) throws IOException {	
		outToServer.writeShort(MessageIDs.JOIN_PRIVATE_LOCALE_REQUEST);
		outToServer.writeLong(localeID);
		outToServer.writeInt(key);
	}

}
