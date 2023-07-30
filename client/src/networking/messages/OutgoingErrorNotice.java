package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;

public class OutgoingErrorNotice implements OutgoingMessage {
	private short errorCode;
	
	public OutgoingErrorNotice(short errorCode){
		this.errorCode = errorCode;
	}

	@Override
	public void send(DataOutputStream outToServer) throws IOException {
		outToServer.writeShort(MessageIDs.ERROR_NOTICE);
		outToServer.writeShort(errorCode);		
	}

}
