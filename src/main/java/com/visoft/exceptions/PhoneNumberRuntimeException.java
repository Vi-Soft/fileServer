package com.visoft.exceptions;

public class PhoneNumberRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -9001881538169727733L;

	public PhoneNumberRuntimeException() {
		super();
	}

	public PhoneNumberRuntimeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PhoneNumberRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public PhoneNumberRuntimeException(String message) {
		super(message);
	}

	public PhoneNumberRuntimeException(Throwable cause) {
		super(cause);
	}

}
