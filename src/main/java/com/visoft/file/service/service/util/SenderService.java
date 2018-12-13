package com.visoft.file.service.service.util;

import io.undertow.server.HttpServerExchange;

public class SenderService {

    public static String sendMessage(HttpServerExchange exchange, String message) {
        exchange.getResponseSender().send(message);
        return null;
    }
}
