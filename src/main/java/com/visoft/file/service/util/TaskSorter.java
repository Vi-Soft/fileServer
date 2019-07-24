package com.visoft.file.service.util;

import com.visoft.file.service.dto.Task;

import java.util.List;

/**
 * This class sorts the passed task list
 */
public class TaskSorter {

    public static void byTaskName(List<Task> tasks) {
        tasks.sort((task1, task2) -> {
            Integer n1 = parseIntegerIfPossible(task1.getName());
            Integer n2 = parseIntegerIfPossible(task2.getName());

            if (n1 != null && n2 != null) {
                return n1.compareTo(n2);
            }
            return 0;
        });
    }

    private static Integer parseIntegerIfPossible(String possibleStringIntegerValue) {
        try {
            return Integer.valueOf(possibleStringIntegerValue);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

}
