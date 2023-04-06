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
import static io.undertow.util.Methods.GET;

public class RouterHandlerProvider implements HandlerProvider {

    private ReportService reportService = new ReportService();

    private HttpHandler unzip = e -> reportService.unzip(e);
    private HttpHandler share = e -> reportService.shareFolder(e);
    private HttpHandler login = AuthenticationService::login;
    private HttpHandler logout = AuthenticationService::logout;
    private HttpHandler version = AuthenticationService::getApplicationVersion;

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
                                .setNext(unzip))
                .add(POST, "/share",
                        getEagerFormParsingHandler()
                                .setNext(share))
                .add(GET, "/version",
                        getEagerFormParsingHandler()
                                .setNext(version));
    }
}
