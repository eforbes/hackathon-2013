package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;


public class OutgoingJoinPublicLocaleRequest implements OutgoingMessage {
	private long localeID;
	public OutgoingJoinPublicLocaleRequest(long localeID){
		this.localeID = localeID;
	}

	@Override
	public void send(DataOutputStream outToServer) throws IOException { 
		outToServer.writeShort(MessageIDs.JOIN_PUBLIC_LOCALE_REQUEST);
		outToServer.writeLong(localeID);
	}
}
