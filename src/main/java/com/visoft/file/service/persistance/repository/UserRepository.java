package com.visoft.file.service.persistance.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.file.service.config.DBConfig;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.persistance.entity.UserConst;

class UserRepository extends AbstractRepository<User> {

    private static final MongoCollection<User> collection = DBConfig.DB
            .getCollection(UserConst.DB, User.class);

    private static final IndexOptions indexOptions = new IndexOptions()
            .unique(true);

    private static final String loginIndex = collection
            .createIndex(Indexes.ascending(UserConst.LOGIN), indexOptions);

    UserRepository() {
        super(collection);
    }
}