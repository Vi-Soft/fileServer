package com.visoft.file.service.service;

import com.visoft.file.service.entity.User;
import com.visoft.file.service.service.abstractService.AbstractService;
import io.undertow.server.HttpServerExchange;

public interface UserService extends AbstractService<User> {

    String create(HttpServerExchange exchange);

    User findByLoginAndPassword(String login, String password);
}
