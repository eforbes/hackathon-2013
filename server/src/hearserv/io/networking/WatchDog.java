package hearserv.io.networking;

import hearserv.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * Kills a thread if it doesn't feed the watchdog for too long
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class WatchDog extends Thread
{
	private Thread thread;
	private Socket socket;
	private long timeout;
	private volatile long lastFed;
	private boolean alive;
	
	/**
	 * Constructor without socket
	 * @param thread Thread to interrupt
	 * @param timeout time to wait without being fed before killing things
	 */
	public WatchDog(Thread thread, long timeout)
	{
		this(thread, null, timeout);
	}
	
	/**
	 * Full constructor
	 * @param thread Thread to interrupt
	 * @param socket Socket to close
	 * @param timeout time to wait without being fed before killing things
	 */
	public WatchDog(Thread thread, Socket socket, long timeout)
	{
		setTimeout(timeout);
		this.socket = socket;
		this.thread = thread;
		alive = true;
	}
	
	@Override
	public void run()
	{
		// initialize
		lastFed = System.currentTimeMillis();
		
		while (thread.isAlive() && alive)
		{
			try
			{
				sleep(timeout);
			}
			catch (InterruptedException e)
			{
				Logger.log(e, Logger.ERROR);
			}
			
			if (System.currentTimeMillis() > lastFed + timeout && alive)
			{				
				thread.interrupt();
				if (socket != null && !socket.isClosed())
				{
					try
					{
						Logger.log(
								"Watchdog timeout, disconnecting " + socket.getInetAddress() + ":" + socket.getPort(),
								Logger.WARNING);
						socket.close();
					}
					catch (IOException e)
					{
						Logger.log(e, Logger.ERROR);
					}
				}
				break;
			}
		}
	}
	
	/**
	 * Keeps the watchdog happy for a while longer
	 */
	public void feed()
	{
		lastFed = System.currentTimeMillis();
	}
	
	/**
	 * Changes the watchdog's timeout
	 * @param timeout the new timeout
	 */
	public void setTimeout(long timeout)
	{
		this.timeout = timeout;
	}
	
	/**
	 * Kill the watchdog
	 */
	public void kill()
	{
		alive = false;
	}
}
