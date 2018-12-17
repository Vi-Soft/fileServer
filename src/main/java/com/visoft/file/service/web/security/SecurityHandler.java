package com.visoft.file.service.web.security;

import com.networknt.config.Config;
import com.networknt.exception.ExceptionHandler;
import com.networknt.handler.Handler;
import com.networknt.handler.MiddlewareHandler;
import com.networknt.utility.ModuleRegistry;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.User;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;

import java.time.Instant;

import static com.visoft.file.service.persistance.entity.Role.USER;
import static com.visoft.file.service.service.DI.DependencyInjectionService.TOKEN_SERVICE;
import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
import static com.visoft.file.service.service.ErrorConst.FORBIDDEN;
import static com.visoft.file.service.service.ErrorConst.UNAUTHORIZED;

public class SecurityHandler implements MiddlewareHandler {

    public static AuthenticatedUser authenticatedUser;
    private volatile HttpHandler next;
    private String USER_URI = "/api/user";
    private String ADMIN_URI = "/api/admin";
    private String cookieName = "token";

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.getRequestURI().startsWith("/api/login")) {
            Handler.next(exchange, next);
        } else {
            Cookie cookie = getCookie(exchange);
            if (cookie == null) {
                exchange.setStatusCode(UNAUTHORIZED);
            } else {
                Token token = TOKEN_SERVICE.findByToken(cookie.getValue());
                if (token == null || token.getExpiration().toEpochMilli() < Instant.now().toEpochMilli()) {
                    exchange.setStatusCode(UNAUTHORIZED);
                } else {
                    User user = USER_SERVICE.findByIdNotDeleted(token.getUserId());
                    if (user == null) {
                        exchange.setStatusCode(FORBIDDEN);
                    } else {
                        authenticatedUser = new AuthenticatedUser(user, cookie, token);
                        String requestURI = exchange.getRequestURI();
                        if (requestURI.startsWith(ADMIN_URI) && user.getRole().equals(USER)) {
                            exchange.setStatusCode(FORBIDDEN);
                        } else {
                            TOKEN_SERVICE.addExpiration(authenticatedUser.getToken().getUserId());
                            setCookie(exchange, cookie);
                            Handler.next(exchange, next);
                        }
                    }
                }
            }
        }

    }

    @Override
    public HttpHandler getNext() {
        return this.next;
    }

    @Override
    public MiddlewareHandler setNext(HttpHandler next) {
        Handlers.handlerNotNull(next);
        this.next = next;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void register() {
        ModuleRegistry.registerModule(ExceptionHandler.class.getName(), Config.getInstance().getJsonMapConfigNoCache("security"), null);
    }


    private void setCookie(HttpServerExchange exchange, Cookie cookie) {
        exchange.setResponseCookie(new CookieImpl(cookieName, cookie.getValue()));
    }

    private Cookie getCookie(HttpServerExchange exchange) {
        return exchange.getRequestCookies().get(cookieName);
    }
}
