package com.visoft.file.service.dto.user;

import com.visoft.file.service.persistence.entity.Folder;
import com.visoft.file.service.persistence.entity.user.Role;
import com.visoft.file.service.persistence.entity.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * OutcomeDto for {@link User User}
 */
@Getter
@Setter
public class UserOutcomeDto {

    /**
     * {@link User#getId() id}
     */
    private String id;

    /**
     * {@link User#isDeleted() deleted}
     */
    private Boolean deleted;

    /**
     * {@link User#getLogin() login}
     */
    private String login;

    /**
     * {@link Role ROLE}
     */
    private List<Role> authorities;

    /**
     * {@link List List}<{@link Folder#getId() folderId}>
     */
    private List<String> folders;
}