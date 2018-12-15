package com.visoft.file.service.service;

import com.visoft.file.service.persistance.entity.GeneralConst;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.TokenConst;
import com.visoft.file.service.persistance.repository.Repositories;
import com.visoft.file.service.service.abstractService.AbstractServiceImpl;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.Instant;

import static com.mongodb.client.model.Filters.eq;

public class TokenServiceImpl extends AbstractServiceImpl<Token> implements TokenService {

    public TokenServiceImpl() {
        super(Repositories.TOKEN_REPOSITORY);
    }

    @Override
    public Token findByToken(String token) {
        Bson filter = eq(TokenConst.TOKEN, token);
        return super.getObject(filter);
    }

    @Override
    public Token findByUserId(ObjectId userId) {
        Bson filter = eq(TokenConst.USER_ID, userId);
        return super.getObject(filter);
    }

    @Override
    public boolean isExistsById(ObjectId id) {
        Bson filter = eq(GeneralConst._ID, id);
        return isExists(filter);
    }

    @Override
    public void addExpiration(ObjectId id) {
        update(
                id,
                TokenConst.EXPIRATION,
                Instant.now().plusSeconds(10800L)
        );
    }

    @Override
    public void setExpirationNow(ObjectId id){
        update(
                id,
                TokenConst.EXPIRATION,
                Instant.now()
        );
    }
}
