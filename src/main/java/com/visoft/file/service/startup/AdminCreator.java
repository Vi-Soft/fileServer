package com.visoft.file.service.startup;

import com.visoft.file.service.persistance.entity.Role;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.User;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import static com.visoft.file.service.service.DI.DependencyInjectionService.TOKEN_SERVICE;
import static com.visoft.file.service.service.DI.DependencyInjectionService.USER_SERVICE;
import static com.visoft.file.service.service.util.EncoderService.getEncode;
import static com.visoft.file.service.service.util.JWTService.generate;

@Log4j
public class AdminCreator {

    public AdminCreator() {
        createAdmin();
    }

    private void createAdmin() {
        String login = "l1";
        String password = "p1";
        if (USER_SERVICE.isExistsByLogin(login)) {
            log.info("Admin already exists");
        } else {
            User user = new User(login,
                    getEncode(
                            password),
                    Role.ADMIN,
                    new ArrayList<>()
            );
            USER_SERVICE.create(user);
            Token createdUserToken = new Token(
                    generate(ObjectId.get()),
                    user.getId()
            );
            TOKEN_SERVICE.create(createdUserToken);
            log.info("Admin created");
        }
    }

}