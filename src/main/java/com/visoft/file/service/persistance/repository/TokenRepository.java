package com.visoft.file.service.persistance.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.visoft.file.service.config.DBConfig;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.TokenConst;

/**
 * Configuration mongo db class for {@link Token}
 */
class TokenRepository extends AbstractRepository<Token> {

    /**
     * Set collection name
     */
    private static final MongoCollection<Token> collection = DBConfig.DB
            .getCollection(TokenConst.DB, Token.class);

    /**
     * Set uniq by {@link Token#getId() id}
     */
    private static final IndexOptions indexOptions = new IndexOptions()
            .unique(true);

    /**
     * Set uniq by {@link Token#getToken() token}
     */
    private static final String tokenIndex = collection
            .createIndex(Indexes.ascending(TokenConst.TOKEN), indexOptions);

    TokenRepository() {
        super(collection);
    }
}