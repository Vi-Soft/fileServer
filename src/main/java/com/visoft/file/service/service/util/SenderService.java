package com.visoft.file.service.service.util;

import io.undertow.server.HttpServerExchange;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

@Log4j
public class SenderService {

    private static final String SEND = "send: ";
    private static final String SPACE = StringUtils.SPACE;

    public static void send(HttpServerExchange exchange, String message) {
        log.info(SEND + message);
        exchange.getResponseSender().send(message);
    }

    public static void send(HttpServerExchange exchange, String message, int code) {
        log.info(SEND + code + SPACE + message);
        exchange.setStatusCode(code);
        exchange.getResponseSender().send(message);
    }

    public static void send(HttpServerExchange exchange, int code) {
        log.info(SEND + code);
        exchange.setStatusCode(code);
    }

    public static void sendInfo(String messageInfo, String content) {
        log.info(messageInfo + ":" + SPACE + content);
    }

    public static void sendWarn(String messageInfo, String content) {
        log.warn(messageInfo + ":" + SPACE + content);
    }

    public static void sendWarn(String messageInfo, int code) {
        log.warn(messageInfo + SPACE + code);
    }
}