package com.visoft.files.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.files.entity.User;
import com.visoft.utils.DBUtils;

import static com.visoft.files.entity.UserConst.DB;
import static com.visoft.files.entity.UserConst.LOGIN;

public class UserRepository extends AbstractRepository<User> {

    private static final MongoCollection<User> collection = DBUtils.DB
            .getCollection(DB, User.class);

    private static final IndexOptions indexOptions = new IndexOptions()
            .unique(true);

    private static final String loginIndex = collection
            .createIndex(Indexes.ascending(LOGIN), indexOptions);

    public UserRepository() {
        super(collection);
    }
}
