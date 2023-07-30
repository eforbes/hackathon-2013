package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;


public class OutgoingLeaveLocaleRequest implements OutgoingMessage{
	private long localeID;
	public OutgoingLeaveLocaleRequest(long localeID){
		this.localeID = localeID;
	}

	@Override
	public void send(DataOutputStream outToServer) throws IOException {
		outToServer.writeShort(MessageIDs.LEAVE_LOCALE_REQUEST);
		outToServer.writeLong(localeID);
	}
}
