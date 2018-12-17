package com.visoft.file.service.web.handler;

import com.networknt.server.HandlerProvider;
import com.visoft.file.service.service.AuthenticationService;
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

    private String ROLE_ADMIN = "admin/";

    private String USER = "users/";

    private String FOLDERS = "folders/";

    private HttpHandler unzip = ReportService::unzip;
    private HttpHandler getRedImage = (exchange) -> ImageService.getImage(exchange, "r.png");
    private HttpHandler getGreyImage = (exchange) -> ImageService.getImage(exchange, "g.png");
    private HttpHandler login = AuthenticationService::login;
    private HttpHandler createUser = USER_SERVICE::create;
    private HttpHandler logout = AuthenticationService::logout;
    private HttpHandler deleteFolder = FOLDER_SERVICE::deleteFolder;
    private HttpHandler allFolders = FOLDER_SERVICE::findAllFolders;
    private HttpHandler deleteUser = USER_SERVICE::delete;
    private HttpHandler recoveryUser = USER_SERVICE::recovery;
    private HttpHandler findAllUser = USER_SERVICE::findAllUser;

    private EagerFormParsingHandler getEagerFormParsingHandler() {
        return new EagerFormParsingHandler(FormParserFactory.builder()
                .addParsers(new MultiPartParserDefinition()).build());
    }

    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                //admin user
                .add(DELETE, ROLE_ADMIN + USER + "/{id}",
                        getEagerFormParsingHandler()
                                .setNext(deleteUser))
                .add(POST, ROLE_ADMIN + USER + "/recovery/{id}",
                        getEagerFormParsingHandler()
                                .setNext(recoveryUser))
                .add(PUT, ROLE_ADMIN + USER,
                        getEagerFormParsingHandler()
                                .setNext(createUser))
                .add(GET, ROLE_ADMIN + USER + "/findAll",
                        getEagerFormParsingHandler()
                                .setNext(findAllUser))
                //admin folder
                .add(GET, ROLE_ADMIN + FOLDERS,
                        getEagerFormParsingHandler()
                                .setNext(allFolders))
                .add(DELETE, ROLE_ADMIN + FOLDERS + "/{id}",
                        getEagerFormParsingHandler()
                                .setNext(deleteFolder))
                //all
                .add(POST, "/logout",
                        getEagerFormParsingHandler()
                                .setNext(logout))
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
