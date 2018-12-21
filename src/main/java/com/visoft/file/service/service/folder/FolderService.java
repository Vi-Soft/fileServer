package com.visoft.file.service.service.folder;

import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.service.abstractService.AbstractService;
import io.undertow.server.HttpServerExchange;
import org.bson.types.ObjectId;

import java.util.List;

public interface FolderService extends AbstractService<Folder> {

    void findById(HttpServerExchange exchange);

    boolean existsFolder(ObjectId folder);

    void delete(HttpServerExchange httpServerExchange);

    void findAll(HttpServerExchange exchange);

    List<String> getIdsFromObjectId(List<ObjectId> folders);

    List<ObjectId> getIdsFromStrings(List<String> ids);
}