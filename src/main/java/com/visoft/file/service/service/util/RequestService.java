package com.visoft.file.service.service.util;

import com.networknt.handler.util.Exchange;
import com.visoft.file.service.util.pageable.Page;
import com.visoft.file.service.util.pageable.Pageable;
import com.visoft.file.service.util.pageable.Sort;
import io.undertow.server.HttpServerExchange;
import org.bson.types.ObjectId;

import java.util.Optional;

public class RequestService {

    public static ObjectId getIdFromRequest(HttpServerExchange exchange) {
        return new ObjectId(Exchange.queryParams().queryParam(exchange, "id").orElse(""));
    }

    public static String getParamFromRequest(HttpServerExchange exchange, String param) {
        return Exchange.queryParams().queryParam(exchange, param).orElse("");
    }

    public static Optional<String> getOptionalParamFromRequest(HttpServerExchange exchange, String param) {
        return Exchange.queryParams().queryParam(exchange, param);
    }

    public static Pageable getPageableFromRequest(HttpServerExchange exchange) {
        return Pageable.builder()
            .page(Page.builder()
                .number(getOptionalParamFromRequest(exchange, "page").map(Integer::parseInt).orElse(0))
                .size(getOptionalParamFromRequest(exchange, "size").map(Integer::parseInt).orElse(10))
                .build()
            ).sort(Sort.builder()
                .column(getParamFromRequest(exchange, "sort"))
                .direction(
                    Sort.Direction.fromOptionalString(
                        getParamFromRequest(exchange, "direction")
                    ).orElse(Sort.Direction.ASC)
                ).build()
            ).build();
    }
}