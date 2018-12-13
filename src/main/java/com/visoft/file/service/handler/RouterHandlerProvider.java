package com.visoft.file.service.handler;

import com.networknt.server.HandlerProvider;
import com.visoft.file.service.service.ReportService;
import com.visoft.file.service.service.AuthService;
import com.visoft.file.service.service.util.ImageService;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;

import static com.visoft.file.service.service.DI.DependencyInjectionService.FOLDER_SERVICE;
import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
import static io.undertow.util.Methods.*;

public class RouterHandlerProvider implements HandlerProvider {

    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(DELETE, "/folders/delete/{id}",
                        getEagerFormParsingHandler()
                                .setNext(deleteFolder))
                .add(POST, "/logout",
                        getEagerFormParsingHandler()
                                .setNext(logout))
                .add(PUT, "/createUser",
                        getEagerFormParsingHandler()
                                .setNext(createUser))
                .add(POST, "/login",
                        getEagerFormParsingHandler()
                                .setNext(login))
                .add(GET, "/r",
                        getEagerFormParsingHandler()
                                .setNext(getRedImage))
                .add(GET, "/g",
                        getEagerFormParsingHandler()
                                .setNext(getGreyImage))
                .add(POST, "/unzip",
                        getEagerFormParsingHandler()
                                .setNext(unzip));
    }

    private EagerFormParsingHandler getEagerFormParsingHandler() {
        return new EagerFormParsingHandler(FormParserFactory.builder()
                .addParsers(new MultiPartParserDefinition()).build());
    }

    HttpHandler unzip = ReportService::unzip;
    HttpHandler getRedImage = (exchange) -> ImageService.getImage(exchange, "r.png");
    HttpHandler getGreyImage = (exchange) -> ImageService.getImage(exchange, "g.png");
    HttpHandler login = AuthService::login;
    HttpHandler createUser = USER_SERVICE::create;
    HttpHandler logout = AuthService::logout;
    HttpHandler deleteFolder = FOLDER_SERVICE::deleteFolder;
}
