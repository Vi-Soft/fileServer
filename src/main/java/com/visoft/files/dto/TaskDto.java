package com.visoft.files.dto;

import lombok.Data;

@Data
public class TaskDto {

    private String name;

    private Long id;

    private Long parentId;

    private Long orderInGroup;

    private Integer icon;
}
