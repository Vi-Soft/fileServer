package com.visoft.file.service.service;

import com.networknt.handler.util.Exchange;
import com.visoft.file.service.persistance.entity.*;
import com.visoft.file.service.persistance.repository.Repositories;
import com.visoft.file.service.service.abstractService.AbstractServiceImpl;
import com.visoft.file.service.service.util.JsonService;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.visoft.file.service.persistance.entity.Role.USER;
import static com.visoft.file.service.service.DI.DependencyInjectionService.TOKEN_SERVICE;
import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
import static com.visoft.file.service.service.ErrorConst.FORBIDDEN;
import static com.visoft.file.service.service.ErrorConst.UNAUTHORIZED;
import static com.visoft.file.service.service.util.SenderService.sendMessage;

public class FolderServiceImpl extends AbstractServiceImpl<Folder> implements FolderService {

    public FolderServiceImpl() {
        super(Repositories.FOLDER_REPOSITORY);
    }

    @Override
    public boolean existsFolder(ObjectId folder) {
        Bson filter = eq(GeneralConst._ID, folder);
        return isExists(filter);
    }

    @Override
    public void deleteFolder(HttpServerExchange exchange) {
        Cookie cookie = exchange.getRequestCookies().get("token");
        if (cookie == null) {
            exchange.setStatusCode(UNAUTHORIZED);
        } else {
            Token token = TOKEN_SERVICE.findByToken(cookie.getValue());
            if (token == null || token.getExpiration().toEpochMilli() < Instant.now().toEpochMilli()) {
                exchange.setStatusCode(UNAUTHORIZED);
            } else {
                User user = USER_SERVICE.findByIdNotDeleted(token.getUserId());
                if (user == null || !user.getRole().equals(USER)) {
                    exchange.setStatusCode(FORBIDDEN);
                }
                String id = Exchange.queryParams().queryParam(exchange, "id").orElse("");
                ObjectId folderId = new ObjectId(id);
                List<User> users = USER_SERVICE.findAll();
                for (User currentUser : users) {
                    List<ObjectId> folders = currentUser.getFolders();
                    folders.remove(folderId);
                    USER_SERVICE.update(currentUser.getId(), UserConst.FOLDERS, currentUser.getFolders());
                    //TODO delete folder on filesystem
                }
            }
        }
    }

    @Override
    public void findAllFolders(HttpServerExchange exchange) {
        Cookie cookie = exchange.getRequestCookies().get("token");
        if (cookie == null) {
            exchange.setStatusCode(UNAUTHORIZED);
        } else {
            Token token = TOKEN_SERVICE.findByToken(cookie.getValue());
            if (token == null || token.getExpiration().toEpochMilli() < Instant.now().toEpochMilli()) {
                exchange.setStatusCode(UNAUTHORIZED);
            } else {
                User user = USER_SERVICE.findByIdNotDeleted(token.getUserId());
                if (user == null || !user.getRole().equals(USER)) {
                    exchange.setStatusCode(FORBIDDEN);
                }
                sendMessage(exchange, JsonService.toJson(getIds(findAll())));
            }
        }
    }

//    @Override
//    public void findById(HttpServerExchange exchange){
//        Cookie cookie = exchange.getRequestCookies().get("token");
//        if (cookie == null) {
//            exchange.setStatusCode(UNAUTHORIZED);
//        } else {
//            Token token = TOKEN_SERVICE.findByToken(cookie.getValue());
//            if (token == null || token.getExpiration().toEpochMilli() < Instant.now().toEpochMilli()) {
//                exchange.setStatusCode(UNAUTHORIZED);
//            } else {
//                User user = USER_SERVICE.findByIdNotDeleted(token.getUserId());
//                if (user == null || !user.getRole().equals(USER)) {
//                    exchange.setStatusCode(FORBIDDEN);
//                }
//                String id = Exchange.queryParams().queryParam(exchange, "id").orElse("");
//                ObjectId folderId = new ObjectId(id);
//                sendMessage(exchange, JsonService.toJson(getIds(findById(folderId))));
//            }
//        }
//    }

    private List<String> getIds(List<Folder> folders) {
        if (folders == null || folders.isEmpty()) {
            return null;
        }
        List<String> folderIds = new ArrayList<>();
        for (Folder folder : folders) {
            folderIds.add(folder.getId().toString());
        }
        return folderIds;
    }
}
