package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;

public class OutgoingKeepAliveNotice implements OutgoingMessage{
	
	public OutgoingKeepAliveNotice(){
		
	}

	@Override
	public void send(DataOutputStream outToServer) throws IOException {
		outToServer.writeShort(MessageIDs.KEEPALIVE_NOTICE);
	}

}
