package com.visoft.file.service.dto.user;

import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.Role;
import com.visoft.file.service.persistance.entity.User;
import lombok.Data;

import java.util.List;

import static com.visoft.file.service.service.DI.DependencyInjectionService.FOLDER_SERVICE;

/**
 * OutcomeDto for {@link User User}
 */
@Data
public class UserOutcomeDto {

    /**
     * {@link User#getId() id}
     */
    private String id;

    /**
     * {@link User#getDeleted() deleted}
     */
    private Boolean deleted;

    /**
     * {@link User#getLogin() login}
     */
    private String login;

    /**
     * {@link Role ROLE}
     */
    private Role role;

    /**
     * {@link List List}<{@link Folder#getId() folderId}>
     */
    private List<String> folders;

    public UserOutcomeDto(User user) {
        if (user != null) {
            this.id = user.getId().toString();
            this.deleted = user.getDeleted();
            this.login = user.getLogin();
            this.role = user.getRole();
            this.folders = FOLDER_SERVICE.getIdsFromObjectId(user.getFolders());
        }
    }
}