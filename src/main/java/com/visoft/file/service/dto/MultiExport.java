package com.visoft.file.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MultiExport {
    private int count;
    private int size;
    private String mutualPath;
    private String projectName;
    private List<String> paths;
}
