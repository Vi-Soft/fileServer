package com.visoft.files.service;

import com.visoft.files.entity.Folder;
import com.visoft.files.service.abstractService.AbstractService;
import org.bson.types.ObjectId;

public interface FolderService extends AbstractService<Folder> {

    boolean existsFolder(ObjectId folder);
}
