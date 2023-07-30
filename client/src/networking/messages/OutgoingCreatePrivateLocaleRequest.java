package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;



public class OutgoingCreatePrivateLocaleRequest implements OutgoingMessage{
	private String localeName;
	private double latitude;
	private double longitude;
	private int radius;
	private int key;
	
	public OutgoingCreatePrivateLocaleRequest(String localeName, double latitude, double longitude, int radius, int key){
		this.localeName = localeName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.key = key;
	}

	@Override
	public void send(DataOutputStream outToServer) throws IOException {
		outToServer.writeShort(MessageIDs.CREATE_LOCALE_REQUEST);
		outToServer.write(Util.getByte(true)); //locale is private
		outToServer.writeShort(localeName.length());
		outToServer.writeChars(localeName);
		outToServer.writeDouble(latitude);
		outToServer.writeDouble(longitude);
		outToServer.writeInt(radius);
		outToServer.writeInt(key);
	}
}
