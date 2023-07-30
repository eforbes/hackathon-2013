package hearserv.io.networking;

import hearserv.Logger;
import hearserv.io.networking.messages.OutgoingMessage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;

/**
 * Sends outgoing messages, cannot be a vanilla thread because it must be notified
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class OutgoingMessageSenderThread extends Thread
{	
	private volatile boolean waiting = false;
	
	@SuppressWarnings("unused") //TODO: is this needed?
	private ClientConnection clientConnection;
	private Socket incomingConnectionSocket;
	private DataOutputStream dataOutputStream;
	private OutputStream outputStream;
	private Queue<OutgoingMessage> outgoingMessageQueue;
	
	/**
	 * Standard constructor
	 * @param clientConnection required to gain access to the user object
	 * @throws IOException 
	 */
	public OutgoingMessageSenderThread(ClientConnection clientConnection) throws IOException
	{
		super("Client Message Sender for " + clientConnection.getIncomingConnectionSocket() + ":"
				+ clientConnection.getIncomingConnectionSocket().getPort());
		
		this.clientConnection = clientConnection;
		incomingConnectionSocket = clientConnection.getIncomingConnectionSocket();
		
		outputStream = clientConnection.getIncomingConnectionSocket().getOutputStream(); // throws IOException
		dataOutputStream = new DataOutputStream(outputStream);
		
		outgoingMessageQueue = clientConnection.getOutgoingMessageQueue();
	}
	
	/**
	 * Check if we are waiting for something to be added to the queue
	 * @return true if we are waiting for something to be added to the queue
	 */
	public boolean isWaiting()
	{
		return waiting;
	}

	@Override
	public void run()
	{
		while (!incomingConnectionSocket.isClosed())
		{				
			if (outgoingMessageQueue.isEmpty())
			{	// must be notified to resume operation
				waiting = true;
				try
				{
					synchronized(this)
					{
						this.wait();
					}
				}
				catch (InterruptedException e)
				{
					// thrown when this thread is killed
				}
				waiting = false;
			}
			else // dequeue a message and send it
			{
				try
				{
					outgoingMessageQueue.poll().send(dataOutputStream);
					
					dataOutputStream.flush();
					outputStream.flush();
				}
				catch (IOException e)
				{
					Logger.log(e, Logger.WARNING);
				}
			}
		}
	}
}
