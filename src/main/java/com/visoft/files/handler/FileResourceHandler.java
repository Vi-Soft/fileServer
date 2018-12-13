package com.visoft.files.handler;

import com.visoft.files.entity.Token;
import com.visoft.files.entity.User;
import com.visoft.files.service.util.PageService;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.visoft.files.service.DI.DependencyInjectionService.*;
import static com.visoft.files.service.ErrorConst.NO_COOKIE;
import static com.visoft.files.service.ErrorConst.TOKEN_NOT_FOUND;
import static com.visoft.files.service.util.SenderService.sendMessage;

public class FileResourceHandler extends ResourceHandler {

    FileResourceHandler(ResourceManager resourceSupplier) {
        super(resourceSupplier);
    }


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Cookie cookie = exchange.getRequestCookies().get("token");
        if (cookie == null) {
            sendMessage(exchange, NO_COOKIE);
            return;
        }
        Token token = TOKEN_SERVICE.findByToken(cookie.getValue());
        if (token == null || token.getExpiration().toEpochMilli() < Instant.now().toEpochMilli()) {
            sendMessage(exchange, TOKEN_NOT_FOUND);
            return;
        }
        ObjectId tokenId = token.getId();
        User user = USER_SERVICE.findById(token.getUserId());
        String requestURI = exchange.getRequestURI();
        if (user.getRole().toString().equals("USER")) {
            if (requestURI.equals("/")) {
                sendResponse(exchange, cookie, user, tokenId);
            }
            requestURI = reorganizeRequestURI(requestURI);
            if (!getFolders(user).contains(requestURI)) {
                sendResponse(exchange, cookie, user, tokenId);
            } else {
                sendResponse(exchange, cookie, requestURI, tokenId);
            }
        } else {
            super.handleRequest(exchange);
        }
    }

    private void sendResponse(HttpServerExchange exchange, Cookie cookie, User user, ObjectId tokenId) {
        PageService.getMainUserHtml(exchange, getFolders(user));
        sendResponse(exchange, cookie, tokenId);
    }

    private void sendResponse(HttpServerExchange exchange, Cookie cookie, String requestURI, ObjectId tokenId) throws IOException {
        PageService.getFolderUserHtml(exchange, requestURI);
        sendResponse(exchange, cookie, tokenId);
    }

    private void sendResponse(HttpServerExchange exchange, Cookie cookie, ObjectId tokenId) {
        exchange.setResponseCookie(new CookieImpl(cookie.getName(), cookie.getValue()));
        TOKEN_SERVICE.addExpiration(tokenId);

    }

    private String reorganizeRequestURI(String requestURI) {
        int lastCharacterRequestURI = requestURI.length() - 1;
        if (requestURI.substring(lastCharacterRequestURI).equals("/")) {
            return requestURI.substring(0, lastCharacterRequestURI);
        }
        return requestURI;
    }

    private List<String> getFolders(User user) {
        List<String> folders = new ArrayList<>();
        for (ObjectId folder : user.getFolders()) {
            folders.add(FOLDER_SERVICE.findById(folder).getFolder());
        }
        return folders;
    }
}
