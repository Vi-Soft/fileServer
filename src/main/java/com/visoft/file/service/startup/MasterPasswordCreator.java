package com.visoft.file.service.startup;

import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.visoft.file.service.service.util.PropertiesService.getMasterPassword;
import static com.visoft.file.service.service.util.PropertiesService.getMasterPasswordPath;

@Log4j
public class MasterPasswordCreator {

    public MasterPasswordCreator() {
        createPassword();
    }

    private void createPassword() {
        String masterPassword = getMasterPassword();
        String pathToOverriddenMasterPassword = getMasterPasswordPath();

        final Path masterPasswordPath = Paths.get(pathToOverriddenMasterPassword);

        if (!masterPasswordPath.toFile().exists()) {
            System.out.println(">>> File " + pathToOverriddenMasterPassword + " doesn't exist");

            try {
                Files.createFile(masterPasswordPath);
                Files.write(masterPasswordPath, masterPassword.getBytes(StandardCharsets.UTF_8));

                log.info("File with master password has been created");
            } catch (IOException e) {
                throw new RuntimeException("Exception in trying to create file: " + pathToOverriddenMasterPassword);
            }
        } else {
            log.info("File with master password already exists");
        }
    }

}