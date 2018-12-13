package com.visoft.file.service.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.file.service.entity.User;
import com.visoft.file.service.entity.UserConst;
import com.visoft.file.service.util.DBUtil;

public class UserRepository extends AbstractRepository<User> {

    private static final MongoCollection<User> collection = DBUtil.DB
            .getCollection(UserConst.DB, User.class);

    private static final IndexOptions indexOptions = new IndexOptions()
            .unique(true);

    private static final String loginIndex = collection
            .createIndex(Indexes.ascending(UserConst.LOGIN), indexOptions);

    public UserRepository() {
        super(collection);
    }
}
