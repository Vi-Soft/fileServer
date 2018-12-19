package com.visoft.file.service.web.handler;

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

    private HttpHandler deleteFolder = FOLDER_SERVICE::deleteFolder;
    private HttpHandler allFolders = FOLDER_SERVICE::findAllFolders;

    private EagerFormParsingHandler getEagerFormParsingHandler() {
        return new EagerFormParsingHandler(FormParserFactory.builder()
                .addParsers(new MultiPartParserDefinition()).build());
    }

    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(GET, "findAll",
                        getEagerFormParsingHandler()
                                .setNext(allFolders))
                .add(DELETE, "/{id}",
                        getEagerFormParsingHandler()
                                .setNext(deleteFolder));
    }
}
