package com.visoft.files.service;

import com.visoft.files.entity.Token;
import com.visoft.files.service.abstractService.AbstractService;
import org.bson.types.ObjectId;

public interface TokenService extends AbstractService<Token> {

    Token findByToken(String token);

    Token findByUserId(ObjectId userId);

    void addExpiration(ObjectId id);

    boolean isExistsById(ObjectId id);
}
