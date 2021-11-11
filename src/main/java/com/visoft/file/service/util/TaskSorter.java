package com.visoft.file.service.util;

import com.visoft.file.service.dto.Task;

import java.util.List;

/**
 * This class sorts the passed task list
 */
public class TaskSorter {

    public static void byTaskName(List<Task> tasks) {
        tasks.sort((task1, task2) -> {
            Integer i1 = parseIntegerIfPossible(task1.getName());
            Integer i2 = parseIntegerIfPossible(task2.getName());

            if (i1 != null && i2 != null) {
                return i1.compareTo(i2);
            } else if (i1 != null) {
                return 1;
            } else if (i2 != null) {
                return -1;
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
