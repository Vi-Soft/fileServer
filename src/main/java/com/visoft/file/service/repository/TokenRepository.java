package com.visoft.file.service.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.file.service.entity.Token;
import com.visoft.file.service.entity.TokenConst;
import com.visoft.file.service.util.DBUtil;

public class TokenRepository extends AbstractRepository<Token> {

    private static final MongoCollection<Token> collection = DBUtil.DB
            .getCollection(TokenConst.DB, Token.class);

    private static final IndexOptions indexOptions = new IndexOptions()
            .unique(true);

    private static final String tokenIndex = collection
            .createIndex(Indexes.ascending(TokenConst.TOKEN), indexOptions);

    public TokenRepository() {
        super(collection);
    }
}
