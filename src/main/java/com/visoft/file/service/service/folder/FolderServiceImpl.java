package com.visoft.file.service.service.folder;

import com.visoft.file.service.dto.folder.FolderOutcomeDto;
import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.GeneralConst;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.persistance.entity.UserConst;
import com.visoft.file.service.persistance.repository.Repositories;
import com.visoft.file.service.service.abstractService.AbstractServiceImpl;
import com.visoft.file.service.service.util.FileSystemService;
import io.undertow.server.HttpServerExchange;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
import static com.visoft.file.service.service.ErrorConst.NOT_FOUND;
import static com.visoft.file.service.service.util.JsonService.toJson;
import static com.visoft.file.service.service.util.PropertiesService.getRootPath;
import static com.visoft.file.service.service.util.RequestService.getIdFromRequest;
import static com.visoft.file.service.service.util.SenderService.send;

public class FolderServiceImpl extends AbstractServiceImpl<Folder> implements FolderService {

    public FolderServiceImpl() {
        super(Repositories.FOLDER_REPOSITORY);
    }

    @Override
    public void create(
            String folder,
            String projectName,
            String taskName
    ) {
        create(
                new Folder(
                        folder,
                        projectName,
                        taskName
                )
        );
    }

    @Override
    public void findById(HttpServerExchange exchange) {
        Folder folder = findById(getIdFromRequest(exchange));
        if (folder == null) {
            send(exchange, NOT_FOUND);
        } else {
            send(
                    exchange,
                    toJson(new FolderOutcomeDto(folder))
            );
        }
    }

    @Override
    public boolean existsFolder(ObjectId id) {
        return isExists(
                eq(
                        GeneralConst._ID,
                        id
                )
        );
    }

    @Override
    public void delete(HttpServerExchange exchange) {
        List<User> users = USER_SERVICE.findAll();
        ObjectId folderId = getIdFromRequest(exchange);
        Folder folder = findById(folderId);
        if (folder != null) {
            removeFolderInUsers(users, folderId);
            super.delete(folderId);
            String folderPath = folder.getFolder();
            try {
                FileSystemService.delete(getRootPath() + folderPath);
                FileSystemService.delete(getRootPath() + folderPath + ".zip");
                String[] split = folderPath.split("/");
                String companyFolder = split[split.length - 1 - 1];
                FileSystemService.deleteIfEmpty(getRootPath() + "/" + companyFolder);
            } catch (IOException e) {
                send(exchange, NOT_FOUND);
            }
        } else {
            send(exchange, NOT_FOUND);
        }
    }

    private void removeFolderInUsers(List<User> users, ObjectId folderId) {
        for (User currentUser : users) {
            List<ObjectId> folders = currentUser.getFolders();
            if (folders != null) {
                folders.remove(folderId);
                USER_SERVICE.update(currentUser.getId(), UserConst.FOLDERS, currentUser.getFolders());
            }
        }
    }

    @Override
    public void findAll(HttpServerExchange exchange) {
        send(
                exchange,
                toJson(
//                        getIdsFromFolders(
                        findAll()
                                .parallelStream()
                                .map(FolderOutcomeDto::new)
                                .collect(Collectors.toList())
//                        )
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
            if (existsFolder(new ObjectId(objectId))) {
                folderIds.add(new ObjectId(objectId));
            } else {
                return null;
            }
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