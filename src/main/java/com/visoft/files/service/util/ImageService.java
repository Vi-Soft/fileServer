package com.visoft.files.service.util;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class ImageService {
    public static void getImage(HttpServerExchange exchange, String imageName) throws IOException {
        byte[] fileContent = IOUtils.toByteArray(Objects.requireNonNull(PageService.class.getClassLoader().getResourceAsStream("image/" + imageName)));
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "image/png; charset=UTF-8");
        exchange.getResponseSender().send(ByteBuffer.wrap(fileContent));

    }
}
