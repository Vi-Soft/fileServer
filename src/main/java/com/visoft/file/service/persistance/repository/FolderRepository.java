package com.visoft.file.service.persistance.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.file.service.config.DBConfig;
import com.visoft.file.service.dto.folder.FolderFindDto;
import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.FolderConst;
import com.visoft.file.service.util.pageable.Page;
import com.visoft.file.service.util.pageable.Pageable;
import com.visoft.file.service.util.pageable.Sort;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Configuration mongo db class for {@link Folder}
 */
public class FolderRepository extends AbstractRepository<Folder> {

    public Folder findByFolder(String folder) {
        Document doc = new Document("folder", Pattern.compile(folder, Pattern.CASE_INSENSITIVE));
        return collection.find(doc).first();
    }

    public List<Folder> findAllByNameContains(FolderFindDto dto, Pageable pageable) {
        Sort sort = pageable.getSort();
        Page page = pageable.getPage();

        BasicDBObject sorting;

        if (!sort.getColumn().isEmpty()) {
            sorting = new BasicDBObject(sort.getColumn(), sort.getDirection().isDescending() ? -1 : 1);
        } else {
            sorting = new BasicDBObject();
        }

        Document query = new Document(
            "$and",
            Arrays.asList(
                new Document("folder", Pattern.compile(dto.getFolder(), Pattern.CASE_INSENSITIVE)),
                new Document("projectName", Pattern.compile(dto.getProjectName(), Pattern.CASE_INSENSITIVE)),
                new Document("taskName", Pattern.compile(dto.getTaskName(), Pattern.CASE_INSENSITIVE))
            )
        );

        return StreamSupport
            .stream(
                collection
                    .find(query)
                    .sort(sorting)
                    .skip(page.getNumber() * page.getSize() )
                    .limit(page.getSize())
                    .spliterator(),
                false)
            .collect(Collectors.toList());
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