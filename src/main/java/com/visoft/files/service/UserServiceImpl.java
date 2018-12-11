package com.visoft.files.service;

import com.networknt.config.Config;
import com.visoft.files.dto.UserCreateDto;
import com.visoft.files.entity.Role;
import com.visoft.files.entity.Token;
import com.visoft.files.entity.User;
import com.visoft.files.service.abstractService.AbstractServiceImpl;
import com.visoft.files.service.util.EncoderService;
import com.visoft.files.service.util.JWTService;
import io.undertow.server.HttpServerExchange;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.visoft.files.entity.UserConst.*;
import static com.visoft.files.repository.Repositories.USER_REPOSITORY;
import static com.visoft.files.service.DI.DependencyInjectionService.FOLDER_SERVICE;
import static com.visoft.files.service.DI.DependencyInjectionService.TOKEN_SERVICE;
import static com.visoft.files.service.ErrorConst.*;
import static com.visoft.files.service.util.SenderService.sendMessage;

public class UserServiceImpl extends AbstractServiceImpl<User> implements UserService {

    public UserServiceImpl() {
        super(USER_REPOSITORY);
    }

    @Override
    public User findByLoginAndPassword(String login, String password) {
        Bson filter = and(
                eq(DELETED, false),
                eq(LOGIN, login),
                eq(PASSWORD, EncoderService.getEncode(password)));
        return super.getObject(filter);
    }

    @Override
    public String create(HttpServerExchange exchange) {
        UserCreateDto dto = getRequestBody(exchange);
        if (dto == null) {
            return sendMessage(exchange, JSON_NOT_CORRECT);
        }
        String validateResult = validate(dto);
        if (validateResult != null) {
            return sendMessage(exchange, validateResult);
        }
        List<ObjectId> folders = new ArrayList<>();
        for (String folder : dto.getFolders()) {
            folders.add(new ObjectId(folder));
        }
        if (isExistsByLogin(dto.getLogin())) {
            return sendMessage(exchange, LOGIN_EXISTS);
        }
        User user = new User(dto.getLogin(), EncoderService.getEncode(dto.getPassword()), Role.USER, folders);
        create(user);
        Token token = new Token(JWTService.genearete(ObjectId.get().toString()), user.getId());
        TOKEN_SERVICE.create(token);
        return "!!!!!";
    }

    private static UserCreateDto getRequestBody(HttpServerExchange exchange) {
        exchange.startBlocking();
        InputStream is = exchange.getInputStream();
        String s = (new Scanner(is, "UTF-8")).useDelimiter("\\A").next();
        try {
            return Config.getInstance().getMapper().readValue(s, UserCreateDto.class);
        } catch (IOException e) {
            return null;
        }
    }

    private static String validate(UserCreateDto dto) {
        String login = dto.getLogin();
        String password = dto.getPassword();
        List<String> folders = dto.getFolders();
        if (login == null || login.isEmpty()) {
            return LOGIN_NOT_CORRECT;
        }
        if (password == null || password.isEmpty()) {
            return PASSWORD_NOT_CORRECT;
        }
        if (folders == null || folders.isEmpty()) {
            return FOLDERS_NOT_CORRECT;
        }
        if (new HashSet<>(folders).size() != folders.size()) {
            return FOLDERS_EQUALS;
        }
        for (String folder : folders) {
            if (!FOLDER_SERVICE.existsFolder(new ObjectId(folder))) {
                return FOLDERS_NOT_EXISTS;
            }
        }
        return null;
    }

    private boolean isExistsByLogin(String login) {
        Bson filter = eq(LOGIN, login);
        return isExists(filter);
    }


}
