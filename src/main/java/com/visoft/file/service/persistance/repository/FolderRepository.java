package com.visoft.file.service.persistance.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.file.service.config.DBConfig;
import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.FolderConst;
import org.bson.Document;

import java.util.regex.Pattern;

/**
 * Configuration mongo db class for {@link Folder}
 */
public class FolderRepository extends AbstractRepository<Folder> {

    public Folder findByFolder(String folder) {
        Document doc = new Document("folder", Pattern.compile(folder));
        return collection.find(doc).first();
    }

    /**
     * Set collection name
     */
    private static final MongoCollection<Folder> collection = DBConfig.DB
            .getCollection(FolderConst.DB, Folder.class);

    /**
     * Set uniq by {@link Folder#getId() id}
     */
    private static final IndexOptions indexOptions = new IndexOptions()
            .unique(true);

    /**
     * Set uniq by {@link Folder#getFolder() folder}
     */
    private static final String folderIndex = collection
            .createIndex(Indexes.ascending(FolderConst.FOLDER), indexOptions);

    FolderRepository() {
        super(collection);
    }
}