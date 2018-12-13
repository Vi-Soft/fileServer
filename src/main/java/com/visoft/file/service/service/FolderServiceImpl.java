package com.visoft.file.service.service;

import com.networknt.handler.util.Exchange;
import com.visoft.file.service.entity.*;
import com.visoft.file.service.repository.Repositories;
import com.visoft.file.service.service.abstractService.AbstractServiceImpl;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.visoft.file.service.service.DI.DependencyInjectionService.TOKEN_SERVICE;
import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
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
            sendMessage(exchange, ErrorConst.NO_COOKIE);
        } else {
            Token token = TOKEN_SERVICE.findByToken(cookie.getValue());
            if (token == null || token.getExpiration().toEpochMilli() < Instant.now().toEpochMilli()) {
                sendMessage(exchange, ErrorConst.TOKEN_NOT_FOUND);
            } else {
                String id = Exchange.queryParams().queryParam(exchange, "id").orElse("");
                ObjectId folderId = new ObjectId(id);
                List<User> users = USER_SERVICE.findAll();
                for (User user : users) {
                    List<ObjectId> folders = user.getFolders();
                    folders.remove(folderId);
                    USER_SERVICE.update(user.getId(), UserConst.FOLDERS, user.getFolders());
                    //TODO delete folder on filesystem
                }
            }
        }
    }
}
