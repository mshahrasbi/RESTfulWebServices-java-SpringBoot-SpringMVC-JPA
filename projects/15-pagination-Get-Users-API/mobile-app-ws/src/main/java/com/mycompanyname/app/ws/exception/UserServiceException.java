package com.mycompanyname.app.ws.exception;

public class UserServiceException extends RuntimeException {

	private static final long serialVersionUID = 5083360959220505813L;

	public UserServiceException(String message) {
		super(message);
	}
}
