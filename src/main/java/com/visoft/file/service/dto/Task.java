package com.visoft.file.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Task {

    private String name;

    private Long id;

    private List<Task> tasks;

    private Long orderInGroup;

    private Integer icon;
}
