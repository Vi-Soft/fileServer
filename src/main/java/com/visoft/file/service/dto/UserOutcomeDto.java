package com.visoft.file.service.dto;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.List;

import static com.visoft.file.service.service.DI.DependencyInjectionService.FOLDER_SERVICE;

@Data
public class UserOutcomeDto {

    private String id;

    private String login;

    private List<String> folders;

    public UserOutcomeDto(ObjectId id, String login, List<ObjectId> folders) {
        this.id = id.toString();
        this.login = login;
        this.folders = FOLDER_SERVICE.getIdsFromObjectId(folders);
    }
}
