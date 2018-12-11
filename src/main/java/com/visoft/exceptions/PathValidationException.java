package com.visoft.exceptions;

public class PathValidationException  extends RuntimeException{
        private String exception;

        public PathValidationException(String message, String exception){
            super(message);
            this.exception = exception;
        }

        public PathValidationException(String message) {
            super(message);
        }

    public String getException() {
        return exception;
    }
}