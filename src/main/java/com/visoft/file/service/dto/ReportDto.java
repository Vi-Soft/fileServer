package com.visoft.file.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReportDto {

    private String customToken;

    private String projectName;

    private String companyName;

    private String archiveName;

    private List<TaskDto> tasks;
}