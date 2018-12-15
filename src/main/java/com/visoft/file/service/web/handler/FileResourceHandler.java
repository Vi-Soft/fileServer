package com.visoft.file.service.web.handler;

import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.service.util.PageService;
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

import static com.visoft.file.service.persistance.entity.Role.USER;
import static com.visoft.file.service.service.DI.DependencyInjectionService.*;
import static com.visoft.file.service.service.ErrorConst.UNAUTHORIZED;

public class FileResourceHandler extends ResourceHandler {

    FileResourceHandler(ResourceManager resourceSupplier) {
        super(resourceSupplier);
    }

    private boolean priviousCookie;
    private String previousUri;


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Cookie cookie = exchange.getRequestCookies().get("token");
        if (cookie == null) {
            exchange.setStatusCode(UNAUTHORIZED);
        } else {
            Token token = TOKEN_SERVICE.findByToken(cookie.getValue());
            if (token == null || token.getExpiration().toEpochMilli() < Instant.now().toEpochMilli()) {
                exchange.setStatusCode(UNAUTHORIZED);
            } else {
                ObjectId tokenId = token.getId();
                User user = USER_SERVICE.findById(token.getUserId());
                String requestURI = exchange.getRequestURI();
                priviousCookie = true;
                previousUri = requestURI;
                if (requestURI.equals("/")) {
                    sendResponse(exchange, cookie, user, tokenId);
                } else {
                    if (user.getRole().equals(USER)){
                        requestURI = reorganizeRequestURI(requestURI);
                        if (!getFolders(user).contains(requestURI)) {
                            sendResponse(exchange, cookie, user, tokenId);
                        } else {
                            sendResponse(exchange, cookie, requestURI, tokenId);
                        }
                    }else {
                        sendResponse(exchange, cookie, requestURI, tokenId);
                    }
                }
            }

        }

    }

    private void sendResponse(HttpServerExchange exchange, Cookie cookie, User user, ObjectId tokenId) {
        if (user.getRole().equals(USER)) {
            PageService.getMainUserHtml(exchange, getFolders(user));
        } else {
            PageService.getMainUserHtml(exchange, getFolders(user));
        }

        sendResponse(exchange, cookie, tokenId);
    }

    private void sendResponse(HttpServerExchange exchange, Cookie cookie, String requestURI, ObjectId tokenId) throws IOException {
        PageService.getFolderUserHtml(exchange, requestURI);
        sendResponse(exchange, cookie, tokenId);
    }

    private void sendResponse(HttpServerExchange exchange, Cookie cookie, ObjectId tokenId) {
        addCookie(exchange, cookie);
        TOKEN_SERVICE.addExpiration(tokenId);

    }

    private void addCookie(HttpServerExchange exchange, Cookie cookie) {
        CookieImpl cookie1 = new CookieImpl(cookie.getName(), cookie.getValue());
        exchange.setResponseCookie(cookie1);
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
        if (user.getRole().equals(USER)) {
            List<ObjectId> userFolders = user.getFolders();
            if (userFolders != null) {
                for (ObjectId id : userFolders) {
                    Folder folderInDB = FOLDER_SERVICE.findById(id);
                    if (folderInDB != null) {
                        folders.add(folderInDB.getFolder());
                    }
                }
            }
        } else {
            List<Folder> all = FOLDER_SERVICE.getListObject(null);
            if (all != null) {
                for (Folder folder : all) {
                    folders.add(folder.getFolder());
                }
            }
        }
        return folders;
    }
}
