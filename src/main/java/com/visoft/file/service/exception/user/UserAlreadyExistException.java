package com.visoft.file.service.exception.user;


import com.visoft.file.service.exception.BadRequestException;

public class UserAlreadyExistException extends BadRequestException {

    public UserAlreadyExistException() {
        super("User Already Exist");
    }
}
