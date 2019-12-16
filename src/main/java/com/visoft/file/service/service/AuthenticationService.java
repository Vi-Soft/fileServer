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
import io.undertow.server.handlers.CookieImpl;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
import static com.visoft.file.service.service.ErrorConst.BAD_REQUEST;
import static com.visoft.file.service.service.ErrorConst.UNAUTHORIZED;
import static com.visoft.file.service.service.util.SenderService.send;

@Log4j
public class AuthenticationService {

    public static void logout(HttpServerExchange exchange) {
        Token token = SecurityHandler.authenticatedUser.getToken();
        DependencyInjectionService.TOKEN_SERVICE.setExpirationNow(token.getId());
        PageService.redirectToLoginPage(exchange);
    }

    public static void login(HttpServerExchange exchange) {
        log.warn("log");
        LoginDto loginDto = getRequestBody(exchange);
        if (loginDto == null) {
            exchange.setStatusCode(BAD_REQUEST);
        } else {
            String validateResult = validate(loginDto);
            if (validateResult != null) {
                exchange.setStatusCode(BAD_REQUEST);
            } else {
                User user = USER_SERVICE.findByLoginAndPassword(loginDto.getLogin(), loginDto.getPassword());
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
                        exchange.setResponseCookie(new CookieImpl("token", token.getToken()).setPath("/"));
                        send(exchange, JsonService.toJson(tokenOutcomeDto));
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