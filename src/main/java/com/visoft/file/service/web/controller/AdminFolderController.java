package com.visoft.file.service.web.controller;

import com.networknt.server.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;

import static com.visoft.file.service.service.DI.DependencyInjectionService.FOLDER_SERVICE;
import static io.undertow.util.Methods.DELETE;
import static io.undertow.util.Methods.GET;

public class AdminFolderController implements HandlerProvider {

    private HttpHandler delete = FOLDER_SERVICE::delete;
    private HttpHandler findAll = FOLDER_SERVICE::findAll;
    private HttpHandler findById = FOLDER_SERVICE::findById;

    private EagerFormParsingHandler getEagerFormParsingHandler() {
        return new EagerFormParsingHandler(FormParserFactory.builder()
                .addParsers(new MultiPartParserDefinition()).build());
    }

    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(GET, "findAll",
                        getEagerFormParsingHandler()
                                .setNext(findAll))
                .add(DELETE, "/{id}",
                        getEagerFormParsingHandler()
                                .setNext(delete))
                .add(GET, "/findById/{id}",
                        getEagerFormParsingHandler()
                                .setNext(findById));
    }
}
