package com.visoft.file.service.service;

import com.networknt.config.Config;
import com.visoft.file.service.dto.Report;
import com.visoft.file.service.dto.ReportDto;
import com.visoft.file.service.dto.Task;
import com.visoft.file.service.dto.TaskDto;
import com.visoft.file.service.service.util.PageService;
import com.visoft.file.service.service.util.PropertiesService;
import io.undertow.server.HttpServerExchange;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReportService {

    private static final String PROJECT_NAME_NOT_CORRECT = "Project name must be not null and not empty";

    private static final String COMPANY_NAME_NOT_CORRECT = "Company name must be not null and not empty";

    private static final String ARCHIVE_NAME_NOT_CORRECT = "Archive name must be not null and not empty";

    private static final String TASK_NAME_NOT_CORRECT = "Task name must be not null and not empty";

    private static final String TASK_ID_NOT_CORRECT = "Task id must be not null and > 0";

    private static final String MORE_ONE_EQUALS_TASK_ID = "More equals task id";

    private static final String PARENT_TASK_ID_NOT_CORRECT = "Parent task id must be not null and not equals 0 and < -1";

    private static final String MORE_ONE_EQUALS_PARENT_TASK_ID = "More one equals parent task id";

    private static final String ORDER_IN_GROUP_NOT_CORRECT = "Order in group be not null";

    private static final String MORE_ONE_EQUALS_ORDER_IN_GROUP = "More one equals order in group";

    private static final String ICON_NOT_CORRECT = "Icon must be not null and > -1 and < 3";

    private static String rootPath = PropertiesService.getRootPath();

    public static void unzip(HttpServerExchange exchange) throws IOException {
        ReportDto reportDto = getRequestBody(exchange);
        sortByParentIdAndOrderInGroup(reportDto);
        String validateReportDtoResult = validateReportDto(reportDto);
        if (validateReportDtoResult != null) {
            exchange.getResponseSender().send(validateReportDtoResult);
        } else {
            String validateZipResult = validateZip(reportDto.getArchiveName(), reportDto.getCompanyName());
            if (validateZipResult != null) {
                exchange.getResponseSender().send(validateZipResult);
            } else {
                unzip(rootPath + "/" + reportDto.getArchiveName() + ".zip",
                        rootPath + "/" + reportDto.getCompanyName());
                Report tree = getTree(reportDto);
                PageService.saveIndexHtml(tree);
            }
        }
    }

    private static ReportDto getRequestBody(HttpServerExchange exchange) throws IOException {
        exchange.startBlocking();
        InputStream is = exchange.getInputStream();
        String s = (new Scanner(is, "UTF-8")).useDelimiter("\\A").next();
        return Config.getInstance().getMapper().readValue(s, ReportDto.class);
    }

    private static String validateReportDto(ReportDto dto) {
        String projectName = dto.getProjectName();
        String companyName = dto.getCompanyName();
        String archiveName = dto.getArchiveName();
        if (projectName == null || projectName.equals("")) {
            return PROJECT_NAME_NOT_CORRECT;
        }
        if (companyName == null || companyName.equals("")) {
            return COMPANY_NAME_NOT_CORRECT;
        }
        if (archiveName == null || archiveName.equals("")) {
            return ARCHIVE_NAME_NOT_CORRECT;
        }
        int parentIdNullCount = 0;
        Set<Long> ids = new HashSet<>();
        Long previousParentId = 0L;
        Long previousOrderInGroup = 0L;
        for (TaskDto task : dto.getTasks()) {
            String name = task.getName();
            Long id = task.getId();
            Long parentId = task.getParentId();
            Long orderInGroup = task.getOrderInGroup();
            Integer icon = task.getIcon();
            if (name == null || name.equals("")) {
                return TASK_NAME_NOT_CORRECT+" id:"+id+"name:"+name+" order:"+orderInGroup+" parentId:"+parentId;
            }
            if (id == null || id < 1) {
                return TASK_ID_NOT_CORRECT+" id:"+id+"name:"+name+" order:"+orderInGroup+" parentId:"+parentId;
            }
            ids.add(id);
            if (parentId == null || parentId == 0 || parentId < -1) {
                return PARENT_TASK_ID_NOT_CORRECT+" id:"+id+"name:"+name+" order:"+orderInGroup+" parentId:"+parentId;
            }
            if (task.getParentId() == -1) {
                parentIdNullCount++;
            }
            if (orderInGroup == null ) {
                return ORDER_IN_GROUP_NOT_CORRECT+" id:"+id+"name:"+name+" order:"+orderInGroup+" parentId:"+parentId;
            }
            if (orderInGroup.equals(previousOrderInGroup) && parentId.equals(previousParentId)) {
                return MORE_ONE_EQUALS_ORDER_IN_GROUP + " id:"+id+"name:"+name+" order:"+orderInGroup+" parentId:"+parentId;
            }
            previousParentId = parentId;
            previousOrderInGroup = orderInGroup;
            if (icon == null || icon < 0 || icon > 2) {
                return ICON_NOT_CORRECT;
            }

        }
        if (parentIdNullCount > 1) {
            return MORE_ONE_EQUALS_PARENT_TASK_ID;
        }
        if (ids.size() != dto.getTasks().size()) {
            return MORE_ONE_EQUALS_TASK_ID;
        }
        return null;
    }

    private static void sortByParentIdAndOrderInGroup(ReportDto reportDto) {
        reportDto.getTasks().sort(Comparator
                .comparing(TaskDto::getParentId)
                .thenComparing(TaskDto::getOrderInGroup)
        );
    }

    private static Report getTree(ReportDto reportDto) {
        Report report = Report.builder()
                .projectName(reportDto.getProjectName())
                .companyName(reportDto.getCompanyName())
                .archiveName(reportDto.getArchiveName())
                .build();
        Task mainTask = null;
        for (TaskDto task : reportDto.getTasks()) {
            if (task.getParentId() == -1) {
                Task currentTask = Task.builder()
                        .name(task.getName())
                        .id(task.getId())
                        .orderInGroup(task.getOrderInGroup())
                        .icon(task.getIcon())
                        .build();
                mainTask = currentTask;
            }
        }
        if (mainTask != null) {
            Task childrenTasks = getChildrenTasks(reportDto.getTasks(), mainTask);
            report.setTask(childrenTasks);
        }
        return report;
    }

    private static Task getChildrenTasks(List<TaskDto> taskDtos, Task mainTask) {
        if (mainTask != null) {
            for (TaskDto taskDto : taskDtos) {
                if (taskDto.getParentId().equals(mainTask.getId())) {
                    Task childrenTask = new Task(
                            taskDto.getName(),
                            taskDto.getId(),
                            null,
                            taskDto.getOrderInGroup(),
                            taskDto.getIcon()
                    );
                    List<Task> tasks1 = mainTask.getTasks();
                    if (tasks1 == null) {
                        ArrayList<Task> currentTasks = new ArrayList<>();
                        currentTasks.add(childrenTask);
                        tasks1 = currentTasks;
                    } else {
                        tasks1.add(childrenTask);
                    }
                    mainTask.setTasks(tasks1);
                }
            }
        } else {
            return null;
        }
        if (mainTask.getTasks() != null) {
            for (Task task : mainTask.getTasks()) {
                getChildrenTasks(taskDtos, task);
            }
        }
        return mainTask;
    }

    private static String validateZip(String archiveName, String companyName) throws IOException {
        if (existsFolder(rootPath + "/" + companyName)) {
            List<Path> subfolder = Files.walk(Paths.get(rootPath + "/" + companyName), 1)
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
            for (Path path : subfolder) {
                if (path.getFileName().toString().equals(archiveName)) {
                    return "Already unzip";
                }
            }
            if (!existsZip(archiveName)) {
                return archiveName + ".zip not exists";
            }
        }

        return null;
    }

    private static boolean existsFolder(String path) {
        return new File(path).exists();
    }

    private static boolean existsZip(String archiveName) {
        return new File(rootPath + "/" + archiveName + ".zip").exists();
    }

    private static void unzip(final String zipFilePath, final String unzipLocation) throws IOException {

        if (!(Files.exists(Paths.get(unzipLocation)))) {
            Files.createDirectories(Paths.get(unzipLocation));
        }
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                Path filePath = Paths.get(unzipLocation, entry.getName());
                if (!entry.isDirectory()) {
                    unzipFiles(zipInputStream, filePath);
                } else {
                    Files.createDirectories(filePath);
                }

                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        }
    }

    private static void unzipFiles(final ZipInputStream zipInputStream, final Path unzipFilePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(unzipFilePath.toAbsolutePath().toString()))
        ) {
            byte[] bytesIn = new byte[1024];
            int read;
            while ((read = zipInputStream.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }

    }
}
