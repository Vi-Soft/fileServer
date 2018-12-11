package com.visoft.exceptions;

import java.util.Map;

public class TemplateValidationException extends RuntimeException{

    private Map<String, String> exception;

    public TemplateValidationException(String message,Map<String, String> exception){
        super(message);
        this.exception = exception;
    }

    public TemplateValidationException(String message) {
        super(message);
    }

    public Map<String, String> getException (){
        return exception;
    }
}
