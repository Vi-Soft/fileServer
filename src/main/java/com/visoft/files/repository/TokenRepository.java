package com.visoft.files.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.files.entity.Token;
import com.visoft.utils.DBUtils;

import static com.visoft.files.entity.TokenConst.*;

public class TokenRepository extends AbstractRepository<Token> {

    private static final MongoCollection<Token> collection = DBUtils.DB
            .getCollection(DB, Token.class);

    private static final IndexOptions indexOptions = new IndexOptions()
            .unique(true);

    private static final String tokenIndex = collection
            .createIndex(Indexes.ascending(TOKEN), indexOptions);

    public TokenRepository() {
        super(collection);
    }
}
