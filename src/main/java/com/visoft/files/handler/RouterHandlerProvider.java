package com.visoft.files.handler;

import com.networknt.server.HandlerProvider;
import com.visoft.files.service.ReportService;
import com.visoft.files.service.AuthService;
import com.visoft.files.service.UserService;
import com.visoft.files.service.util.ImageService;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.util.Methods;

import static com.visoft.files.service.DI.DependencyInjectionService.USER_SERVICE;

public class RouterHandlerProvider implements HandlerProvider {

    UserService userService = USER_SERVICE;

    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(Methods.PUT, "/createUser",
                        getEagerFormParsingHandler()
                                .setNext(createUser))
                .add(Methods.POST, "/login",
                        getEagerFormParsingHandler()
                                .setNext(login))
                .add(Methods.GET, "/r",
                        getEagerFormParsingHandler()
                                .setNext(getRedImage))
                .add(Methods.GET, "/g",
                        getEagerFormParsingHandler()
                                .setNext(getGreyImage))
                .add(Methods.POST, "/unzip",
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
    HttpHandler login = AuthService::getToken;
    HttpHandler createUser = userService::create;
}
