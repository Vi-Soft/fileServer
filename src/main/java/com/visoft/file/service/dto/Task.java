package com.visoft.file.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Task implements Comparable<Task> {

    private String name;

    private Long id;

    private List<Task> tasks;

    private Long orderInGroup;

    private Integer icon;

    private String path;

    public Task(String name, Long id, List<Task> tasks, Long orderInGroup, Integer icon) {
        this.name = name;
        this.id = id;
        this.tasks = tasks;
        this.orderInGroup = orderInGroup;
        this.icon = icon;
        this.path = null;
    }

    @Override
    public int compareTo(Task o) {
        return this.orderInGroup.compareTo(o.getOrderInGroup());
    }
}