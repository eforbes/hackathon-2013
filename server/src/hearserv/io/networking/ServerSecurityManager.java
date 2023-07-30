package hearserv.io.networking;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Permission;
import java.util.ArrayList;

/**
 * Manages the security stuff. Level 7 amber access required to read further.
 * You didn't listen... stahp
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class ServerSecurityManager extends SecurityManager
{
	ArrayList<InetAddress> denied;
	
	
	/**
	 * Default constructor from superclass
	 */
	public ServerSecurityManager()
	{
//		super();
		
		denied = new ArrayList<InetAddress>();
		
//		try
//		{
//			denied.add(InetAddress.getByName("129.244.196.167"));
//		}
//		catch (UnknownHostException e)
//		{
//			e.printStackTrace();
//		}
		
	}
	
	@Override
	public void checkAccept(String host, int port)
	{
//		super.checkAccept(host, port);
		
		try
		{
			InetAddress testing = InetAddress.getByName(host);
			for (InetAddress addr : denied)
			{
				if (addr.equals(testing))
				{
					System.out.printf("Denied connection from %s:%d\n", host, port);
					throw new SecurityException("Denied IP Address");
				}
			}
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		
//		System.out.printf("Allowed connection from %s:%d\n", host, port);
	}
	
	
	
	@Override
	public void checkConnect(String host, int port)
	{
//		super.checkConnect(host, port);
		
//		System.out.printf("Allowed connection to %s:%d\n", host, port);
	}

	@Override
	public void checkAccess(Thread t)
	{
//		super.checkAccess(t);
	}

	@Override
	public void checkAccess(ThreadGroup g)
	{
//		super.checkAccess(g);
	}

	@Override
	public void checkAwtEventQueueAccess()
	{
//		super.checkAwtEventQueueAccess();
	}

	@Override
	public void checkConnect(String host, int port, Object context)
	{
//		super.checkConnect(host, port, context);
	}

	@Override
	public void checkCreateClassLoader()
	{
//		super.checkCreateClassLoader();
	}

	@Override
	public void checkDelete(String file)
	{
//		super.checkDelete(file);
	}

	@Override
	public void checkExec(String cmd)
	{
//		super.checkExec(cmd);
	}

	@Override
	public void checkExit(int status)
	{
//		super.checkExit(status);
	}

	@Override
	public void checkLink(String lib)
	{
//		super.checkLink(lib);
	}

	@Override
	public void checkListen(int port)
	{
//		super.checkListen(port);
	}

	@Override
	public void checkMemberAccess(Class<?> clazz, int which)
	{
//		super.checkMemberAccess(clazz, which);
	}

	@Override
	public void checkMulticast(InetAddress maddr, byte ttl)
	{
//		super.checkMulticast(maddr, ttl);
	}

	@Override
	public void checkMulticast(InetAddress maddr)
	{
//		super.checkMulticast(maddr);
	}

	@Override
	public void checkPackageAccess(String pkg)
	{
//		super.checkPackageAccess(pkg);
	}

	@Override
	public void checkPackageDefinition(String pkg)
	{
//		super.checkPackageDefinition(pkg);
	}

	@Override
	public void checkPermission(Permission perm, Object context)
	{
//		super.checkPermission(perm, context);
	}

	@Override
	public void checkPermission(Permission perm)
	{
//		super.checkPermission(perm);
	}

	@Override
	public void checkPrintJobAccess()
	{
//		super.checkPrintJobAccess();
	}

	@Override
	public void checkPropertiesAccess()
	{
//		super.checkPropertiesAccess();
	}

	@Override
	public void checkPropertyAccess(String key)
	{
//		super.checkPropertyAccess(key);
	}

	@Override
	public void checkRead(FileDescriptor fd)
	{
//		super.checkRead(fd);
	}

	@Override
	public void checkRead(String file, Object context)
	{
//		super.checkRead(file, context);
	}

	@Override
	public void checkRead(String file)
	{
//		super.checkRead(file);
	}

	@Override
	public void checkSecurityAccess(String target)
	{
//		super.checkSecurityAccess(target);
	}

	@Override
	public void checkSetFactory()
	{
//		super.checkSetFactory();
	}

	@Override
	public void checkSystemClipboardAccess()
	{
//		super.checkSystemClipboardAccess();
	}

	@Override
	public boolean checkTopLevelWindow(Object window)
	{
//		return super.checkTopLevelWindow(window);
		return true;
	}

	@Override
	public void checkWrite(FileDescriptor fd)
	{
//		super.checkWrite(fd);
	}

	@Override
	public void checkWrite(String file)
	{
//		super.checkWrite(file);
	}	
	
	
}
