package com.visoft.file.service.dto.user;

import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.User;
import lombok.Data;

import java.util.List;

/**
 * UpdateDto for {@link User User}
 */
@Data
public class UserUpdateDto {

    /**
     * {@link User#getId() id}
     */
    private String id;

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