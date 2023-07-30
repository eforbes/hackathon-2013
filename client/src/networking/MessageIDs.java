package networking;

import java.util.Hashtable;

public final class MessageIDs
{	
	/** Unconstructable */
	private MessageIDs(){}
	
	/** The version of the protocol. This MUST be incremented every time the protocol changes */
	public static final short PROTOCOL_VERSION = 1;
	
	// message types
	public final static short
		NEARBY_LOCALE_REQUEST 		=  0,
		NEARBY_LOCALE_RESPONSE 		=  1,
		JOIN_PUBLIC_LOCALE_REQUEST 	=  2,
		JOIN_PRIVATE_LOCALE_REQUEST =  3,
		JOIN_LOCALE_RESPONSE 		=  4,
		LEAVE_LOCALE_REQUEST 		=  5,
		LEAVE_LOCALE_RESPONSE 		=  6,
		SEND_TEXT_POST_REQUEST 		=  7, // does not have response
		SEND_IMAGE_POST_REQUEST 	=  8, // does not have response
		NEW_TEXT_POST_NOTICE 		= 10, // server -> client
		NEW_IMAGE_POST_NOTICE 		= 11, // server -> client
		CREATE_LOCALE_REQUEST 		= 12,
		CREATE_LOCALE_RESPONSE 		= 13,
		NICKNAME_SET_REQUEST 		= 14,
		NICKNAME_SET_RESPONSE 		= 15,
		VERSION_CHECK_REQUEST 		= 16,
		VERSION_CHECK_RESPONSE 		= 17,
		KEEPALIVE_NOTICE 			= 18,
		ERROR_NOTICE				= 19;
	
	public final static short
		ERRORCODE_GENERIC 			=  0,
		ERRORCODE_EVANS_GONE_WILD   =  1;
	
	private final static Hashtable<Short, String> errorLookupTable = initErrorLookupTable();
	
	private final static Hashtable<Short, String> initErrorLookupTable()
	{
		Hashtable<Short, String> errors = new Hashtable<Short, String>();
		errors.put(ERRORCODE_GENERIC, 			"generic error");
		errors.put(ERRORCODE_EVANS_GONE_WILD, 	"Evans gone wild!");
		
		return null;
	}
	
	public final static String getErrorMessage(short errorCode)
	{
		String message = errorLookupTable.get(errorCode);
		
		if (message == null)
			return "Unknown error code: " + errorCode;
		else
			return message;
	}
}
