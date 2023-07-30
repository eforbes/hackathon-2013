package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;

public class OutgoingVersionCheckRequest implements OutgoingMessage{
	private short versionNumber;
	public OutgoingVersionCheckRequest(short versionNumber){
		this.versionNumber = versionNumber;
	}

	@Override
	public void send(DataOutputStream outToServer) throws IOException {
		outToServer.writeShort(MessageIDs.VERSION_CHECK_REQUEST);
		outToServer.writeShort(versionNumber);
	}

}
