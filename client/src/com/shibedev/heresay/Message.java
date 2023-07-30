package com.shibedev.heresay;

import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	private String sender;
	private String message;

	public Message(String sender, String message) {
		this.sender = sender;
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return sender+": "+message;
	}
}
