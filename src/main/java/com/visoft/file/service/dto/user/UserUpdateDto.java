package com.visoft.file.service.dto.user;

import com.visoft.file.service.persistence.entity.Folder;
import com.visoft.file.service.persistence.entity.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * UpdateDto for {@link User User}
 */
@Getter
@Setter
public class UserUpdateDto {

    /**
     * {@link List List<}{@link Folder#getId() folderId}>
     */
    private List<String> folders;
}