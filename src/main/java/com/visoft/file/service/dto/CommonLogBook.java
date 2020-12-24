package com.visoft.file.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

@Getter
@Setter
public class CommonLogBook {

    private String fullPath;

    private String fullName;

    private Long orderInGroup;

    public void setFullPath(String fullPath) {
        this.fullPath = FilenameUtils.separatorsToSystem(fullPath);

    }

    public void setFullName(String fullName) {
        String[] split = FilenameUtils.separatorsToSystem(fullName).split("/");
        this.fullName = split[split.length - 1];
    }
}