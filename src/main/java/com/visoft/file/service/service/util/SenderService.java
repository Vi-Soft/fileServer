package com.visoft.file.service.service.util;

import io.undertow.server.HttpServerExchange;

public class SenderService {

    public static String sendMessage(HttpServerExchange exchange, String message) {
        exchange.getResponseSender().send(message);
        return null;
    }

    public static String sendStatusCode(HttpServerExchange exchange, int code) {
        exchange.setStatusCode(code);
        return null;
    }
}
