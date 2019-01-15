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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.visoft.file.service.persistance.entity.Role.USER;
import static com.visoft.file.service.service.DI.DependencyInjectionService.TOKEN_SERVICE;
import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
import static com.visoft.file.service.service.ErrorConst.FORBIDDEN;
import static com.visoft.file.service.service.ErrorConst.UNAUTHORIZED;
import static com.visoft.file.service.service.util.PageService.redirectToLoginPage;

public class SecurityHandler implements MiddlewareHandler {

    public static AuthenticatedUser authenticatedUser;

    private volatile HttpHandler next;

    private String ADMIN_URI = "/admin";

    private String cookieName = "token";

    private List<String> allAccess = new ArrayList<>(Arrays.asList("/api/login", "/api/unzip", "/static"));

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (isHasAccess(exchange.getRequestURI())) {
            Handler.next(exchange, next);
        } else {
            Cookie cookie = getCookie(exchange);
            if (cookie == null) {
                redirectToLoginPage(exchange);
//                exchange.setStatusCode(UNAUTHORIZED);
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

    private Cookie getCookie(HttpServerExchange exchange) {
        return exchange.getRequestCookies().get(cookieName);
    }

    private boolean isHasAccess(String requestURI) {
        for (String access : allAccess) {
            if (requestURI.startsWith(access)) {
                return true;
            }
        }
        return false;
    }
}