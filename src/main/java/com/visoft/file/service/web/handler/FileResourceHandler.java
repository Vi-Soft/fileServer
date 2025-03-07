package com.visoft.file.service.web.handler;

import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.service.util.PageService;
import com.visoft.file.service.web.security.AuthenticatedUser;
import com.visoft.file.service.web.security.SecurityHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.apache.commons.io.FilenameUtils;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.visoft.file.service.persistance.entity.Role.ADMIN;
import static com.visoft.file.service.persistance.entity.Role.USER;
import static com.visoft.file.service.service.DI.DependencyInjectionService.FOLDER_SERVICE;
import static com.visoft.file.service.service.ErrorConst.FORBIDDEN;
import static com.visoft.file.service.service.ErrorConst.USER_HAS_NO_PERMISSIONS_URI;
import static com.visoft.file.service.service.util.SenderService.send;
import static com.visoft.file.service.service.util.SenderService.sendInfo;

public class FileResourceHandler extends ResourceHandler {

    FileResourceHandler(ResourceManager resourceSupplier) {
        super(resourceSupplier);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        AuthenticatedUser authenticatedUser = SecurityHandler.authenticatedUser;
        User user = authenticatedUser.getUser();
        String requestURI = exchange.getRequestURI();
        String extension = FilenameUtils.getExtension(requestURI);
        if (extension != null && !extension.isEmpty()) {
            if (user.getRole().equals(ADMIN)) {
                super.handleRequest(exchange);
            } else {
                if (haveAccess(user, requestURI)) {
                    super.handleRequest(exchange);
                } else {
                    sendInfo(USER_HAS_NO_PERMISSIONS_URI, requestURI);
                    send(exchange, FORBIDDEN);
                }
            }
        } else {
            if (requestURI.equals("/")) {
                sendResponse(exchange, user);
            } else {
                if (user.getRole().equals(USER)) {
                    requestURI = reorganizeRequestURI(requestURI);
                }
                if (!haveAccess(user, requestURI)) {
                    sendResponse(exchange, user);
                } else {
                    sendResponse(exchange, requestURI);
                }
            }
        }
    }

    private boolean haveAccess(User user, String requestURI) {
        if (user.getRole().equals(ADMIN)) {
            return true;
        }

        try {
            requestURI = URLDecoder.decode(requestURI, "UTF-8");
            List<Folder> folders = getFolders(user);
            if (!folders.isEmpty()) {
                for (Folder folder : getFolders(user)) {
                    if (requestURI.startsWith(folder.getFolder())) {
                        return true;
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Requested url is incorrect");
        }

        return false;
    }

    private void sendResponse(HttpServerExchange exchange, User user) {
        PageService.getMainUserHtml(exchange, getFolders(user));
    }

    private void sendResponse(HttpServerExchange exchange, String requestURI) throws IOException {
        PageService.getFolderUserHtml(exchange, requestURI);
    }

    private String reorganizeRequestURI(String requestURI) {
        int lastCharacterRequestURI = requestURI.length() - 1;
        if (requestURI.substring(lastCharacterRequestURI).equals("/")) {
            return requestURI.substring(0, lastCharacterRequestURI);
        }
        return requestURI;
    }

    private List<Folder> getFolders(User user) {
        List<Folder> folders = new ArrayList<>();
        if (user.getRole().equals(USER)) {
            List<ObjectId> userFolders = user.getFolders();
            if (userFolders != null) {
                for (ObjectId id : userFolders) {
                    Folder folderInDB = FOLDER_SERVICE.findById(id);
                    if (folderInDB != null) {
                        folders.add(folderInDB);
                    }
                }
            }
        } else {
            List<Folder> all = FOLDER_SERVICE.findAll();
            if (all != null) {
                folders.addAll(all);
            }
        }
        return folders;
    }
}