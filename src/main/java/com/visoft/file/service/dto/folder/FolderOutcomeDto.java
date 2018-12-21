package com.visoft.file.service.dto.folder;

import com.visoft.file.service.persistance.entity.Folder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FolderOutcomeDto {

    private String id;

    private String folder;

    public FolderOutcomeDto(Folder folder) {
        if (folder != null) {
            this.id = folder.getId().toString();
            this.folder = folder.getFolder();
        }
    }
}