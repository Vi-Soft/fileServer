package com.visoft.file.service.persistance.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.file.service.config.DBConfig;
import com.visoft.file.service.dto.folder.UserFindDto;
import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.persistance.entity.UserConst;
import com.visoft.file.service.util.pageable.Page;
import com.visoft.file.service.util.pageable.PageResult;
import com.visoft.file.service.util.pageable.Pageable;
import com.visoft.file.service.util.pageable.Sort;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Configuration mongo db class for {@link User}
 */
public class UserRepository extends AbstractRepository<User> {

    private final FolderRepository directFolderRepository = Repositories.DIRECT_FOLDER_REPOSITORY;

    /**
     * Set collection name
     */
    private static final MongoCollection<User> collection = DBConfig.DB
            .getCollection(UserConst.DB, User.class);

    /**
     * Set uniq by {@link User#getId() id}
     */
    private static final IndexOptions indexOptions = new IndexOptions()
            .unique(true);

    /**
     * Set uniq by {@link User#getLogin() login}
     */
    private static final String loginIndex = collection
            .createIndex(Indexes.ascending(UserConst.LOGIN), indexOptions);

    public PageResult<User> findAll(UserFindDto dto, Pageable pageable) {
        Sort sort = pageable.getSort();
        Page page = pageable.getPage();

        BasicDBObject sorting = new BasicDBObject();

        if (!sort.getColumn().isEmpty()) {
            sorting = new BasicDBObject(sort.getColumn(), sort.getDirection().isDescending() ? -1 : 1);
        }

        List<Document> filters = new ArrayList<>();

        if (!StringUtils.isEmpty(dto.getDeleted())) {
            filters.add(new Document("deleted", Boolean.parseBoolean(dto.getDeleted())));
        }
        if (!StringUtils.isEmpty(dto.getLogin())) {
            filters.add(new Document("login", Pattern.compile(dto.getLogin(), Pattern.CASE_INSENSITIVE)));
        }
        if (!StringUtils.isEmpty(dto.getRole())) {
            filters.add(new Document("role", Pattern.compile(dto.getRole(), Pattern.CASE_INSENSITIVE)));
        }
        if (!StringUtils.isEmpty(dto.getFolders())) {
            List<ObjectId> folderIds = directFolderRepository.findAllByFolderPattern(dto.getFolders())
                .stream()
                .map(Folder::getId)
                .collect(Collectors.toList());

            filters.add(new Document("folders", new Document("$in", folderIds)));
        }

        Document query = new Document();

        if (!filters.isEmpty()) {
            query = new Document(
                "$and",
                filters
            );
        }

        List<User> data = StreamSupport
            .stream(
                collection
                    .find(query)
                    .sort(sorting)
                    .skip(page.getNumber() * page.getSize())
                    .limit(page.getSize())
                    .spliterator(),
                false)
            .collect(Collectors.toList());

        long total = collection.count(query);

        return new PageResult<>(data, total);
    }

    UserRepository() {
        super(collection);
    }
}