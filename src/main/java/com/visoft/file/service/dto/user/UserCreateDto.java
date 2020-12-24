package com.visoft.file.service.dto.user;

import com.visoft.file.service.persistence.entity.Folder;
import com.visoft.file.service.persistence.entity.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * CreateDto for {@link User User}
 */
@Getter
@Setter
public class UserCreateDto {

    /**
     * {@link User#getLogin() login}
     */
    private String login;

    /**
     * {@link User#getPassword() password}
     */
    private String password;

    /**
     * {@link List List<}{@link Folder#getId() folderId}>
     */
    private List<String> folders;
}