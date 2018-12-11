package com.visoft.files.service;

public interface ErrorConst {

    String LOGIN_NOT_CORRECT = "Login must be not null and not empty";

    String PASSWORD_NOT_CORRECT = "Password must be not null and not empty";

    String JSON_NOT_CORRECT = "JSON not correct";

    String NOT_AUTHORIZATION = "Login or password not found";

    String USER_TOKEN_NOT_FOUND = "User token not found";

    String FOLDERS_NOT_CORRECT = "Folders must be not null and not empty";

    String FOLDERS_EQUALS = "Folder must uniq";

    String FOLDERS_NOT_EXISTS = "Folder not exists";

    String LOGIN_EXISTS = "Login exists";

    String NO_TOKEN = "You not have token";

    String TOKEN_NOT_FOUND = "Token not found";

}
