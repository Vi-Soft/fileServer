package com.visoft.file.service.dto;

import lombok.Data;
import org.apache.commons.io.FilenameUtils;


@Data
public class FormType {

    private Type type = Type.DEFAULT;

    private String path = "-";

    public void setPath(String path) {
        this.path = FilenameUtils.separatorsToSystem(path);
    }
}
