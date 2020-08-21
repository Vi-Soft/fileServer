package com.visoft.file.service.service.user;

import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.service.abstractService.AbstractService;
import io.undertow.server.HttpServerExchange;

public interface UserService extends AbstractService<User> {

    void findAll(HttpServerExchange exchange);

//    void createAdmin(UserCreateDto dto);

    void delete(HttpServerExchange exchange);

    void update(HttpServerExchange exchange);

    void findById(HttpServerExchange exchange);

    void createUser(HttpServerExchange exchange);

    User findByLoginAndPassword(String login, String password);

    void recovery(HttpServerExchange exchange);

    boolean isExistsByLogin(String login);
}