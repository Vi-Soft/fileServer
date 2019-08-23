package com.visoft.file.service.dto;

import lombok.Data;

import java.nio.file.Paths;
import java.util.List;

import static com.visoft.file.service.dto.Type.*;

@Data
public class FormType {

    private Type type= DEFAULT;

    private String path;

    public void setPaths(List<String> paths) {
        this.path = Paths
                .get(path)
                .toString();

    }
}
