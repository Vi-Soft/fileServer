package com.visoft.file.service.dto;

import lombok.Data;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Paths;

@Data
public class AttachmentDocument {

    private String path;

    private String type= "-";

    private String description= "-";

    private String certificate= "-";

    private String comment= "-";

    private String uploadDate= "-";

    private String fileName= "-";

    public void setPath(String path) {
        this.path = path == null || path.isEmpty()
                ? "-"
                : FilenameUtils.separatorsToSystem(path);
    }
}