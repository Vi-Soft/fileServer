package com.visoft.exceptions;

public class UserRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -6062160387042123860L;

	public UserRuntimeException() {
		super();
	}

	public UserRuntimeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UserRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserRuntimeException(String message) {
		super(message);
	}

	public UserRuntimeException(Throwable cause) {
		super(cause);
	}

}
