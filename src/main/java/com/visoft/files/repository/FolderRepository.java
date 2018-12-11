package com.visoft.files.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.files.entity.Folder;
import com.visoft.utils.DBUtils;

import static com.visoft.files.entity.FolderConst.DB;
import static com.visoft.files.entity.FolderConst.FOLDER;

public class FolderRepository extends AbstractRepository<Folder> {

    private static final MongoCollection<Folder> collection = DBUtils.DB
            .getCollection(DB, Folder.class);

    private static final IndexOptions indexOptions = new IndexOptions()
            .unique(true);

    private static final String folderIndex = collection
            .createIndex(Indexes.ascending(FOLDER), indexOptions);

    public FolderRepository() {
        super(collection);
    }
}
