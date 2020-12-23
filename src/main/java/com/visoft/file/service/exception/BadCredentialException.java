package com.visoft.file.service.exception;

public class BadCredentialException extends BadRequestException {

    public BadCredentialException() {
        super("Bad Credentials");
    }
}
