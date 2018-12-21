package com.visoft.file.service.web.handler;

import com.networknt.server.HandlerProvider;
import com.visoft.file.service.service.AuthenticationService;
import com.visoft.file.service.service.ReportService;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;

import static io.undertow.util.Methods.POST;

public class RouterHandlerProvider implements HandlerProvider {

    private HttpHandler unzip = ReportService::unzip;
    private HttpHandler login = AuthenticationService::login;
    private HttpHandler logout = AuthenticationService::logout;

    private EagerFormParsingHandler getEagerFormParsingHandler() {
        return new EagerFormParsingHandler(FormParserFactory.builder()
                .addParsers(new MultiPartParserDefinition()).build());
    }

    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(POST, "/logout",
                        getEagerFormParsingHandler()
                                .setNext(logout))
                .add(POST, "/login",
                        getEagerFormParsingHandler()
                                .setNext(login))
                .add(POST, "/unzip",
                        getEagerFormParsingHandler()
                                .setNext(unzip));
    }
}