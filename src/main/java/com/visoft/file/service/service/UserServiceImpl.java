package com.visoft.file.service.service;

import com.networknt.config.Config;
import com.networknt.handler.util.Exchange;
import com.visoft.file.service.dto.UserCreateDto;
import com.visoft.file.service.dto.UserOutcomeDto;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.persistance.entity.UserConst;
import com.visoft.file.service.persistance.repository.Repositories;
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
import static com.visoft.file.service.persistance.entity.Role.ADMIN;
import static com.visoft.file.service.persistance.entity.Role.USER;
import static com.visoft.file.service.persistance.entity.UserConst.*;
import static com.visoft.file.service.service.DI.DependencyInjectionService.*;
import static com.visoft.file.service.service.ErrorConst.*;
import static com.visoft.file.service.service.util.EncoderService.getEncode;
import static com.visoft.file.service.service.util.JWTService.generate;
import static com.visoft.file.service.service.util.JsonService.toJson;
import static com.visoft.file.service.service.util.SenderService.sendMessage;
import static com.visoft.file.service.service.util.SenderService.sendStatusCode;

public class UserServiceImpl extends AbstractServiceImpl<User> implements UserService {

    public UserServiceImpl() {
        super(Repositories.USER_REPOSITORY);
    }

    @Override
    public void findByIdUser(HttpServerExchange exchange) {
        String id = Exchange.queryParams().queryParam(exchange, "id").orElse("");
        ObjectId userId = new ObjectId(id);
        Bson filter = and(eq(_ID, userId), eq(ROLE, USER.toString()));
        User user = getObject(filter);
        if (user == null) {
            sendStatusCode(exchange, NOT_FOUND);
        } else {
            sendMessage(exchange, toJson(new UserOutcomeDto(
                    user.getId(),
                    user.getLogin(),
                    user.getFolders())));
        }
    }

    @Override
    public void create(HttpServerExchange exchange) {
        UserCreateDto dto = getCreateUserRequestBody(exchange);
        if (dto == null) {
            exchange.setStatusCode(BAD_REQUEST);
        } else {
            String validateResult = validate(dto);
            if (validateResult != null) {
                exchange.setStatusCode(BAD_REQUEST);
            } else {
                List<ObjectId> folders = new ArrayList<>();
                for (String folder : dto.getFolders()) {
                    folders.add(new ObjectId(folder));
                }
                if (isExistsByLogin(dto.getLogin())) {
                    sendMessage(exchange, LOGIN_EXISTS);
                }
                User createdUser = new User(dto.getLogin(), getEncode(dto.getPassword()), USER, folders);
                create(createdUser);
                Token createdUserToken = new Token(generate(ObjectId.get()), createdUser.getId());
                TOKEN_SERVICE.create(createdUserToken);
                exchange.setStatusCode(CREATE);
            }
        }
    }

    @Override
    public void delete(HttpServerExchange exchange) {
        String id = Exchange.queryParams().queryParam(exchange, "id").orElse("");
        ObjectId userId = new ObjectId(id);
        User currentUser = findByIdNotDeleted(userId);
        if (currentUser == null || currentUser.getRole().equals(ADMIN)) {
            exchange.setStatusCode(FORBIDDEN);
        } else {
            update(userId, DELETED, true);
        }
    }

    @Override
    public void recovery(HttpServerExchange exchange) {
        String id = Exchange.queryParams().queryParam(exchange, "id").orElse("");
        ObjectId userId = new ObjectId(id);
        User currentUser = USER_SERVICE.findById(userId);
        if (currentUser == null || currentUser.getDeleted().equals(false) || currentUser.getRole().equals(ADMIN)) {
            exchange.setStatusCode(FORBIDDEN);
        } else {
            update(userId, DELETED, false);
        }
    }

    @Override
    public void findAllUser(HttpServerExchange exchange) {
        Bson filter = eq(UserConst.ROLE, ADMIN);
        sendMessage(
                exchange,
                toJson(
                        getIds(getListObject(filter))
                )
        );
    }

    @Override
    public User findByLoginAndPassword(String login, String password) {
        Bson filter = and(
                eq(DELETED, false),
                eq(UserConst.LOGIN, login),
                eq(UserConst.PASSWORD, getEncode(password)));
        return super.getObject(filter);
    }

    private static UserCreateDto getCreateUserRequestBody(HttpServerExchange exchange) {
        exchange.startBlocking();
        InputStream is = exchange.getInputStream();
        String s = (new Scanner(is, "UTF-8")).useDelimiter("\\A").next();
        try {
            return Config.getInstance().getMapper().readValue(s, UserCreateDto.class);
        } catch (IOException e) {
            return null;
        }
    }

    private List<String> getIds(List<User> users) {
        List<String> ids = new ArrayList<>();
        if (users == null) {
            return null;
        } else {
            for (User user : users) {
                ids.add(user.getId().toString());
            }
        }
        return ids;
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
