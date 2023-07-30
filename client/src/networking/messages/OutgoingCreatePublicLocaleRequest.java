package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;


public class OutgoingCreatePublicLocaleRequest implements OutgoingMessage{
	private String localeName;
	private double latitude;
	private double longitude;
	private int radius;	
	
	public OutgoingCreatePublicLocaleRequest(String localeName, double latitude, double longitude, int radius){
		this.localeName = localeName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;		
	}

	@Override
	public void send(DataOutputStream outToServer) throws IOException {
		outToServer.writeShort(MessageIDs.CREATE_LOCALE_REQUEST);
		outToServer.write(Util.getByte(false)); //locale is not private
		outToServer.writeShort(localeName.length());
		outToServer.writeChars(localeName);
		outToServer.writeDouble(latitude);
		outToServer.writeDouble(longitude);
		outToServer.writeInt(radius);
	}
}
