package com.visoft.file.service.web.handler;

import com.networknt.server.HandlerProvider;
import com.visoft.file.service.service.util.PropertiesService;
import com.visoft.file.service.web.controller.AdminFolderController;
import com.visoft.file.service.web.controller.AdminUserController;
//import com.visoft.file.service.web.controller.StaticController;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.MimeMappings;

import java.nio.file.Paths;

import static io.undertow.Handlers.path;

public class GeneralHandlerProvider implements HandlerProvider {

    private String path = PropertiesService.getRootPath();

    @Override
    public HttpHandler getHandler() {
        ResourceHandler resourceHandler = new FileResourceHandler(
                new PathResourceManager(Paths.get(path), 100));

        PathHandler handler = path().addPrefixPath(
                "/",
                resourceHandler
                        .setDirectoryListingEnabled(false)
                        .setMimeMappings(MimeMappings.builder(true)
                                .addMapping("html", "text/html;charset=utf-8")
                                .build()
                        )
        );
        handler.addPrefixPath("api", new RouterHandlerProvider().getHandler())
//                .addPrefixPath("static", new StaticController().getHandler())
                .addPrefixPath("admin/users", new AdminUserController().getHandler())
                .addPrefixPath("admin/folders", new AdminFolderController().getHandler());
        return handler;
    }
}