package com.visoft.exceptions;

/**
 * @author vlad
 *
 */
public class EmailRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -7121740675508842969L;

	public EmailRuntimeException(String message) {
		super(message);
	}
}
