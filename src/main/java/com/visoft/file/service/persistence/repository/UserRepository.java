package com.visoft.file.service.persistence.repository;

import com.visoft.file.service.persistence.entity.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByLogin(String email);

    boolean existsByLogin(String email);
}
