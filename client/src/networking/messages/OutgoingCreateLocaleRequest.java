package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;



public class OutgoingCreateLocaleRequest implements OutgoingMessage{
	private boolean isPrivate;
	private String localeName;
	private double latitude;
	private double longitude;
	private int radius;
	private int key;
	
	public OutgoingCreateLocaleRequest(boolean isPrivate, String localeName, double latitude, double longitude, int radius, int key){
		this.isPrivate = isPrivate;
		this.localeName = localeName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.key = key;
	}
	
	//TODO why is isPrivate sent if one does not have a key
	public OutgoingCreateLocaleRequest(boolean isPrivate, String localeName, double latitude, double longitude, int radius){
		this.isPrivate = isPrivate;
		this.localeName = localeName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;		
	}

	@Override
	public void send(DataOutputStream outToServer) throws IOException {
		outToServer.writeShort(MessageIDs.CREATE_LOCALE_REQUEST);
		outToServer.write(Util.getByte(isPrivate));
		outToServer.writeShort(localeName.length());
		outToServer.writeChars(localeName);
		outToServer.writeDouble(latitude);
		outToServer.writeDouble(longitude);
		outToServer.writeInt(radius);
		if(isPrivate){
			outToServer.writeInt(key);
		}
	}
}
