package com.visoft.file.service.exception.user;


import com.visoft.file.service.exception.BadRequestException;

public class UserNotFoundException extends BadRequestException {

    public UserNotFoundException() {
        super("User Not Found");
    }
}
