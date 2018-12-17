package com.visoft.file.service.web.handler;

import com.networknt.server.HandlerProvider;
import com.visoft.file.service.service.AuthService;
import com.visoft.file.service.service.ReportService;
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

    private HttpHandler unzip = ReportService::unzip;
    private HttpHandler getRedImage = (exchange) -> ImageService.getImage(exchange, "r.png");
    private HttpHandler getGreyImage = (exchange) -> ImageService.getImage(exchange, "g.png");
    private HttpHandler login = AuthService::login;
    private HttpHandler createUser = USER_SERVICE::create;
    private HttpHandler logout = AuthService::logout;
    private HttpHandler deleteFolder = FOLDER_SERVICE::deleteFolder;
    private HttpHandler allFolders = FOLDER_SERVICE::findAllFolders;
    private HttpHandler deleteUser = USER_SERVICE::delete;
    private HttpHandler recoveryUser = USER_SERVICE::recovery;

    private EagerFormParsingHandler getEagerFormParsingHandler() {
        return new EagerFormParsingHandler(FormParserFactory.builder()
                .addParsers(new MultiPartParserDefinition()).build());
    }

    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(DELETE, "/users/{id}",
                        getEagerFormParsingHandler()
                                .setNext(deleteUser))
                .add(POST, "/users/recovery/{id}",
                        getEagerFormParsingHandler()
                                .setNext(recoveryUser))
                .add(GET, "/folders",
                        getEagerFormParsingHandler()
                                .setNext(allFolders))
                .add(DELETE, "/folders//{id}",
                        getEagerFormParsingHandler()
                                .setNext(deleteFolder))
                .add(POST, "/logout",
                        getEagerFormParsingHandler()
                                .setNext(logout))
                .add(PUT, "/users",
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
}
