package com.visoft.file.service.service.folder;

import com.visoft.file.service.dto.folder.FolderOutcomeDto;
import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.GeneralConst;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.persistance.entity.UserConst;
import com.visoft.file.service.persistance.repository.Repositories;
import com.visoft.file.service.service.abstractService.AbstractServiceImpl;
import io.undertow.server.HttpServerExchange;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
import static com.visoft.file.service.service.ErrorConst.NOT_FOUND;
import static com.visoft.file.service.service.util.JsonService.toJson;
import static com.visoft.file.service.service.util.RequestService.getIdFromRequest;
import static com.visoft.file.service.service.util.SenderService.sendMessage;
import static com.visoft.file.service.service.util.SenderService.sendStatusCode;

public class FolderServiceImpl extends AbstractServiceImpl<Folder> implements FolderService {

    public FolderServiceImpl() {
        super(Repositories.FOLDER_REPOSITORY);
    }

    @Override
    public void create(String folder) {
        super.create(new Folder(folder));
    }

    @Override
    public void findById(HttpServerExchange exchange) {
        Folder folder = findById(getIdFromRequest(exchange));
        if (folder == null) {
            sendStatusCode(exchange, NOT_FOUND);
        } else {
            sendMessage(
                    exchange,
                    toJson(new FolderOutcomeDto(folder))
            );
        }
    }

    @Override
    public boolean existsFolder(ObjectId folder) {
        return isExists(
                eq(
                        GeneralConst._ID,
                        folder
                )
        );
    }

    @Override
    public void delete(HttpServerExchange exchange) {
        List<User> users = USER_SERVICE.findAll();
        for (User currentUser : users) {
            List<ObjectId> folders = currentUser.getFolders();
            folders.remove(getIdFromRequest(exchange));
            USER_SERVICE.update(currentUser.getId(), UserConst.FOLDERS, currentUser.getFolders());
            //TODO delete folder on filesystem
        }
    }

    @Override
    public void findAll(HttpServerExchange exchange) {
        sendMessage(
                exchange,
                toJson(
                        getIdsFromFolders(
                                findAll()
                        )
                )
        );
    }

    @Override
    public List<String> getIdsFromObjectId(List<ObjectId> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        List<String> folderIds = new ArrayList<>();
        for (ObjectId objectId : ids) {
            folderIds.add(objectId.toString());
        }
        return folderIds;
    }

    @Override
    public List<ObjectId> getIdsFromStrings(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        List<ObjectId> folderIds = new ArrayList<>();
        for (String objectId : ids) {
            folderIds.add(new ObjectId(objectId));
        }
        return folderIds;
    }

    private List<String> getIdsFromFolders(List<Folder> folders) {
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