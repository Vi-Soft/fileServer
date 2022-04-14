package com.visoft.file.service.service.user;

import com.networknt.config.Config;
import com.visoft.file.service.dto.user.UserCreateDto;
import com.visoft.file.service.dto.user.UserOutcomeDto;
import com.visoft.file.service.dto.user.UserUpdateDto;
import com.visoft.file.service.persistance.entity.Role;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.persistance.entity.UserConst;
import com.visoft.file.service.persistance.repository.Repositories;
import com.visoft.file.service.service.abstractService.AbstractServiceImpl;
import com.visoft.file.service.web.security.SecurityHandler;
import io.undertow.server.HttpServerExchange;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static com.visoft.file.service.persistance.entity.Role.USER;
import static com.visoft.file.service.persistance.entity.UserConst.DELETED;
import static com.visoft.file.service.persistance.entity.UserConst._ID;
import static com.visoft.file.service.service.DI.DependencyInjectionService.*;
import static com.visoft.file.service.service.ErrorConst.*;
import static com.visoft.file.service.service.StatusConst.USER_HAS_BEEN_CREATED;
import static com.visoft.file.service.service.util.EncoderService.getEncode;
import static com.visoft.file.service.service.util.EncoderService.isPasswordsMatch;
import static com.visoft.file.service.service.util.JWTService.generate;
import static com.visoft.file.service.service.util.JsonService.toJson;
import static com.visoft.file.service.service.util.RequestService.getIdFromRequest;
import static com.visoft.file.service.service.util.SenderService.*;

public class UserServiceImpl extends AbstractServiceImpl<User> implements UserService {

    public UserServiceImpl() {
        super(Repositories.USER_REPOSITORY);
    }

    @Override
    public void update(HttpServerExchange exchange) {
        UserUpdateDto dto = getUpdateUserRequestBody(exchange);
        if (!validate(dto)) {
            send(exchange, BAD_REQUEST);
        } else {
            User user = findById(new ObjectId(dto.getId()));
            if (user == null) {
                sendInfo(USER_DOES_NOT_EXIST, null);
                send(exchange, BAD_REQUEST);
            }
            List<ObjectId> folders = null;
            if (dto.getFolders() != null && !dto.getFolders().isEmpty()) {
                folders = FOLDER_SERVICE.getIdsFromStrings(dto.getFolders());
            }
            if (isExistsByLogin(dto.getLogin(), user.getId())) {
                send(
                        exchange,
                        LOGIN_EXISTS,
                        BAD_REQUEST
                );
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
        User user = findById(getIdFromRequest(exchange));
        if (user == null) {
            sendInfo(USER_DOES_NOT_EXIST, null);
            send(exchange, NOT_FOUND);
        } else {
            send(
                    exchange,
                    toJson(new UserOutcomeDto(user)
                    )
            );
        }
    }

    @Override
    public void createUser(HttpServerExchange exchange) {
        create(
                getCreateUserRequestBody(exchange),
                exchange,
                USER
        );
    }

//    @Override
//    public void createAdmin(HttpServerExchange exchange) {
//        create(
//                getCreateUserRequestBody(exchange),
//                exchange,
//                ADMIN
//        );
//    }

    private void create(UserCreateDto dto, HttpServerExchange exchange, Role role) {
        if (!validate(dto)) {
            send(exchange, BAD_REQUEST);
        } else {
            List<ObjectId> folders = null;
            if (dto.getFolders() != null && !dto.getFolders().isEmpty()) {
                folders = FOLDER_SERVICE.getIdsFromStrings(dto.getFolders());
            }

            if (isExistsByLogin(dto.getLogin())) {
                send(exchange, LOGIN_EXISTS, BAD_REQUEST);
            }
            User createdUser = new User(dto.getLogin(), getEncode(
                    dto.getPassword()),
                    role, folders
            );
            create(createdUser);
            Token createdUserToken = new Token(
                    generate(ObjectId.get()),
                    createdUser.getId()
            );
            TOKEN_SERVICE.create(createdUserToken);
            sendInfo(USER_HAS_BEEN_CREATED, createdUser.getId().toString());
            send(exchange, CREATE);
        }
    }

    @Override
    public void delete(HttpServerExchange exchange) {
        ObjectId userId = getIdFromRequest(exchange);
        User currentUser = findByIdNotDeleted(getIdFromRequest(exchange));
        if (currentUser == null || currentUser.getId().equals(SecurityHandler.authenticatedUser.getUser().getId())) {
            send(exchange, "User not found");
            send(exchange, FORBIDDEN);
        } else {
            update(userId, DELETED, true);
        }
    }

    @Override
    public void recovery(HttpServerExchange exchange) {
        ObjectId userId = getIdFromRequest(exchange);
        User currentUser = USER_SERVICE.findById(userId);
        if (currentUser == null || currentUser.getDeleted().equals(false)) {
            sendInfo(USER_DOES_NOT_EXIST, userId.toString());
            send(exchange, FORBIDDEN);
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
        send(
                exchange,
                toJson(
                        findAll()
                        .parallelStream()
                        .map(UserOutcomeDto::new)
                        .collect(Collectors.toList())
                )
        );
    }

    @Override
    public User findByLoginAndPassword(String login, String password) {
        Bson filter = and(
                eq(DELETED, false),
                eq(UserConst.LOGIN, login));
        User user = super.getObject(filter);
        if (!isPasswordsMatch(password, user.getPassword()))
            return null;
        return user;
    }

    @Override
    public User findByLogin(String login) {
        Bson filter = and(
                eq(DELETED, false),
                eq(UserConst.LOGIN, login));
        return super.getObject(filter);
    }

    @Override
    public boolean isExistsByLogin(String login) {
        return isExists(eq(UserConst.LOGIN, login));
    }

    private UserCreateDto getCreateUserRequestBody(HttpServerExchange exchange) {
        exchange.startBlocking();
        InputStream is = exchange.getInputStream();
        String s = (new Scanner(is, "UTF-8")).useDelimiter("\\A").next();
        try {
            return Config.getInstance().getMapper().readValue(s, UserCreateDto.class);
        } catch (IOException e) {
            sendWarn(UNABLE_TO_READ_VALUE_REQUEST_BODY, e.getMessage());
            e.printStackTrace();
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
            sendWarn(UNABLE_TO_READ_VALUE_REQUEST_BODY, e.getMessage());
            e.printStackTrace();
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
            sendInfo(DTO_IS_EMPTY, null);
            return false;
        }
        return validate(dto.getLogin(), dto.getPassword(), dto.getFolders());
    }

    private boolean validate(UserUpdateDto dto) {
        if (dto == null) {
            sendInfo(DTO_IS_EMPTY, null);
            return false;
        }
        if (dto.getId() == null || dto.getId().isEmpty()) {
            sendInfo(USER_UPDATE_DTO_ID_IS_EMPTY, null);
            return false;
        }
        return validate(dto.getLogin(), dto.getPassword(), dto.getFolders());
    }

    private boolean validate(String login, String password, List<String> folders) {
        if (login == null || login.isEmpty()) {
            sendInfo(USER_LOGIN_IS_EMPTY, login);
            return false;
        }
        if (password == null || password.isEmpty()) {
            sendInfo(USER_PASSWORD_IS_EMPTY, password);
            return false;
        }
        if (folders == null) {
            sendInfo(FOLDER_DTO_IS_EMPTY, null);
            return false;
        }
        if (!folders.isEmpty()) {
            if (new HashSet<>(folders).size() != folders.size()) {
                sendInfo(FOLDER_DTO_DUPLICATE, String.valueOf(folders.size()));
                return false;
            }
            for (String folder : folders) {
                if (!FOLDER_SERVICE.existsFolder(new ObjectId(folder))) {
                    sendInfo(FOLDER_DOES_NOT_EXIST, folder);
                    return false;
                }
            }
        }
        return true;
    }

    public String getRandomPassword() {
        return Arrays.toString(
                new Random().ints(5, 0, 10).toArray()
        ).replaceAll("\\[|]|,|\\s", "");
    }

    private boolean isExistsByLogin(String login, ObjectId id) {
        return isExists(and(eq(UserConst.LOGIN, login), ne(_ID, id)));
    }
}