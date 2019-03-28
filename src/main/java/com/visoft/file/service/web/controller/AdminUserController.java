package com.visoft.file.service.web.controller;

import com.networknt.server.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;

import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
import static io.undertow.util.Methods.*;

public class AdminUserController implements HandlerProvider {

    private HttpHandler createUser = USER_SERVICE::createUser;
    private HttpHandler deleteUser = USER_SERVICE::delete;
    private HttpHandler recoveryUser = USER_SERVICE::recovery;
    private HttpHandler findAllUser = USER_SERVICE::findAll;
    private HttpHandler findByIdUser = USER_SERVICE::findById;
    private HttpHandler updateUser = USER_SERVICE::update;

    private EagerFormParsingHandler getEagerFormParsingHandler() {
        return new EagerFormParsingHandler(FormParserFactory.builder()
                .addParsers(new MultiPartParserDefinition()).build());
    }

    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(DELETE, "/{id}",
                        getEagerFormParsingHandler()
                                .setNext(deleteUser))
                .add(POST, "/recovery/{id}",
                        getEagerFormParsingHandler()
                                .setNext(recoveryUser))
                .add(PUT, "/create",
                        getEagerFormParsingHandler()
                                .setNext(createUser))
                .add(GET, "/findAll",
                        getEagerFormParsingHandler()
                                .setNext(findAllUser))
                .add(GET, "findById/{id}",
                        getEagerFormParsingHandler()
                                .setNext(findByIdUser))
                .add(POST, "update",
                        getEagerFormParsingHandler()
                                .setNext(updateUser));
    }
}