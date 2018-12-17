package com.visoft.file.service.service;

import com.networknt.config.Config;
import com.visoft.file.service.dto.LoginDto;
import com.visoft.file.service.dto.TokenOutcomeDto;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.service.DI.DependencyInjectionService;
import com.visoft.file.service.service.util.JsonService;
import com.visoft.file.service.service.util.PageService;
import com.visoft.file.service.web.security.SecurityHandler;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.visoft.file.service.service.ErrorConst.BAD_REQUEST;
import static com.visoft.file.service.service.ErrorConst.UNAUTHORIZED;
import static com.visoft.file.service.service.util.SenderService.sendMessage;

public class AuthenticationService {

    private static UserService userService = DependencyInjectionService.USER_SERVICE;

    public static void logout(HttpServerExchange exchange) {
        Token token = SecurityHandler.authenticatedUser.getToken();
        DependencyInjectionService.TOKEN_SERVICE.setExpirationNow(token.getId());
        PageService.redirectToLoginPage(exchange);
    }

    public static void login(HttpServerExchange exchange) {
        LoginDto loginDto = getRequestBody(exchange);
        if (loginDto == null) {
            exchange.setStatusCode(BAD_REQUEST);
        } else {
            String validateResult = validate(loginDto);
            if (validateResult != null) {
                exchange.setStatusCode(BAD_REQUEST);
            } else {
                User user = userService.findByLoginAndPassword(loginDto.getLogin(), loginDto.getPassword());
                if (user == null) {
                    exchange.setStatusCode(UNAUTHORIZED);
                } else {
                    Token token = DependencyInjectionService.TOKEN_SERVICE.findByUserId(user.getId());
                    if (token == null) {
                        exchange.setStatusCode(UNAUTHORIZED);
                    } else {
                        DependencyInjectionService.TOKEN_SERVICE.addExpiration(token.getId());
                        TokenOutcomeDto tokenOutcomeDto = new TokenOutcomeDto(
                                token.getToken(),
                                user.getRole()
                        );
                        sendMessage(exchange, JsonService.toJson(tokenOutcomeDto));
                    }
                }

            }

        }

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
            return ErrorConst.LOGIN_NOT_CORRECT;
        }
        if (password == null || password.isEmpty()) {
            return ErrorConst.PASSWORD_NOT_CORRECT;
        }
        return null;
    }
}
