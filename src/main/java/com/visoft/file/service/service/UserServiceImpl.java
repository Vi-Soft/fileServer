package com.visoft.file.service.service;

import com.networknt.config.Config;
import com.visoft.file.service.dto.UserCreateDto;
import com.visoft.file.service.entity.*;
import com.visoft.file.service.repository.Repositories;
import com.visoft.file.service.service.util.EncoderService;
import com.visoft.file.service.service.util.JWTService;
import com.visoft.file.service.service.util.SenderService;
import com.visoft.file.service.entity.Role;
import com.visoft.file.service.entity.Token;
import com.visoft.file.service.entity.User;
import com.visoft.file.service.service.abstractService.AbstractServiceImpl;
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
import static com.visoft.file.service.service.DI.DependencyInjectionService.FOLDER_SERVICE;
import static com.visoft.file.service.service.DI.DependencyInjectionService.TOKEN_SERVICE;
import static com.visoft.file.service.service.ErrorConst.*;

public class UserServiceImpl extends AbstractServiceImpl<User> implements UserService {

    public UserServiceImpl() {
        super(Repositories.USER_REPOSITORY);
    }

    @Override
    public User findByLoginAndPassword(String login, String password) {
        Bson filter = and(
                eq(GeneralConst.DELETED, false),
                eq(UserConst.LOGIN, login),
                eq(UserConst.PASSWORD, EncoderService.getEncode(password)));
        return super.getObject(filter);
    }

    @Override
    public String create(HttpServerExchange exchange) {
        UserCreateDto dto = getRequestBody(exchange);
        if (dto == null) {
            return SenderService.sendMessage(exchange, JSON_NOT_CORRECT);
        }
        String validateResult = validate(dto);
        if (validateResult != null) {
            return SenderService.sendMessage(exchange, validateResult);
        }
        List<ObjectId> folders = new ArrayList<>();
        for (String folder : dto.getFolders()) {
            folders.add(new ObjectId(folder));
        }
        if (isExistsByLogin(dto.getLogin())) {
            return SenderService.sendMessage(exchange, LOGIN_EXISTS);
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
        Bson filter = eq(UserConst.LOGIN, login);
        return isExists(filter);
    }


}
