package com.visoft.file.service.persistance.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.file.service.config.DBConfig;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.TokenConst;

class TokenRepository extends AbstractRepository<Token> {

    private static final MongoCollection<Token> collection = DBConfig.DB
            .getCollection(TokenConst.DB, Token.class);

    private static final IndexOptions indexOptions = new IndexOptions()
            .unique(true);

    private static final String tokenIndex = collection
            .createIndex(Indexes.ascending(TokenConst.TOKEN), indexOptions);

    TokenRepository() {
        super(collection);
    }
}