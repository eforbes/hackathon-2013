package hearserv.util;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Useful utilities used throughout the project
 * @author Michael Ripley (<a href="mailto:michael-ripley@utulsa.edu">michael-ripley@utulsa.edu</a>)
 */
public class Util
{	
	private static int[] indices = 
		{
			0B10000000,
			0B01000000,
			0B00100000,
			0B00010000,
			0B00001000,
			0B00000100,
			0B00000010,
			0B00000001
		};
	
	/** a label for getBitFromLabel()  */
	public static int LOCALE_IS_PRIVATE = indices[7];
	
	/**
	 * Get a boolean bit from a byte given a label
	 * @param aByte the byte
	 * @param label the label (for example Util.LOCALE_IS_PRIVATE)
	 * @return the boolean value of the bit
	 */
	public static boolean getBitFromLabel(int aByte, int label)
	{
		return (aByte & label) != 0;
	}
	
	/**
	 * Get the bit in a certain position of a byte [01234567]
	 * @param aByte the byte
	 * @param position the position in the byte [01234567]
	 * @return the boolean value of the bit
	 */
	public static boolean getBitFromPosition(int aByte, int position)
	{
		return (aByte & indices[position]) != 0;
	}
	
	/**
	 * Generate a flags byte out of up to 8 boolean flags
	 * @param isPrivate Is the locale private?
	 * @return the flags byte for the specified bit
	 */
	public static int getByte(boolean isPrivate)
	{
		return 0 + LOCALE_IS_PRIVATE; // if you add more flags remove the 0 + and replace it with other values
	}
	
	/**
	 * Reads a short-prefixed string from the socket
	 * @return the string
	 * @throws IOException
	 */
	public static String readStringFromSocket(DataInputStream dataInputStream) throws IOException
	{
		short length = dataInputStream.readShort();
		if (length > 0)
		{
			char[] buffer = new char[length];
			for (short i = 0; i < length; i++)
			{
				buffer[i] = dataInputStream.readChar();
			}
			return new String(buffer);
		}
		else // zero-length string
		{
			return "";
		}
	}
	
	@SuppressWarnings("unused") // TODO Block bad words?
	private final static String[] blockedNicks = {
		"mich",
		"will",
		"forbes",
		"david"
	};
	
	/**
	 * Check a nickname to see if it is allowed.  The current criteria that must be met are that:
	 * <ul>
	 *  <li> The nickname is three characters or longer </li>
	 * </ul>
	 * @param nick The nickname to test
	 * @return True if this nickname is allowed
	 */
	public static boolean testNickname(String nick)
	{
		nick=nick.trim().toLowerCase();
		
		if (nick.length() < 3)
			return false;
		
//		for (String block : blockedNicks)
//		{
//			if (nick.contains(block))
//			{
//				return false;
//			}
//		}
		
		return true;
	}
}
