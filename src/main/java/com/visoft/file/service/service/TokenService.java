package com.visoft.file.service.service;

import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.service.abstractService.AbstractService;
import org.bson.types.ObjectId;

public interface TokenService extends AbstractService<Token> {

    Token findByToken(String token);

    Token findByUserId(ObjectId userId);

    void addExpiration(ObjectId id);

    boolean isExistsById(ObjectId id);

    void setExpirationNow(ObjectId id);
}
