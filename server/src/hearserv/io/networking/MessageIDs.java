package hearserv.io.networking;

public class MessageIDs
{	
	// message types
	public static final short
		NEARBY_LOCALE_REQUEST 		=  0,
		NEARBY_LOCALE_RESPONSE 		=  1,
		JOIN_PUBLIC_LOCALE_REQUEST 	=  2,
		JOIN_PRIVATE_LOCALE_REQUEST =  3,
		JOIN_LOCALE_RESPONSE 		=  4,
		LEAVE_LOCALE_REQUEST 		=  5,
		LEAVE_LOCALE_RESPONSE 		=  6,
		SEND_TEXT_POST_REQUEST 		=  7, // does not have response
		SEND_IMAGE_POST_REQUEST 	=  8, // does not have response
		NEW_TEXT_POST_NOTICE 		= 10,
		NEW_IMAGE_POST_NOTICE 		= 11,
		CREATE_LOCALE_REQUEST 		= 12,
		CREATE_LOCALE_RESPONSE 		= 13,
		NICKNAME_SET_REQUEST 		= 14,
		NICKNAME_SET_RESPONSE 		= 15;
}
