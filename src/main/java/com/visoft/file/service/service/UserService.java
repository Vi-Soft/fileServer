package com.visoft.file.service.service;

import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.service.abstractService.AbstractService;
import io.undertow.server.HttpServerExchange;

public interface UserService extends AbstractService<User> {

    void delete(HttpServerExchange exchange);

    void create(HttpServerExchange exchange);

    User findByLoginAndPassword(String login, String password);

    void recovery(HttpServerExchange exchange);
}
