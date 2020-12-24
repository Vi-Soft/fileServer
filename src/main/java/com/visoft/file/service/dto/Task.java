package com.visoft.file.service.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task implements Comparable<Task> {

    private String name;

    private Long id;

    private List<Task> tasks;

    private Long orderInGroup;

    private Integer icon;

    private String path;

    private String color;

    private Type type = Type.DEFAULT;

    private Map<String, String> detail;

    public Task(String name, Long id, List<Task> tasks, Long orderInGroup, Integer icon, String color, Map<String, String> detail) {
        this.name = name;
        this.id = id;
        this.tasks = tasks;
        this.orderInGroup = orderInGroup;
        this.icon = icon;
        this.path = null;
        this.color = color;
        this.detail = detail;
    }

    @Override
    public int compareTo(Task o) {
        return this.orderInGroup.compareTo(o.getOrderInGroup());
    }
}