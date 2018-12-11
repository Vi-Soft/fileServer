package com.visoft.files.service;

import com.networknt.config.Config;
import com.visoft.files.dto.LoginDto;
import com.visoft.files.dto.TokenOutcomeDto;
import com.visoft.files.entity.Token;
import com.visoft.files.entity.User;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.visoft.files.service.DI.DependencyInjectionService.TOKEN_SERVICE;
import static com.visoft.files.service.DI.DependencyInjectionService.USER_SERVICE;
import static com.visoft.files.service.ErrorConst.*;
import static com.visoft.files.service.util.JsonService.toJson;
import static com.visoft.files.service.util.SenderService.sendMessage;

public class AuthService {

    private static UserService userService = USER_SERVICE;


    public static String getToken(HttpServerExchange exchange) {
        LoginDto loginDto = getRequestBody(exchange);
        if (loginDto == null) {
            return sendMessage(exchange, JSON_NOT_CORRECT);
        }
        String validateResult = validate(loginDto);
        if (validateResult != null) {
            return sendMessage(exchange, validateResult);
        }
        User user = userService.findByLoginAndPassword(loginDto.getLogin(), loginDto.getPassword());
        if (user == null) {
            return sendMessage(exchange, NOT_AUTHORIZATION);
        }
        Token token = TOKEN_SERVICE.findByUserId(user.getId());
        if (token == null) {
            return sendMessage(exchange, USER_TOKEN_NOT_FOUND);
        }
        TOKEN_SERVICE.addExpiration(token.getId());
        TokenOutcomeDto tokenOutcomeDto = new TokenOutcomeDto(
                token.getToken(),
                user.getRole()
        );

        return sendMessage(exchange, toJson(tokenOutcomeDto));
    }

    private static LoginDto getRequestBody(HttpServerExchange exchange) {
        exchange.startBlocking();
        InputStream is = exchange.getInputStream();
        String s = (new Scanner(is, "UTF-8")).useDelimiter("\\A").next();
        try {
            return Config.getInstance().getMapper().readValue(s, LoginDto.class);
        } catch (IOException e) {
            return null;
        }
    }

    private static String validate(LoginDto loginDto) {
        String login = loginDto.getLogin();
        String password = loginDto.getPassword();
        if (login == null || login.isEmpty()) {
            return LOGIN_NOT_CORRECT;
        }
        if (password == null || password.isEmpty()) {
            return PASSWORD_NOT_CORRECT;
        }
        return null;
    }
}
