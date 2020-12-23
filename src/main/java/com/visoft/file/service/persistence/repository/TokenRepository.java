package com.visoft.file.service.persistence.repository;

import com.visoft.file.service.persistence.entity.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {

    Optional<Token> findByToken(String token);

    Optional<Token> findByUserId(String userId);

    void deleteByUserId(String userId);
}
