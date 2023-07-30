package test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import hearserv.util.Util;

public class Test
{	
	public static void main(String[] args)
	{
		bitTest();
		
		// THIS IS A TEST CHANGE TO SEE IF I CAN MERGE WITH TRUNK
		
	}
	
	public static void bitTest()
	{
		int a = 0B10101010;
		int flag = 0B00000001;;
		for (int i = 0; i < 8; i++)
		{
//			System.out.println(Util.getBitFromPosition(a, i));
			System.out.println(Util.getBitFromLabel(a, flag));
			flag = flag << 1;
		}
	}
	
	public static void socketTest()
	{
		Socket socket = new Socket();
		try
		{
			socket.connect(new InetSocketAddress("129.244.99.134", 65535), 1000);
			OutputStream outputStream = socket.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			
			dataOutputStream.writeShort(14); //set nick
			String nick = "Gordon Freeman";
			dataOutputStream.writeShort(nick.length());
			dataOutputStream.writeChars(nick);
			dataOutputStream.flush();
			outputStream.flush();
			
			Thread.sleep(7000);
			
			dataOutputStream.writeShort(0); // get nearby locales
			dataOutputStream.writeDouble(50);
			dataOutputStream.writeDouble(50);
			
			Thread.sleep(7000);
			
			dataOutputStream.close();
			outputStream.close();
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
