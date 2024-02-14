package com.visoft.file.service.dto.folder;

import com.visoft.file.service.persistance.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFindDto {

    private String deleted;
    private String login;
    private String role;
    private String folders;
}