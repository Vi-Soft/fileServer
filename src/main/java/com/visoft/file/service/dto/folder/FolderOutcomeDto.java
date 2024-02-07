package com.visoft.file.service.dto.folder;

import com.visoft.file.service.persistance.entity.Folder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OutcomeDto for {@link Folder}
 */
@Data
@NoArgsConstructor
public class FolderOutcomeDto {

    private String id;
    private String folder;
    private String projectName;
    private String taskName;

    public FolderOutcomeDto(Folder folder) {
        if (folder != null) {
            this.id = folder.getId().toString();
            this.folder = folder.getFolder();
            this.projectName = folder.getProjectName();
            this.taskName = folder.getTaskName();
        }
    }
}