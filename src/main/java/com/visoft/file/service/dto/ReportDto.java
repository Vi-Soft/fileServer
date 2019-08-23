package com.visoft.file.service.dto;

import com.visoft.file.service.Version;
import lombok.Data;

import java.util.List;

@Data
public class ReportDto {

    private String email;

    private Version version;

    private String url;

    private String customToken;

    private String projectName;

    private String companyName;

    private String archiveName;

    private List<TaskDto> tasks;

    private List<FormType> formTypes;
}