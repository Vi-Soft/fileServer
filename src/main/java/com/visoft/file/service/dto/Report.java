package com.visoft.file.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Report {

    private String projectName;

    private String companyName;

    private String archiveName;

    private Task task;
}