package networking.messages;

import java.io.DataOutputStream;
import java.io.IOException;

import networking.MessageIDs;


public class OutgoingNearbyLocaleRequest implements OutgoingMessage{
	
	private double latitude;
	private double longitude;
	
	public OutgoingNearbyLocaleRequest(double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public void send(DataOutputStream outToServer) throws IOException { 
		outToServer.writeShort(MessageIDs.NEARBY_LOCALE_REQUEST);
		outToServer.writeDouble(latitude);
		outToServer.writeDouble(longitude);
	}

}
