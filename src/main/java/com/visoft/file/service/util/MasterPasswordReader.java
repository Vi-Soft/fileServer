package com.visoft.file.service.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.visoft.file.service.service.util.PropertiesService.getMasterPasswordPath;

@Slf4j
public class MasterPasswordReader {

    public static String readMasterPassword() {
        final String path = getMasterPasswordPath();

        log.info("Property value has been read: {}", path);

        if (path == null) {
            return null;
        }

        return readFirstLine(path);
    }

    private static String readFirstLine(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath))
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unable to read individual adjustable settings"));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
