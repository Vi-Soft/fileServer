package com.visoft.exceptions;

public class DocMngRuntimeException  extends RuntimeException {

	private static final long serialVersionUID = -4251230961164150702L;

	public DocMngRuntimeException() {
		super();
	}

	public DocMngRuntimeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DocMngRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public DocMngRuntimeException(String message) {
		super(message);
	}

	public DocMngRuntimeException(Throwable cause) {
		super(cause);
	}

}
