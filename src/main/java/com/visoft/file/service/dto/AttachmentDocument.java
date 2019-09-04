package com.visoft.file.service.dto;

import lombok.Data;

import java.nio.file.Paths;

@Data
public class AttachmentDocument {

    private String path;

    private String type;

    private String description;

    private String certificate;

    private String comment;

    private String uploadDate;

    private String fileName;

    public void setPath(String path) {
        this.path = path == null || path.isEmpty()
                ? "-"
                : Paths
                .get(path)
                .toString();
    }

    public void setType(String type) {
        this.type = type == null || type.isEmpty() ? "-" : type;
    }

    public void setDescription(String description) {
        this.description = description == null || description.isEmpty() ? "-" : description;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public void setComment(String comment) {
        this.comment = comment == null || comment.isEmpty() ? "-" : comment;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate == null || uploadDate.isEmpty() ? "-" : uploadDate;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null || fileName.isEmpty() ? "-" : fileName;
    }
}
