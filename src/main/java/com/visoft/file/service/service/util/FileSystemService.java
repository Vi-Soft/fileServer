package com.visoft.file.service.service.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class FileSystemService {

    public static void delete(String path) throws IOException {
        File file = new File(path);
        if (isDirectory(path)) {
            FileUtils.deleteDirectory(file);
        } else {
            FileUtils.deleteQuietly(file);
        }

    }

    public static void deleteIfEmpty(String path) throws IOException {
        exists(path);
        if (Objects.requireNonNull(new File(path).list()).length == 0) {
            delete(path);
        }
    }

    public static void exists(String path) throws FileNotFoundException {
        if (!new File(path).exists()) {
            throw new FileNotFoundException();
        }
    }

    public static boolean isDirectory(String path) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException();
        } else {
            return file.isDirectory();
        }
    }
}