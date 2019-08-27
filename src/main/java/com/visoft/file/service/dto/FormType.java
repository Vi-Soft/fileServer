package com.visoft.file.service.dto;

import lombok.Data;

import java.nio.file.Paths;

import static com.visoft.file.service.dto.Type.DEFAULT;

@Data
public class FormType {

    private Type type = DEFAULT;

    private String path;

    public void setPath(String path) {
        this.path = Paths
                .get(path)
                .toString();
    }
}
