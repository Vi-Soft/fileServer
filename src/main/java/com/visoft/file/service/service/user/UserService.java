package com.visoft.file.service.service.user;

import com.visoft.file.service.persistence.entity.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    Optional<User> findById(String id);

    boolean isExistsByLogin(String login);

    User create(User user);

    Optional<User> findByLogin(String login);
}
