package com.visoft.exceptions;

public class UniversalSetterRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -1915654488082996663L;

	public UniversalSetterRuntimeException() {
		super();
	}

	public UniversalSetterRuntimeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UniversalSetterRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UniversalSetterRuntimeException(String message) {
		super(message);
	}

	public UniversalSetterRuntimeException(Throwable cause) {
		super(cause);
	}

}
