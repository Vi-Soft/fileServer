package com.visoft.file.service.service.util;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import lombok.extern.log4j.Log4j;

@Log4j
public class SenderService {

    public static void send(HttpServerExchange exchange, String message) {
        log.info("send: " + message);
        exchange.getResponseSender().send(message);
    }

    public static void send(HttpServerExchange exchange, String message, int code) {
        log.info("send: " + code + " " + message);
        exchange.setStatusCode(code);
        exchange.getResponseSender().send(message);
    }

    public static void send(HttpServerExchange exchange, int code) {
        log.info("send: " + code);
        exchange.setStatusCode(code);
    }
}