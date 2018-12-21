package com.visoft.file.service.dto.user;

import com.visoft.file.service.persistance.entity.Role;
import com.visoft.file.service.persistance.entity.User;
import lombok.Data;

import java.util.List;

import static com.visoft.file.service.service.DI.DependencyInjectionService.FOLDER_SERVICE;

@Data
public class UserOutcomeDto {

    private String id;

    private Boolean deleted;

    private String login;

    private Role role;

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