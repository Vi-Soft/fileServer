package com.visoft.file.service.service.token;


import com.visoft.file.service.persistence.entity.Token;
import com.visoft.file.service.persistence.entity.user.User;

import java.time.Instant;
import java.util.Optional;

public interface TokenService {

    Optional<Token> findByToken(String token);

    Optional<Token> findByUser(User user);

    Token save(Token token);

    long delete(Token token);

    long deleteByUser(User user);

    long delete(String id);

    void deleteTokenByUserId(String userId);

    long deleteToken(Token token);

    Token createToken(User user);

    Token create(Token token);

    Instant makeExpirationPoint();
}
