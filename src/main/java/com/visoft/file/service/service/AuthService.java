package com.visoft.file.service.service;

import com.networknt.config.Config;
import com.visoft.file.service.dto.LoginDto;
import com.visoft.file.service.dto.TokenOutcomeDto;
import com.visoft.file.service.entity.Token;
import com.visoft.file.service.entity.User;
import com.visoft.file.service.service.DI.DependencyInjectionService;
import com.visoft.file.service.service.util.JsonService;
import com.visoft.file.service.service.util.PageService;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.visoft.file.service.service.util.SenderService.sendMessage;

public class AuthService {

    private static UserService userService = DependencyInjectionService.USER_SERVICE;

    public static void logout(HttpServerExchange exchange) {
        Cookie cookie = exchange.getRequestCookies().get("token");
        if (cookie == null) {
            sendMessage(exchange, ErrorConst.NO_COOKIE);
            return;
        }
        Token token = DependencyInjectionService.TOKEN_SERVICE.findByToken(cookie.getValue());
        if (token == null) {
            sendMessage(exchange, ErrorConst.TOKEN_NOT_FOUND);
            return;
        }
        DependencyInjectionService.TOKEN_SERVICE.setExpirationNow(token.getId());
        PageService.redirectToLoginPage(exchange);
    }

    public static void login(HttpServerExchange exchange) {
        LoginDto loginDto = getRequestBody(exchange);
        if (loginDto == null) {
            sendMessage(exchange, ErrorConst.JSON_NOT_CORRECT);
        }
        String validateResult = validate(loginDto);
        if (validateResult != null) {
            sendMessage(exchange, validateResult);
        }
        User user = userService.findByLoginAndPassword(loginDto.getLogin(), loginDto.getPassword());
        if (user == null) {
            sendMessage(exchange, ErrorConst.NOT_AUTHORIZATION);
        }
        Token token = DependencyInjectionService.TOKEN_SERVICE.findByUserId(user.getId());
        if (token == null) {
            sendMessage(exchange, ErrorConst.USER_TOKEN_NOT_FOUND);
        }
        DependencyInjectionService.TOKEN_SERVICE.addExpiration(token.getId());
        TokenOutcomeDto tokenOutcomeDto = new TokenOutcomeDto(
                token.getToken(),
                user.getRole()
        );
        sendMessage(exchange, JsonService.toJson(tokenOutcomeDto));
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
