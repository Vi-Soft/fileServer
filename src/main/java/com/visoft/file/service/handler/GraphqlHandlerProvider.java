package com.visoft.file.service.handler;

import com.networknt.graphql.router.GraphqlRouter;
import com.visoft.file.service.service.util.PropertiesService;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.MimeMappings;

import java.nio.file.Paths;

import static io.undertow.Handlers.path;

/**
 * Two end-points are defined in the application.
 * <p>
 * /graphql
 * /api
 *
 * @author vlad
 */
public class GraphqlHandlerProvider extends GraphqlRouter {

    private String path = PropertiesService.getRootPath();

    @Override
    public HttpHandler getHandler() {
        ResourceHandler resourceHandler = new FileResourceHandler(
                new PathResourceManager(Paths.get(path), 100));

        PathHandler handler = path().addPrefixPath(
                "/",
                resourceHandler
                        .setDirectoryListingEnabled(true)
                        .setMimeMappings(MimeMappings.builder(true)
                                .addMapping("html", "text/html;charset=utf-8")
                                .build()
                        )
        );
        handler.addPrefixPath("api",
                (new RouterHandlerProvider()).getHandler());

        return handler;

    }

}
