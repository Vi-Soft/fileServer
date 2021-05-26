package com.visoft.file.service.startup;

import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;

import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.visoft.file.service.service.DI.DependencyInjectionService.FOLDER_SERVICE;
import static com.visoft.file.service.service.util.PropertiesService.getRootPath;
import static org.zeroturnaround.zip.commons.FileUtilsV2_2.deleteQuietly;

@Log4j
public class Scheduler extends TimerTask {

    private static final String rootPath = getRootPath();

    public Scheduler() {
        new Timer().scheduleAtFixedRate(new Scheduler(1), 0, TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
    }

    public Scheduler(int i) {}

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        FOLDER_SERVICE.findAll().forEach(folder -> {
            if (folder.getDate() != null && ChronoUnit.WEEKS.between(LocalDateTime.ofInstant(folder.getDate(), ZoneId.systemDefault()), now) >= 2) {
                deleteQuietly(new File(Paths.get(rootPath, folder.getFolder()).toString()));
                deleteQuietly(new File(Paths.get(rootPath, folder.getFolder() + ".zip").toString()));
                FOLDER_SERVICE.delete(folder.getId());
            }
        });
    }

}
