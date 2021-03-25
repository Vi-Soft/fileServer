package com.visoft.file.service.dto;

import com.visoft.file.service.Version;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class EmailReportDto {
    private String email;

    private Object version;

    private Object url;

    private Object customToken;

    private Object projectName;

    private Object companyName;

    private Object archiveName;

    private Object count;

    private Object timestamp;

    private List<Object> tasks;

    private Set<Object> formTypes = new HashSet<>();

    private Set<Object> attachmentDocuments = new HashSet<>();

    private Set<Object> commonLogBooks = new HashSet<>();

    private String errorMessage;
}
