package networking;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;

import networking.messages.OutgoingMessage;

public class OutgoingMessageSenderThread extends Thread
{	
	private volatile boolean waiting = false;
	
	private ServerConnection clientConnection; //WILL unused?
	private Socket incomingConnectionSocket;
	private DataOutputStream dataOutputStream;
	private Queue<OutgoingMessage> outgoingMessageQueue;
	
	private OutputStream outputStream;
	
	/**
	 * Standard constructor
	 * @param clientConnection required to gain access to the user object
	 * @throws IOException 
	 */
	public OutgoingMessageSenderThread(ServerConnection clientConnection) throws IOException
	{
		this.clientConnection = clientConnection;
		incomingConnectionSocket = clientConnection.getSocket();
		outputStream = clientConnection.getSocket().getOutputStream();
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
					synchronized(this){
						this.wait();
					}
				}
				catch (InterruptedException e)
				{
					//noop
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
					e.printStackTrace();
				}
			}
		}
	}
}