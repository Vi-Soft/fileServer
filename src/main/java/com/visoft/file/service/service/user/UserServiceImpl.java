package com.visoft.file.service.service.user;

import com.networknt.config.Config;
import com.visoft.file.service.dto.user.UserCreateDto;
import com.visoft.file.service.dto.user.UserOutcomeDto;
import com.visoft.file.service.dto.user.UserUpdateDto;
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
import static com.visoft.file.service.service.util.RequestService.getIdFromRequest;
import static com.visoft.file.service.service.util.SenderService.sendMessage;
import static com.visoft.file.service.service.util.SenderService.sendStatusCode;

public class UserServiceImpl extends AbstractServiceImpl<User> implements UserService {

    public UserServiceImpl() {
        super(Repositories.USER_REPOSITORY);
    }

    @Override
    public void update(HttpServerExchange exchange) {
        UserUpdateDto dto = getUpdateUserRequestBody(exchange);
        if (!validate(dto)) {
            sendStatusCode(exchange, BAD_REQUEST);
        } else {
            User user = findUserNotAdmin(new ObjectId(dto.getId()));
            if (user == null) {
                sendStatusCode(exchange, BAD_REQUEST);
            }
            List<ObjectId> folders = FOLDER_SERVICE.getIdsFromStrings(dto.getFolders());
            if (isExistsByLogin(dto.getLogin())) {
                sendStatusCode(exchange, BAD_REQUEST);
                sendMessage(exchange, LOGIN_EXISTS);
            } else {
                user.setLogin(dto.getLogin());
                user.setPassword(getEncode(dto.getPassword()));
                user.setFolders(folders);
                update(user, user.getId());
            }
        }
    }

    @Override
    public void findById(HttpServerExchange exchange) {
        User user = findUserNotAdmin(getIdFromRequest(exchange));
        if (user == null) {
            sendStatusCode(exchange, NOT_FOUND);
        } else {
            sendMessage(
                    exchange,
                    toJson(new UserOutcomeDto(user)
                    )
            );
        }
    }

    @Override
    public void create(HttpServerExchange exchange) {
        UserCreateDto dto = getCreateUserRequestBody(exchange);
        if (!validate(dto)) {
            sendStatusCode(exchange, BAD_REQUEST);
        } else {
            List<ObjectId> folders = FOLDER_SERVICE.getIdsFromStrings(dto.getFolders());
            if (isExistsByLogin(dto.getLogin())) {
                sendMessage(exchange, LOGIN_EXISTS);
                sendStatusCode(exchange, BAD_REQUEST);
            }
            User createdUser = new User(dto.getLogin(), getEncode(
                    dto.getPassword()),
                    USER, folders
            );
            create(createdUser);
            Token createdUserToken = new Token(
                    generate(ObjectId.get()),
                    createdUser.getId());
            TOKEN_SERVICE.create(createdUserToken);
            sendStatusCode(exchange, CREATE);
        }
    }

    @Override
    public void delete(HttpServerExchange exchange) {
        ObjectId userId = getIdFromRequest(exchange);
        User currentUser = findByIdNotDeleted(getIdFromRequest(exchange));
        if (currentUser == null || currentUser.getRole().equals(ADMIN)) {
            sendStatusCode(exchange, FORBIDDEN);
        } else {
            update(userId, DELETED, true);
        }
    }

    @Override
    public void recovery(HttpServerExchange exchange) {
        ObjectId userId = getIdFromRequest(exchange);
        User currentUser = USER_SERVICE.findById(userId);
        if (currentUser == null || currentUser.getDeleted().equals(false) || currentUser.getRole().equals(ADMIN)) {
            sendStatusCode(exchange, FORBIDDEN);
        } else {
            update(
                    userId,
                    DELETED,
                    false
            );
        }
    }

    @Override
    public void findAll(HttpServerExchange exchange) {
        Bson filter = eq(ROLE, USER.toString());
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

    private User findUserNotAdmin(ObjectId id) {
        Bson filter = and(
                eq(_ID, id),
                eq(ROLE, USER.toString())
        );
        return getObject(filter);
    }

    private UserCreateDto getCreateUserRequestBody(HttpServerExchange exchange) {
        exchange.startBlocking();
        InputStream is = exchange.getInputStream();
        String s = (new Scanner(is, "UTF-8")).useDelimiter("\\A").next();
        try {
            return Config.getInstance().getMapper().readValue(s, UserCreateDto.class);
        } catch (IOException e) {
            return null;
        }
    }

    private UserUpdateDto getUpdateUserRequestBody(HttpServerExchange exchange) {
        exchange.startBlocking();
        InputStream is = exchange.getInputStream();
        String s = (new Scanner(is, "UTF-8")).useDelimiter("\\A").next();
        try {
            return Config.getInstance().getMapper().readValue(s, UserUpdateDto.class);
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

    private boolean validate(UserCreateDto dto) {
        if (dto == null) {
            return false;
        }
        return validate(dto.getLogin(), dto.getPassword(), dto.getFolders());
    }

    private boolean validate(UserUpdateDto dto) {
        if (dto == null) {
            return false;
        }
        if (dto.getId() == null || dto.getId().isEmpty()) {
            return false;
        }
        return validate(dto.getLogin(), dto.getPassword(), dto.getFolders());
    }

    private boolean validate(String login, String password, List<String> folders) {
        if (login == null || login.isEmpty()) {
            return false;
        }
        if (password == null || password.isEmpty()) {
            return false;
        }
        if (folders == null || folders.isEmpty()) {
            return false;
        }
        if (new HashSet<>(folders).size() != folders.size()) {
            return false;
        }
        for (String folder : folders) {
            if (!FOLDER_SERVICE.existsFolder(new ObjectId(folder))) {
                return false;
            }
        }
        return true;
    }

    private boolean isExistsByLogin(String login) {
        return isExists(eq(UserConst.LOGIN, login));
    }
}