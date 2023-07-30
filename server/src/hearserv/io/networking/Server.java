package hearserv.io.networking;

import hearserv.Logger;

import java.io.IOException;
import java.net.*;

/**
 * The actual listening server component of the application
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class Server
{	
	public final static int SERVER_PORT = 65535;
	
	private final static long RETRY_DELAY = 500;
	private final static long MAX_RETRIES = 10;
	
//	private final static int BACKLOG = 50;
	
	private ServerSocket mainSocket;
	private Thread connectionAcceptingThread;
	
	/**
	 * Constructs a new Server and attempts to bind a listening port
	 * @throws IOException if the port bind fails
	 */
	public Server() throws IOException
	{
		// create socket
		
		int retries = 0;
		boolean failure = true;
		
		IOException exception = null;
		while (failure && retries <= MAX_RETRIES)
		{
			try
			{
				// Can throw an IOException
				mainSocket = new ServerSocket(SERVER_PORT);
//				mainSocket = new ServerSocket(SERVER_PORT, BACKLOG);
				
				Logger.log(
						"Server listening on " + mainSocket.getInetAddress().toString() + ":"
								+ mainSocket.getLocalPort(), Logger.INFO);

				connectionAcceptingThread = new Thread(connectionAccepter, "Listening Socket");
				failure = false;
			}
			catch (IOException e)
			{
				exception = e;
				Logger.log(e, Logger.ERROR);
				
				try
				{
					Thread.sleep(RETRY_DELAY);
				}
				catch (InterruptedException e1)
				{
					Logger.log(e1, Logger.ERROR);
				}
			}
		}
		
		if (failure)
		{
			throw exception;
		}
	}
	
	/**
	 * Begin accepting connections
	 */
	public void start()
	{
		connectionAcceptingThread.start();
	}
	
	/**
	 * Close the listening socket
	 */
	public void close()
	{
		try
		{
			mainSocket.close();
		}
		catch (IOException e)
		{
			Logger.log(e, Logger.ERROR);
		}
	}
	
	/**
	 * Repeatedly accepts connections
	 */
	private final Runnable connectionAccepter = new Runnable()
	{
		@Override
		public void run()
		{
			while (!mainSocket.isClosed())
			{
				try
				{
					Socket incomingSocket = mainSocket.accept();
					
					ClientConnection clientConnection = new ClientConnection(incomingSocket);
					
					clientConnection.start();
					
				}
				catch (IOException e)
				{
					// TODO: handle various exceptions?
					Logger.log(e, Logger.WARNING);
				}
				catch (SecurityException e)
				{
					Logger.log(e, Logger.WARNING);
				}
			}
		}
	};
}
