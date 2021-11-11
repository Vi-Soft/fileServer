//package com.visoft.file.service.web.controller;
//
//import com.networknt.server.HandlerProvider;
//import com.visoft.file.service.service.util.ImageService;
//import io.undertow.Handlers;
//import io.undertow.server.HttpHandler;
//import io.undertow.server.handlers.form.EagerFormParsingHandler;
//import io.undertow.server.handlers.form.FormParserFactory;
//import io.undertow.server.handlers.form.MultiPartParserDefinition;
//
//import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
//import static io.undertow.util.Methods.GET;
//import static io.undertow.util.Methods.PUT;
//
//public class StaticController implements HandlerProvider {
//
//    private HttpHandler getRedImage = (exchange) -> ImageService.getImage(exchange, "r.png");
//    private HttpHandler getGreyImage = (exchange) -> ImageService.getImage(exchange, "g.png");
//    private HttpHandler getHighlight = (exchange) -> ImageService.getJS(exchange, "highlight.js");
//    private HttpHandler getJquery = (exchange) -> ImageService.getJS(exchange, "jquery.js");
//    private HttpHandler getScrollToMin = (exchange) -> ImageService.getJS(exchange, "scrollTo-min.js");
//    private HttpHandler createAdmin = USER_SERVICE::createAdmin;
//
//    private EagerFormParsingHandler getEagerFormParsingHandler() {
//        return new EagerFormParsingHandler(FormParserFactory.builder()
//                .addParsers(new MultiPartParserDefinition()).build());
//    }
//
//    @Override
//    public HttpHandler getHandler() {
//        return Handlers.routing()
//                .add(GET, "/highlight",
//                        getEagerFormParsingHandler()
//                                .setNext(getHighlight))
//                .add(GET, "/jquery",
//                        getEagerFormParsingHandler()
//                                .setNext(getJquery))
//                .add(GET, "/scrollToMin",
//                        getEagerFormParsingHandler()
//                                .setNext(getScrollToMin))
//                .add(GET, "/r",
//                        getEagerFormParsingHandler()
//                                .setNext(getRedImage))
//                .add(GET, "/g",
//                        getEagerFormParsingHandler()
//                                .setNext(getGreyImage))
//                .add(PUT, "/createAdmin",
//                        getEagerFormParsingHandler()
//                                .setNext(createAdmin));
//    }
//}