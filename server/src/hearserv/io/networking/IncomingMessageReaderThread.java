package hearserv.io.networking;

import hearserv.Logger;
import hearserv.TerminalDriver;
import hearserv.app_elements.Locale;
import hearserv.app_elements.LocaleNotFoundException;
import hearserv.app_elements.Post;
import hearserv.app_elements.User;
import hearserv.io.networking.messages.OutgoingCreateLocaleResponseMessage;
import hearserv.io.networking.messages.OutgoingJoinLocaleResponseMessage;
import hearserv.io.networking.messages.OutgoingLeaveLocaleResponseMessage;
import hearserv.io.networking.messages.OutgoingNearbyLocaleResponseMessage;
import hearserv.io.networking.messages.OutgoingNicknameSetResponseMessage;
import hearserv.util.Util;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
/**
 * Reads incoming messages from the client
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class IncomingMessageReaderThread extends Thread
{	
	private static final int NO_KEY = 0;
	
	private ClientConnection clientConnection;
	private Socket incomingConnectionSocket;
	private DataInputStream dataInputStream;
	
	/** Watches this thread and kills the socket if it isn't fed in too long */
	WatchDog watchDog;
	
	/**
	 * Standard constructor
	 * @param clientConnection required to gain access to the user object
	 * @throws IOException 
	 */
	public IncomingMessageReaderThread(ClientConnection clientConnection) throws IOException
	{
		super("Client Message Reader for " + clientConnection.getIncomingConnectionSocket() + ":"
				+ clientConnection.getIncomingConnectionSocket().getPort());

		this.clientConnection = clientConnection;
		incomingConnectionSocket = clientConnection.getIncomingConnectionSocket();
		
		// Throws IOException
		dataInputStream = new DataInputStream(clientConnection.getIncomingConnectionSocket().getInputStream());
	}
	
	@Override
	public void run()
	{
		watchDog = clientConnection.getWatchDog();
		try
		{
			parsingLoop: //TODO: remove
			while (!incomingConnectionSocket.isClosed())
			{
				short messageType = dataInputStream.readShort();
				
				if (clientConnection.isUserCreated())
				{
					switch (messageType)
					{
						case MessageIDs.NEARBY_LOCALE_REQUEST: 			handleNearbyLocaleRequest(); 		break;
						case MessageIDs.JOIN_PUBLIC_LOCALE_REQUEST: 	handleJoinPublicLocaleRequest(); 	break;
						case MessageIDs.JOIN_PRIVATE_LOCALE_REQUEST: 	handleJoinPrivateLocaleRequest(); 	break;
						case MessageIDs.LEAVE_LOCALE_REQUEST: 			leaveLocaleRequest(); 				break;
						case MessageIDs.SEND_TEXT_POST_REQUEST: 		handleSendTextPostRequest(); 		break;
						//case MessageIDs.SEND_IMAGE_POST_REQUEST: break;  // NOT YET IMPLEMENTED
						case MessageIDs.CREATE_LOCALE_REQUEST: 			handleCreateLocaleRequest(); 		break;
						case MessageIDs.NICKNAME_SET_REQUEST: 			handleNicknameSetRequest(); 		break;
						default:
						{	// handle unknown message types
							Logger.log("UNKNOWN MESSAGE, PROGRAM STATE CORRUPT", Logger.SEVERE);
							break parsingLoop;
						}
					}
				}
				else // user must authenticate
				{
					switch (messageType)
					{
						// disallowed messages
						case MessageIDs.NEARBY_LOCALE_REQUEST:
						case MessageIDs.JOIN_PUBLIC_LOCALE_REQUEST:
						case MessageIDs.JOIN_PRIVATE_LOCALE_REQUEST:
						case MessageIDs.LEAVE_LOCALE_REQUEST:
						case MessageIDs.SEND_TEXT_POST_REQUEST:
						//case MessageIDs.SEND_IMAGE_POST_REQUEST: // NOT YET IMPLEMENTED
						case MessageIDs.CREATE_LOCALE_REQUEST:
						{	// handle all disallowed messages
							Logger.log("UNEXPECTED MESSAGE, TERMINATING CONNECTION", Logger.SEVERE);
							break parsingLoop;
						}
						
						// allowed messages 
						case MessageIDs.NICKNAME_SET_REQUEST: handleNicknameSetRequest(); break;
						
						default:
						{	// handle unknown message types
							Logger.log("UNKNOWN MESSAGE, TERMINATING CONNECTION", Logger.SEVERE);
							break parsingLoop;
						}
					}
				}
				

			}
		}
		catch (LocaleNotFoundException e)
		{
			Logger.log(e, Logger.SEVERE);
		}
		catch (EOFException e)
		{
			Logger.log("Invalid communication from client? " + incomingConnectionSocket.getInetAddress() + ":"
					+ incomingConnectionSocket.getPort(), Logger.WARNING);
//			Logger.log(e, Logger.ERROR);
		}
		catch (IOException e)
		{	// client disconnected?
//			Logger.log(e, Logger.ERROR);
		}
		
		// kill this client
		
		try
		{
			incomingConnectionSocket.close();
		}
		catch (IOException e)
		{
			Logger.log(e, Logger.ERROR);
		}
		clientConnection.getOutgoingMessageSenderThread().interrupt();
		Logger.log("Client disconnected: " + incomingConnectionSocket.getInetAddress() + ":"
				+ incomingConnectionSocket.getPort(), Logger.INFO);
		
		clientConnection.getUser().leaveAllLocales();
	}
	
	private void handleNearbyLocaleRequest() throws IOException
	{
		double latitude = dataInputStream.readDouble();
		double longitude = dataInputStream.readDouble();
		
		ArrayList<Locale> nearbyLocales = TerminalDriver.getLocaleManager().getNearbyLocales(latitude, longitude);
		
		Logger.log(incomingConnectionSocket.getInetAddress() + ":" + incomingConnectionSocket.getPort() + " at "
				+ latitude + ", " + longitude + " requested nearby locales, got " + nearbyLocales.size(), Logger.DEBUG);
		
		clientConnection.sendMessage(new OutgoingNearbyLocaleResponseMessage(nearbyLocales));
	}
	
	private void handleJoinPublicLocaleRequest() throws IOException, LocaleNotFoundException
	{
		long localeID = dataInputStream.readLong();
		
		Locale toJoin = TerminalDriver.getLocaleManager().lookupLocale(localeID);
		
		if (toJoin.isPrivate())
		{ // the user has tried to join a private locale as public
			Logger.log(incomingConnectionSocket.getInetAddress() + ":" + incomingConnectionSocket.getPort()
					+ " incorrectly tried to join private locale " + toJoin.getName()
					+ " as if it were public and was denied", Logger.DEBUG);
			
			clientConnection.sendMessage(new OutgoingJoinLocaleResponseMessage(toJoin, false));
		}
		else
		{ // all is well
			toJoin.addUser(clientConnection.getUser());
			
			Logger.log(incomingConnectionSocket.getInetAddress() + ":" + incomingConnectionSocket.getPort()
					+ " joined locale " + toJoin.getName(), Logger.DEBUG);
			
			clientConnection.sendMessage(new OutgoingJoinLocaleResponseMessage(toJoin, true));
		}
	}
	
	private void handleJoinPrivateLocaleRequest() throws IOException
	{
		long localeID = dataInputStream.readLong();
		int localeKey = dataInputStream.readInt();
		
		try
		{
			Locale toJoin = TerminalDriver.getLocaleManager().lookupLocale(localeID);
			boolean success = localeKey == toJoin.getKey();
			
			Logger.log(incomingConnectionSocket.getInetAddress() + ":" + incomingConnectionSocket.getPort() + " "
					+ (success ? "allowed" : "denied") + " entry to locale " + toJoin.getName(), Logger.DEBUG);
			
			if (!toJoin.isPrivate()) // if the locale is actually public and the client is WRONG
			{
				
				Logger.log(incomingConnectionSocket.getInetAddress() + ":" + incomingConnectionSocket.getPort()
						+ " incorrectly tried to join public locale " + toJoin.getName()
						+ " as if it were private and was allowed because I'm nice", Logger.DEBUG);
				
				// we allow them in anyways
				toJoin.addUser(clientConnection.getUser());
				clientConnection.sendMessage(new OutgoingJoinLocaleResponseMessage(toJoin, true));
			}
			else if (success) // if the key matched
			{
				toJoin.addUser(clientConnection.getUser());
				clientConnection.sendMessage(new OutgoingJoinLocaleResponseMessage(toJoin, true));
			}
			else
			// if the key did not match
			{
				clientConnection.sendMessage(new OutgoingJoinLocaleResponseMessage(toJoin, false));
			}
		}
		catch (LocaleNotFoundException e)
		{
			// the client has tried to join a locale that does not exist
			Logger.log(e, Logger.ERROR);
			clientConnection.sendMessage(new OutgoingJoinLocaleResponseMessage(localeID, false));
		}
	}
	
	private void leaveLocaleRequest() throws IOException, LocaleNotFoundException
	{
		long localeID = dataInputStream.readLong();
		
		Locale leaving = TerminalDriver.getLocaleManager().lookupLocale(localeID);
		
		Logger.log(incomingConnectionSocket.getInetAddress() + ":" + incomingConnectionSocket.getPort()
				+ " left locale " + leaving.getName(), Logger.DEBUG);
		
		leaving.removeUser(clientConnection.getUser());
		
		clientConnection.sendMessage(new OutgoingLeaveLocaleResponseMessage(leaving));
	}
	
	private void handleSendTextPostRequest() throws IOException, LocaleNotFoundException
	{
		long localeID = dataInputStream.readLong();
		String content = Util.readStringFromSocket(dataInputStream);
		
		Post post = new Post(content, clientConnection.getUser(), TerminalDriver.getLocaleManager().lookupLocale(
				localeID));
		
		Logger.log(incomingConnectionSocket.getInetAddress() + ":" + incomingConnectionSocket.getPort() + " ("
				+ post.getSourceUser().getName() + ") sent post \"" + post.getMessage() + "\" to locale "
				+ post.getDestinationLocale().getName(), Logger.DEBUG);
		
		post.send();

	}
	
	private void handleCreateLocaleRequest() throws IOException
	{
		byte flags = dataInputStream.readByte();
		String localeName = Util.readStringFromSocket(dataInputStream);
		double latitude = dataInputStream.readDouble();
		double longitude = dataInputStream.readDouble();
		int radius = dataInputStream.readInt();
		
		Locale newLocale = TerminalDriver.getLocaleManager().addLocale(latitude, longitude, localeName,
				Util.getBitFromLabel(flags, Util.LOCALE_IS_PRIVATE), NO_KEY, radius);
		
		Logger.log(incomingConnectionSocket.getInetAddress() + ":" + incomingConnectionSocket.getPort()
				+ " created locale " + newLocale.getName() + " with radius " + newLocale.getRadius()
				+ " and ID " + newLocale.getLocaleID(), Logger.DEBUG);
		
		clientConnection.sendMessage(
				new OutgoingCreateLocaleResponseMessage(newLocale != null, newLocale));
	}
	
	private void handleNicknameSetRequest() throws IOException
	{
		String nick = Util.readStringFromSocket(dataInputStream);
		
		User user = TerminalDriver.getUserManager().addUser(nick, clientConnection);
		clientConnection.setUser(user);
		clientConnection.setUserCreated();
		
		boolean accepted = Util.testNickname(nick);
		
		Logger.log(
				incomingConnectionSocket.getInetAddress() + ":" + incomingConnectionSocket.getPort()
						+ " (UID " + user.getUserID() + ") " + (accepted ? "gets" : "denied")
						+ " nick " + user.getName(), Logger.DEBUG);

		clientConnection.sendMessage(new OutgoingNicknameSetResponseMessage(accepted));
	}
}
