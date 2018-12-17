package com.visoft.file.service.web.security;

import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.User;
import io.undertow.server.handlers.Cookie;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticatedUser {

    private User user;

    private Cookie cookie;

    private Token token;

}
