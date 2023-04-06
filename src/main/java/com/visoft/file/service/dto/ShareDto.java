package com.visoft.file.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShareDto {

    private List<String> emails;

    private String folder;
}
