package com.visoft.file.service.service.util;

import io.undertow.server.HttpServerExchange;

public class SenderService {

    public static void sendMessage(HttpServerExchange exchange, String message) {
        exchange.getResponseSender().send(message);
    }

    public static void sendStatusCode(HttpServerExchange exchange, int code) {
        exchange.setStatusCode(code);
    }
}