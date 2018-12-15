package com.visoft.file.service.service;

import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.service.abstractService.AbstractService;
import io.undertow.server.HttpServerExchange;
import org.bson.types.ObjectId;

public interface FolderService extends AbstractService<Folder> {

    boolean existsFolder(ObjectId folder);

    void deleteFolder(HttpServerExchange httpServerExchange);
}
