package com.visoft.file.service.service.user;

import com.visoft.file.service.dto.user.UserCreateDto;
import com.visoft.file.service.dto.user.UserOutcomeDto;
import com.visoft.file.service.dto.user.UserUpdateDto;
import com.visoft.file.service.persistence.entity.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    Optional<User> findById(String id);

    List<UserOutcomeDto> findAll();

    void delete(String id);

    void recovery(String id);

    UserOutcomeDto create(UserCreateDto userCreateDto);

    UserOutcomeDto update(UserUpdateDto userUpdateDto);

    boolean isExistsByLogin(String login);

    //User create(User user);

    Optional<User> findByLogin(String login);


}
