package com.visoft.file.service.persistance.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.file.service.config.DBConfig;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.persistance.entity.UserConst;

/**
 * Configuration mongo db class for {@link User}
 */
class UserRepository extends AbstractRepository<User> {

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

    UserRepository() {
        super(collection);
    }
}