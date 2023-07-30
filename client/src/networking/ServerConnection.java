package networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import networking.messages.OutgoingMessage;
import networking.messages.OutgoingNicknameSetRequest;
import networking.messages.OutgoingVersionCheckRequest;

import com.shibedev.heresay.MessagingService;

public class ServerConnection 
{
	/** the socket that connects us to the user */
	private Socket socket;
	
	/** The queue of outgoing messages to send */
	private Queue<OutgoingMessage> outgoingMessageQueue;
	
	/** Reads incoming messages */
	private IncomingMessageReaderThread incomingMessageReaderThread;
	
	/** Sends outgoing messages, cannot be a vanilla thread because it must be notified */
	private OutgoingMessageSenderThread outgoingMessageSenderThread;
	
	/** After this is true, the server should no longer auto-reconnect */
	private boolean permanentlyClosed = false;
	
	private MessagingService messagingService;
	
	/**
	 * Construct a clientConnection. CONSTRUCTING THIS WiLL START NETWORKING,
	 * SO MAKE SURE YOU KNOW YOUR NICKNAME ALLREADY!
	 * @param messagingService I, Michael, have no idea what in the blazes this is for
	 */
	public ServerConnection(MessagingService messagingService, String username) throws IOException
	{
		super();
		this.messagingService = messagingService;
		this.socket = new Socket();
		
		outgoingMessageQueue = new ConcurrentLinkedQueue<OutgoingMessage>();
		socket.connect(new InetSocketAddress("zcraft.no-ip.org", 65535), 1000);

		incomingMessageReaderThread = new IncomingMessageReaderThread(this, messagingService);
		outgoingMessageSenderThread = new OutgoingMessageSenderThread(this);

		startProcessingConnection(username);
	}
	
	/**
	 * Called when the connection should permanently stop
	 */
	public void close()
	{
		//TODO MICHAEL FINISH THIS
		
		 permanentlyClosed = true;
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isPermanentlyClosed() {
		return permanentlyClosed;
	}

	/**
	 * Adds an outgoing message to the outgoing message queue. It will be sent
	 * as soon as possible.
	 * @param outgoingMessage The message to enqueue
	 */
	public void sendMessage(OutgoingMessage outgoingMessage)
	{
		outgoingMessageQueue.add(outgoingMessage);
		
		if (outgoingMessageSenderThread.isWaiting())
		{
			synchronized(outgoingMessageSenderThread){
				outgoingMessageSenderThread.notify();
			}
		}
	}
	
	/**
	 * Starts reading incoming messages from the user and starts sending the queue of outgoing messages
	 */
	private void startProcessingConnection(String username)
	{
		incomingMessageReaderThread.start();
		
		outgoingMessageSenderThread.start();
		sendMessage(new OutgoingVersionCheckRequest(MessageIDs.PROTOCOL_VERSION));
		sendMessage(new OutgoingNicknameSetRequest(username));
	}
	
	/**
	 * Get the incoming connection socket from the client
	 * @return a socket to the client
	 */
	Socket getSocket()
	{
		return socket;
	}
	
	/**
	 * Get the queue of outgoing messages
	 * @return the queue of outgoing messages
	 */
	Queue<OutgoingMessage> getOutgoingMessageQueue()
	{
		return outgoingMessageQueue;
	}

	public IncomingMessageReaderThread getIncomingMessageReaderThread() {
		return incomingMessageReaderThread;
	}

	public OutgoingMessageSenderThread getOutgoingMessageSenderThread() {
		return outgoingMessageSenderThread;
	}

	public MessagingService getMessagingService() {
		return messagingService;
	}
}