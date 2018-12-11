package com.visoft.exceptions;

public class AuthDataRuntimeException extends RuntimeException{

	private static final long serialVersionUID = -1686673297538015961L;

	public AuthDataRuntimeException() {
		super();
	}

	public AuthDataRuntimeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AuthDataRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthDataRuntimeException(String message) {
		super(message);
	}

	public AuthDataRuntimeException(Throwable cause) {
		super(cause);
	}

}
