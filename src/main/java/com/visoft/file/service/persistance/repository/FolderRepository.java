package com.visoft.file.service.persistance.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.FolderConst;
import com.visoft.file.service.util.DBUtil;

public class FolderRepository extends AbstractRepository<Folder> {

    private static final MongoCollection<Folder> collection = DBUtil.DB
            .getCollection(FolderConst.DB, Folder.class);

    private static final IndexOptions indexOptions = new IndexOptions()
            .unique(true);

    private static final String folderIndex = collection
            .createIndex(Indexes.ascending(FolderConst.FOLDER), indexOptions);

    public FolderRepository() {
        super(collection);
    }
}