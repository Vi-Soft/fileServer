package com.visoft.files.service;

import com.visoft.files.entity.Token;
import com.visoft.files.service.abstractService.AbstractServiceImpl;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.Instant;

import static com.mongodb.client.model.Filters.eq;
import static com.visoft.files.entity.TokenConst.*;
import static com.visoft.files.repository.Repositories.TOKEN_REPOSITORY;

public class TokenServiceImpl extends AbstractServiceImpl<Token> implements TokenService {

    public TokenServiceImpl() {
        super(TOKEN_REPOSITORY);
    }

    @Override
    public Token findByToken(String token) {
        Bson filter = eq(TOKEN, token);
        return super.getObject(filter);
    }

    @Override
    public Token findByUserId(ObjectId userId) {
        Bson filter = eq(USER_ID, userId);
        return super.getObject(filter);
    }

    @Override
    public boolean isExistsById(ObjectId id) {
        Bson filter = eq(_ID, id);
        return isExists(filter);
    }

    @Override
    public void addExpiration(ObjectId id) {
        update(
                id,
                EXPIRATION,
                Instant.now().plusSeconds(10800L)
        );
    }

    @Override
    public void setExpirationNow(ObjectId id){
        update(
                id,
                EXPIRATION,
                Instant.now()
        );
    }
}
