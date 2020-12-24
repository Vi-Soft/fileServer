package com.visoft.file.service.dto.folder;

import com.visoft.file.service.persistence.entity.Folder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OutcomeDto for {@link Folder}
 */
@Data
@NoArgsConstructor
public class FolderOutcomeDto {

    /**
     * {@link Folder#getId() id}
     */
    private String id;

    /**
     * {@link Folder#getFolder() folder}
     */
    private String folder;
}