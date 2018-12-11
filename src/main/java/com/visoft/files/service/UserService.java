package com.visoft.files.service;

import com.visoft.files.entity.User;
import com.visoft.files.service.abstractService.AbstractService;
import io.undertow.server.HttpServerExchange;

public interface UserService extends AbstractService<User> {

     String create(HttpServerExchange exchange);

    User findByLoginAndPassword(String login, String password);
}
