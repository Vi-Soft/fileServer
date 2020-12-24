package com.visoft.file.service.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TaskDto {

    private String name;

    private Long id;

    private Long parentId;

    private Long orderInGroup;

    private Integer icon;

    private String color;

    private Map<String, String> detail;
}