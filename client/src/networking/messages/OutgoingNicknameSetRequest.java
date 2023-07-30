package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;


public class OutgoingNicknameSetRequest implements OutgoingMessage{
	private String nickName;
	public OutgoingNicknameSetRequest(String nickName){
		this.nickName = nickName;
	}

	@Override
	public void send(DataOutputStream outToServer) throws IOException {
		outToServer.writeShort(MessageIDs.NICKNAME_SET_REQUEST);
		outToServer.writeShort(nickName.length());
		outToServer.writeChars(nickName);
		
	}

}
