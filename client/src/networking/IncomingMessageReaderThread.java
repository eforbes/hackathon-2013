package networking;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import networking.messages.Util;

import android.util.Log;

import com.shibedev.heresay.Locale;
import com.shibedev.heresay.Message;
import com.shibedev.heresay.MessagingService;

public class IncomingMessageReaderThread extends Thread{

	private ArrayList<Locale> localesList;
	private ServerConnection serverConnection;
	private MessagingService messagingService;

	private DataInputStream inFromServer;

	public IncomingMessageReaderThread(ServerConnection serverConnection, MessagingService messagingService) throws IOException{
		this.serverConnection = serverConnection;
		this.messagingService = messagingService;
		inFromServer = new DataInputStream(serverConnection.getSocket().getInputStream());
	}
	public void run(){

		try
		{				
			while (!serverConnection.getSocket().isClosed())
			{				
				//FIXME: This gives EOF exception in some cases, causing app to stay in a loading screen
				short messageType = inFromServer.readShort();
				//parse message received from server based on first short that it sends

				switch (messageType) {				
				// the servers response, the client should NOT respond here
				case MessageIDs.NEARBY_LOCALE_RESPONSE: handleNearbyLocaleResponse(); break;				
				case MessageIDs.JOIN_LOCALE_RESPONSE: handleJoinLocaleResponse(); break;				
				case MessageIDs.LEAVE_LOCALE_RESPONSE: handleLeaveLocaleResponse();	break;					
				case MessageIDs.NEW_TEXT_POST_NOTICE: handleNewTextPostNotice(); break;				
				//case MESSAGE_ID_NEW_IMAGE_POST_NOTICE: break;
				case MessageIDs.CREATE_LOCALE_RESPONSE: handleCreateLocaleResponse(); break;				
				case MessageIDs.NICKNAME_SET_RESPONSE: handleNicknameSetResponse(); break;
				case MessageIDs.VERSION_CHECK_RESPONSE: handleVersionCheckResponse(); break;
				case MessageIDs.ERROR_NOTICE: handleErrorNotice(); break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		

		serverConnection.getOutgoingMessageSenderThread().interrupt(); // dead

		if (!serverConnection.isPermanentlyClosed())
		{	// then we should reconnect
			Log.i("shibe", "reconnecting");
			
			serverConnection.getMessagingService().startNetworking();
		}
	}	// end run() method


	private void handleNearbyLocaleResponse() throws IOException {
		localesList = new ArrayList<Locale>();
		short numberOfLocales= inFromServer.readShort();

		for(int i = 0; i < numberOfLocales; i++){
			long id = inFromServer.readLong();
			byte flags = inFromServer.readByte();
			short nameLength = inFromServer.readShort();
			char[] name = new char[nameLength];
			for (int j = 0; j < name.length; j++) {
				name[j] = inFromServer.readChar();
			}
			short numPeople = inFromServer.readShort();

			String localeName = new String(name);
			boolean isPrivate = networking.messages.Util.getBitFromLabel(flags, Util.LOCALE_IS_PRIVATE);
			localesList.add(new Locale(id, localeName, isPrivate, numPeople)); 

		}
		messagingService.broadcastLocalesAvailable(localesList);

	}

	private void handleJoinLocaleResponse() throws IOException {
		long localeId = inFromServer.readLong();
		boolean successfulJoin = inFromServer.readBoolean();
		if(successfulJoin){
			readStringFromSocket(inFromServer);
			Locale l = lookupLocale(localeId);

			if(l==null)
				l = messagingService.createdLocale;
			
			messagingService.broadcastConnectedToLocale(successfulJoin, l);
		}	else {
			messagingService.broadcastConnectedToLocale(successfulJoin, null);
		}
	}

	private void handleNewTextPostNotice() throws IOException{
		@SuppressWarnings("unused")
		long localeId = inFromServer.readLong(); //not used in single locale version

		short nickNameLength = inFromServer.readShort();
		char[] nickName = new char[nickNameLength];
		for (int i = 0; i < nickName.length; i++) {
			nickName[i] = inFromServer.readChar();
		}

		short length = inFromServer.readShort();
		char[] post = new char[length];
		for (int i = 0; i < post.length; i++) {
			post[i] = inFromServer.readChar();
		}
		messagingService.broadcastMessageReceived(new Message(new String(nickName), new String(post)));		
	}

	private void handleCreateLocaleResponse() throws IOException{
		boolean createLocaleWorked = inFromServer.readBoolean();
		if(createLocaleWorked){
			messagingService.createdLocale.setId(inFromServer.readLong());
		}
		messagingService.broadcastLocaleCreated(createLocaleWorked);
	}

	private void handleNicknameSetResponse() throws IOException{
		boolean gotNickname = inFromServer.readBoolean();
		messagingService.broadcastUsernameRegistered(gotNickname);
	}

	private void handleLeaveLocaleResponse() throws IOException{
		@SuppressWarnings("unused")
		long localeId = inFromServer.readLong(); //not used in single locale version
		messagingService.broadcastLeaveLocale();		
	}
	
	private void handleVersionCheckResponse() throws IOException{
		short serverVersionNumber = inFromServer.readShort();
		if(serverVersionNumber != MessageIDs.PROTOCOL_VERSION){
			messagingService.broadcastNetworkProtocolVersionMismatch();
		}
	}
	
	private void handleErrorNotice() throws IOException{
		short errorCode = inFromServer.readShort();
		messagingService.broadcastNetworkError(MessageIDs.getErrorMessage(errorCode));
	}

	private Locale lookupLocale(long id){
		for(Locale l : localesList){
			if(l.getId() == id)
				return l;
		}
		return null; 
	}

	/**
	 * Reads a short-prefixed string from the socket
	 * @return the string
	 * @throws IOException
	 */
	private static String readStringFromSocket(DataInputStream inFromServer) throws IOException
	{
		short length = inFromServer.readShort();
		if (length > 0)
		{
			char[] buffer = new char[length];
			for (short i = 0; i < length; i++)
			{
				buffer[i] = inFromServer.readChar();
			}
			return new String(buffer);
		}
		else // zero-length string
		{
			return "";
		}

	}
}
