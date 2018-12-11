package com.visoft.files.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Report {

    private String projectName;

    private String companyName;

    private String archiveName;

    private Task task;
}
