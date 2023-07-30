short numberOfLocales = inFromServer.readShort();

	for (short i = 0; i < numberOfLocales; i++)
	{
		long localeID = inFromServer.readLong();
		byte flag = inFromServer.readByte();
		// todo extract private bit
		
		short length = inFromServer.readShort();
		char[] buffer = new char[length];
		for (int j = 0; j < length; j++)
		{
			buffer[i] = inFromServer.readChar();
		}
		String localeName = new String(buffer);
		
		short numberOfPeople = inFromServer.readShort();
		
		// TODO: make a locale object, do something with it
	}