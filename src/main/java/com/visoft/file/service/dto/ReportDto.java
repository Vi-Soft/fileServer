package com.visoft.file.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.visoft.file.service.Version;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ReportDto {

    private String email;

    private Version version;

    private String url;

    private String customToken;

    private String projectName;

    private String companyName;

    private String archiveName;

    private int count;

    private long timestamp;

    private String password;

    private String mainCompanyId;

    private List<TaskDto> tasks;

    private Set<FormType> formTypes = new HashSet<>();

    private Set<AttachmentDocument> attachmentDocuments = new HashSet<>();

    private Set<CommonLogBook> commonLogBooks = new HashSet<>();

    private Set<Type> typesToDisplay = new HashSet<>();

    private Boolean isWinMode;

    @JsonIgnore
    private String errorMassage;
}
