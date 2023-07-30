package hearserv.io.networking;

import hearserv.Logger;
import hearserv.app_elements.User;
import hearserv.io.networking.messages.OutgoingMessage;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handles a connection to a specific client
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class ClientConnection
{
	private final static long CLIENT_TIMEOUT = 4 * 60 *1000;
	
	/** the socket that connects us to the user */
	private Socket incomingConnectionSocket;
	
	/** initially false, set to true once the user authenticates */
	private volatile boolean userCreated;
	
	/** The queue of outgoing messages to send */
	private Queue<OutgoingMessage> outgoingMessageQueue;
	
	/** Reads incoming messages */
	private Thread incomingMessageReaderThread;
	
	/** Sends outgoing messages, cannot be a vanilla thread because it must be notified */
	private OutgoingMessageSenderThread outgoingMessageSenderThread;
	
	/** the user this client connection is associated with */
	private User user;
	
	/** Watches this the incomingMessageReaderThread and kills the socket if it isn't fed in too long */
	WatchDog watchDog;
	
	/**
	 * Construct a clientConnection
	 * @param incomingConnectionSocket The socket for this client
	 */
	public ClientConnection(Socket incomingConnectionSocket)
	{
		super();
		
		Logger.log("Client connected from " + incomingConnectionSocket.getInetAddress() + ":"
				+ incomingConnectionSocket.getPort(), Logger.INFO);

		userCreated = false;
		this.incomingConnectionSocket = incomingConnectionSocket;
		outgoingMessageQueue = new ConcurrentLinkedQueue<OutgoingMessage>();
		watchDog = new WatchDog(getIncomingMessageReaderThread(),
				getIncomingConnectionSocket(), CLIENT_TIMEOUT);
		
		try
		{	
			incomingMessageReaderThread = new IncomingMessageReaderThread(this);
			outgoingMessageSenderThread = new OutgoingMessageSenderThread(this);
		}
		catch (IOException e)
		{
			Logger.log(e, Logger.ERROR);
		}
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
			synchronized(outgoingMessageSenderThread)
			{
				outgoingMessageSenderThread.notify();
			}
		}
	}
	
	
	/**
	 * Start the ClientConnection processing network data
	 */
	public void start()
	{
		startIncomingMessageReader();
		startOutgoingMessageSender();
		watchDog.start();
	}
	
	/**
	 * Starts reading incoming messages from the user.
	 * Once the client has started authentication we will start sending outgoing messages.
	 * @param watchDog 
	 */
	private void startIncomingMessageReader()
	{
		incomingMessageReaderThread.start();
	}
	
	/**
	 * Starts sending the queue of outgoing messages
	 */
	private void startOutgoingMessageSender()
	{
		outgoingMessageSenderThread.start();
	}
	
	/**
	 * Checks if the user has been authenticated
	 * @return userCreated true if the user has been authenticated
	 */
	boolean isUserCreated()
	{
		return userCreated;
	}

	/**
	 * Set this user as having been created
	 */
	void setUserCreated()
	{
		this.userCreated = true;
	}
	
	/**
	 * Get the incoming connection socket from the client
	 * @return a socket to the client
	 */
	public Socket getIncomingConnectionSocket()
	{
		return incomingConnectionSocket;
	}
	
	/**
	 * Get the queue of outgoing messages
	 * @return the queue of outgoing messages
	 */
	Queue<OutgoingMessage> getOutgoingMessageQueue()
	{
		return outgoingMessageQueue;
	}
	
	/**
	 * Get the OutgoingMessageSenderThread
	 * @return the OutgoingMessageSenderThread
	 */
	OutgoingMessageSenderThread getOutgoingMessageSenderThread()
	{
		return outgoingMessageSenderThread;
	}

	/**
	 * Get the user associated with this client connection
	 * @return the user associated with this client connection
	 */
	public User getUser()
	{
		return user;
	}
	
	/**
	 * set the user associated with this client connection
	 * @param user the new user object to associate with this client connection
	 */
	void setUser(User user)
	{
		this.user = user;
	}

	/**
	 * Get the IncomingMessageReaderThread for use by the watchdog
	 * @return the IncomingMessageReaderThread
	 */
	Thread getIncomingMessageReaderThread()
	{
		return incomingMessageReaderThread;
	}

	/**
	 * Get the watchdog
	 * @return the watchdog
	 */
	WatchDog getWatchDog()
	{
		return watchDog;
	}
}
