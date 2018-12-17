package com.visoft.file.service.web.handler;

import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.service.util.PageService;
import com.visoft.file.service.web.security.AuthenticatedUser;
import com.visoft.file.service.web.security.SecurityHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.visoft.file.service.persistance.entity.Role.USER;
import static com.visoft.file.service.service.DI.DependencyInjectionService.FOLDER_SERVICE;

public class FileResourceHandler extends ResourceHandler {

    FileResourceHandler(ResourceManager resourceSupplier) {
        super(resourceSupplier);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        AuthenticatedUser authenticatedUser = SecurityHandler.authenticatedUser;
        User user = authenticatedUser.getUser();
        String requestURI = exchange.getRequestURI();
        if (requestURI.equals("/")) {
            sendResponse(exchange, user
            );
        } else {
            if (user.getRole().equals(USER)) {
                requestURI = reorganizeRequestURI(requestURI);
                if (!getFolders(user).contains(requestURI)) {
                    sendResponse(exchange, user);
                } else {
                    sendResponse(exchange, requestURI);
                }
            } else {
                sendResponse(exchange, requestURI);
            }
        }
    }

    private void sendResponse(HttpServerExchange exchange, User user) {
        if (user.getRole().equals(USER)) {
            PageService.getMainUserHtml(exchange, getFolders(user));
        } else {
            PageService.getMainUserHtml(exchange, getFolders(user));
        }
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
