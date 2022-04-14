package com.visoft.file.service.service.util;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import static com.visoft.file.service.service.ErrorConst.UNABLE_TO_READ_FILE_FROM_BYTE;

public class ImageService {
    private static String JS = "text/javascript";
    private static String PNG = "image/png";

    public static void getJS(HttpServerExchange exchange, String imageName) throws IOException {
        sendFile(
                exchange,
                JS,
                getByteFromFile(imageName)
        );

    }

    public static void getImage(HttpServerExchange exchange, String imageName) {
        sendFile(
                exchange,
                PNG,
                getByteFromFile(imageName)
        );
    }

    private static byte[] getByteFromFile(String fileName) {
        byte[] fileContent = new byte[0];
        try {
            fileContent = IOUtils.toByteArray(Objects.requireNonNull(PageService.class.getClassLoader().getResourceAsStream("file/" + fileName)));
        } catch (IOException e) {
            SenderService.sendWarn(UNABLE_TO_READ_FILE_FROM_BYTE, e.getMessage());
            e.printStackTrace();
        }
        return fileContent;
    }

    private static void sendFile(HttpServerExchange exchange, String type, byte[] fileContent) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, type + "; charset=UTF-8");
        exchange.getResponseSender().send(ByteBuffer.wrap(fileContent));
    }
}