package com.visoft.file.service.dto.folder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FolderFindDto {

    private String folder;
    private String projectName;
    private String taskName;
}