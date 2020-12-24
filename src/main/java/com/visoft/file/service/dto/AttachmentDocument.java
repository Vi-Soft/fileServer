package com.visoft.file.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

@Getter
@Setter
public class AttachmentDocument {

    private String path;

    private String type = "-";

    private String description = "-";

    private String certificate = "-";

    private String comment = "-";

    private String uploadDate = "-";

    private String fileName = "-";

    public void setPath(String path) {
        this.path = path == null || path.isEmpty()
                ? "-"
                : FilenameUtils.separatorsToSystem(path);
    }
}