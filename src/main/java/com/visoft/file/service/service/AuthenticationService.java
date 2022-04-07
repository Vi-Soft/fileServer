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
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
import static com.visoft.file.service.service.ErrorConst.*;
import static com.visoft.file.service.service.StatusConst.LOGIN;
import static com.visoft.file.service.service.StatusConst.LOGIN_SUCCESS;
import static com.visoft.file.service.service.util.SenderService.*;

@Log4j
public class AuthenticationService {

    public static void logout(HttpServerExchange exchange) {
        Token token = SecurityHandler.authenticatedUser.getToken();
        DependencyInjectionService.TOKEN_SERVICE.setExpirationNow(token.getId());
        PageService.redirectToLoginPage(exchange);
    }

    public static void login(HttpServerExchange exchange) {
        log.warn(LOGIN);
        LoginDto loginDto = getRequestBody(exchange);
        if (loginDto == null) {
            exchange.setStatusCode(BAD_REQUEST);
            sendWarn(LOGIN_NOT_CORRECT, null);
        } else {
            String validateResult = validate(loginDto);
            if (validateResult != null) {
                exchange.setStatusCode(BAD_REQUEST);
                sendWarn(LOGIN_NOT_VALID, validateResult);
            } else {
                User user = USER_SERVICE.findByLoginAndPassword(loginDto.getLogin(), loginDto.getPassword());
                if (user == null) {
                    exchange.setStatusCode(UNAUTHORIZED);
                    sendWarn(LOGIN_NOT_VALID, loginDto.getLogin() + " " + loginDto.getPassword());
                } else {
                    Token token = DependencyInjectionService.TOKEN_SERVICE.findByUserId(user.getId());
                    if (token == null) {
                        exchange.setStatusCode(UNAUTHORIZED);
                        sendWarn(TOKEN_NOT_FOUND, user.getId().toString());
                    } else {
                        DependencyInjectionService.TOKEN_SERVICE.addExpiration(token.getId());
                        TokenOutcomeDto tokenOutcomeDto = new TokenOutcomeDto(
                                token.getToken(),
                                user.getRole()
                        );
                        exchange.setResponseCookie(new CookieImpl("token", token.getToken()).setPath("/"));
                        send(exchange, JsonService.toJson(tokenOutcomeDto));
                        sendWarn(LOGIN_SUCCESS, loginDto.getLogin());
                    }
                }
            }
        }
    }

    public static void getApplicationVersion(HttpServerExchange exchange) {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            send(exchange, Optional.ofNullable(model.getVersion()).orElse("undefined"));
        } catch (IOException | XmlPullParserException e) {
            sendWarn(UNABLE_TO_GET_APP_VERSION, e.getMessage());
            e.printStackTrace();
        }
    }

    private static LoginDto getRequestBody(HttpServerExchange exchange) {
        exchange.startBlocking();
        InputStream is = exchange.getInputStream();
        String s = (new Scanner(is, "UTF-8")).useDelimiter("\\A").next();
        try {
            return Config.getInstance().getMapper().readValue(s, LoginDto.class);
        } catch (IOException e) {
            sendWarn(UNABLE_TO_READ_VALUE_REQUEST_BODY, e.getMessage());
            e.printStackTrace();
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