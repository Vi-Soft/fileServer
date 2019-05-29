package com.visoft.file.service.service.util;

import com.networknt.handler.util.Exchange;
import io.undertow.server.HttpServerExchange;
import org.bson.types.ObjectId;

public class RequestService {

    public static ObjectId getIdFromRequest(HttpServerExchange exchange) {
        return new ObjectId(Exchange.queryParams().queryParam(exchange, "id").orElse(""));
    }
}