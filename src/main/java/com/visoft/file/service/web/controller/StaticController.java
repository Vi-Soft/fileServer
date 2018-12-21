package com.visoft.file.service.web.controller;

import com.networknt.server.HandlerProvider;
import com.visoft.file.service.service.util.ImageService;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;

import static io.undertow.util.Methods.GET;

public class StaticController implements HandlerProvider {

    private HttpHandler getRedImage = (exchange) -> ImageService.getImage(exchange, "r.png");
    private HttpHandler getGreyImage = (exchange) -> ImageService.getImage(exchange, "g.png");

    private EagerFormParsingHandler getEagerFormParsingHandler() {
        return new EagerFormParsingHandler(FormParserFactory.builder()
                .addParsers(new MultiPartParserDefinition()).build());
    }

    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(GET, "/r",
                        getEagerFormParsingHandler()
                                .setNext(getRedImage))
                .add(GET, "/g",
                        getEagerFormParsingHandler()
                                .setNext(getGreyImage));
    }
}