package com.visoft.file.service.dto;

import lombok.Data;
import org.apache.commons.io.FilenameUtils;

import static com.visoft.file.service.dto.Type.DEFAULT;

@Data
public class FormType implements PathObject {

    private Type type = DEFAULT;

    private String path = "-";

    public void setPath(String path) {
        this.path = FilenameUtils.separatorsToSystem(path);
    }
}
