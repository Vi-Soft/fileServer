package com.visoft.file.service.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TaskDto {

    private String name;

    private String id;

    private String parentId;

    private Long orderInGroup;

    private Integer icon;

    private String color;

    private Map<String, String> detail;
}