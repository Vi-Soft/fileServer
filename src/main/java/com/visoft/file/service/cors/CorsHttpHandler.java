package com.visoft.file.service.cors;

import com.networknt.config.Config;
import com.networknt.handler.Handler;
import com.networknt.handler.MiddlewareHandler;
import com.networknt.utility.ModuleRegistry;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;

import java.util.Collection;

import static com.visoft.file.service.cors.CorsHeaders.*;
import static com.visoft.file.service.cors.CorsUtil.isPreflightedRequest;
import static io.undertow.server.handlers.ResponseCodeHandler.HANDLE_200;

public class CorsHttpHandler implements MiddlewareHandler {

    public static final String CONFIG_NAME = "cors";

    public static CorsConfig config =
            (CorsConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME, CorsConfig.class);

    private static final Collection<String> allowedOrigins = config.getAllowedOrigins();
    private static final Collection<String> allowedMethods = config.getAllowedMethods();

    private volatile HttpHandler next;
    /** Default max age **/
    private static final long ONE_HOUR_IN_SECONDS = 60 * 60;

    public CorsHttpHandler() {

    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HeaderMap headers = exchange.getRequestHeaders();
        if (CorsUtil.isCoreRequest(headers)) {
            if (isPreflightedRequest(exchange)) {
                handlePreflightRequest(exchange);
                return;
            }
            setCorsResponseHeaders(exchange);
        }
        Handler.next(exchange, next);
    }

    private void handlePreflightRequest(HttpServerExchange exchange) throws Exception {
        setCorsResponseHeaders(exchange);
        HANDLE_200.handleRequest(exchange);
    }

    private void setCorsResponseHeaders(HttpServerExchange exchange) throws Exception {
        HeaderMap headers = exchange.getRequestHeaders();
        if (headers.contains(Headers.ORIGIN)) {
            if(CorsUtil.matchOrigin(exchange, allowedOrigins) != null) {
                exchange.getResponseHeaders().addAll(ACCESS_CONTROL_ALLOW_ORIGIN, headers.get(Headers.ORIGIN));
                exchange.getResponseHeaders().add(Headers.VARY, Headers.ORIGIN_STRING);
            }
        }
        exchange.getResponseHeaders().addAll(ACCESS_CONTROL_ALLOW_METHODS, allowedMethods);
        HeaderValues requestedHeaders = headers.get(ACCESS_CONTROL_REQUEST_HEADERS);
        if (requestedHeaders != null && !requestedHeaders.isEmpty()) {
            exchange.getResponseHeaders().addAll(ACCESS_CONTROL_ALLOW_HEADERS, requestedHeaders);
        } else {
            exchange.getResponseHeaders().add(ACCESS_CONTROL_ALLOW_HEADERS, Headers.CONTENT_TYPE_STRING);
            exchange.getResponseHeaders().add(ACCESS_CONTROL_ALLOW_HEADERS, Headers.WWW_AUTHENTICATE_STRING);
            exchange.getResponseHeaders().add(ACCESS_CONTROL_ALLOW_HEADERS, Headers.AUTHORIZATION_STRING);
        }
        exchange.getResponseHeaders().add(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        exchange.getResponseHeaders().add(ACCESS_CONTROL_MAX_AGE, ONE_HOUR_IN_SECONDS);
    }

    @Override
    public HttpHandler getNext() {
        return next;
    }

    @Override
    public MiddlewareHandler setNext(final HttpHandler next) {
        Handlers.handlerNotNull(next);
        this.next = next;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void register() {
        ModuleRegistry.registerModule(CorsHttpHandler.class.getName(), Config.getInstance().getJsonMapConfigNoCache(CONFIG_NAME), null);
    }

}