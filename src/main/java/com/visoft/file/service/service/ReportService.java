package com.visoft.file.service.service;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.networknt.config.Config;
import com.visoft.file.service.dto.Report;
import com.visoft.file.service.dto.ReportDto;
import com.visoft.file.service.dto.Task;
import com.visoft.file.service.dto.TaskDto;
import io.undertow.server.HttpServerExchange;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.visoft.file.service.service.DI.DependencyInjectionService.FOLDER_SERVICE;
import static com.visoft.file.service.service.ErrorConst.*;
import static com.visoft.file.service.service.util.PageService.saveIndexHtml;
import static com.visoft.file.service.service.util.PropertiesService.*;
import static com.visoft.file.service.service.util.SenderService.sendStatusCode;

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

    private static String rootPath = getRootPath();

    private static Report getFullTree(ReportDto reportDto) {

        Report report = Report.builder()
                .projectName(reportDto.getProjectName())
                .companyName(reportDto.getCompanyName())
                .archiveName(reportDto.getArchiveName())
                .build();
        Task task = new Task(report.getProjectName(), null, new ArrayList<>(), -10000L, 0);
        List<Task> tasks = new ArrayList<>();

        String[] list = new File(rootPath + "/" + report.getCompanyName() + "/" + report.getArchiveName()).list();
        if (list != null && list.length != 0) {
            for (String s : list) {
                Task currentTask = new Task(s, null, new ArrayList<>(), 100000L, 0);
                tasks.add(new ReportService().getTreeByFileSystem(currentTask, "/" + currentTask.getName(), "/" + report.getCompanyName(), "/" + report.getArchiveName()));

            }
        }
        task.setTasks(tasks);
        report.setTask(task);
        return report;
    }

    private static void getDeleteNotWantFiles(Report report) {
        List<Task> deletedTask = new ArrayList<>();
        for (Task task : report.getTask().getTasks()) {
            String taskName = task.getName();
            if (taskName.equals("index.html")
                    || taskName.equals("g.png")
                    || taskName.equals("r.png")) {
                deletedTask.add(task);
            }
        }
        report.getTask().getTasks().removeAll(deletedTask);
    }

    private static void setPathFullPath(Task task, String path) {
        if (task.getTasks() != null && !task.getTasks().isEmpty()) {
            for (Task taskTask : task.getTasks()) {
                if (taskTask.getIcon() == 3) {
                    taskTask.setPath("/" + path + "/" + taskTask.getPath());
                }
                setPathFullPath(taskTask, path);
            }
        }
    }

    private static void getRealTask(Task task, List<TaskDto> tasks) {
        if (task.getTasks() != null && !task.getTasks().isEmpty()) {
            for (Task taskTask : task.getTasks()) {
                for (TaskDto taskDto : tasks) {
                    if (taskTask.getName().equals(taskDto.getId().toString())) {
                        taskTask.setIcon(taskDto.getIcon());
                        taskTask.setName(taskDto.getName());
                        taskTask.setOrderInGroup(taskDto.getOrderInGroup());
                    }
                }

                getRealTask(taskTask, tasks);
            }
            sortByParentIdAndOrderInGroup(task.getTasks());
        }
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

        }
        if (!existsZip(archiveName)) {
            return archiveName + ".zip not exists";
        }
        return null;
    }

    private static void sortByParentIdAndOrderInGroup(List<Task> tasks) {
        tasks.sort(Comparator
                .comparing(Task::getOrderInGroup)
        );
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
                return TASK_NAME_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            if (id == null || id < 1) {
                return TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            ids.add(id);
            if (parentId == null || parentId == 0 || parentId < -1) {
                return PARENT_TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            if (task.getParentId() == -1) {
                parentIdNullCount++;
            }
            if (orderInGroup == null) {
                return ORDER_IN_GROUP_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            if (orderInGroup.equals(previousOrderInGroup) && parentId.equals(previousParentId)) {
                return MORE_ONE_EQUALS_ORDER_IN_GROUP + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
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

    private Task getTreeByFileSystem(Task task, String path, String companyName, String projectName) {
        String fullPath = rootPath + companyName + projectName + path;
        if (task != null) {
            if (new File(fullPath).isDirectory()) {
                String[] list = new File(fullPath).list();
                List<Task> tasks = new ArrayList<>();
                if (list != null) {
                    for (String s : list) {
                        Task currentTask = new Task(s, null, new ArrayList<>(), 10L, 0);
                        Task ss = getTreeByFileSystem(currentTask, path + "/" + currentTask.getName(), companyName, projectName);
                        tasks.add(ss);
                        task.setTasks(tasks);
                    }
                }
            } else {
                task.setIcon(3);
                task.setPath(path.substring(1));
            }

        }

        return task;
    }

    public void unzip(HttpServerExchange exchange) throws IOException {

        try {
            ReportDto reportDto = getRequestBody(exchange);
            String token = reportDto.getCustomToken();
            if (token != null && !token.isEmpty() && token.equals(getToken())) {
                sortByParentIdAndOrderInGroup(reportDto);
                String validateReportDtoResult = validateReportDto(reportDto);
                if (validateReportDtoResult != null) {
                    exchange.getResponseSender().send(validateReportDtoResult);
                } else {
                    String validateZipResult = validateZip(reportDto.getArchiveName(), reportDto.getCompanyName());

                    if (validateZipResult != null) {
                        exchange.getResponseSender().send(validateZipResult);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
//                                    downloadZip(reportDto.getArchiveName());
                                    System.out.println("start unzip");
                                    ZipUtil.unpack(new File(rootPath + "/" + reportDto.getArchiveName() + getReportExtension()),
//                                            new File(rootPath + "/" + reportDto.getCompanyName() + "/" + reportDto.getArchiveName()),);
                                            new File(rootPath + "/" + reportDto.getCompanyName() + "/" + reportDto.getArchiveName()), Charset.forName("Cp437"));
                                    System.out.println("finish unzip");
//                                    new File(rootPath + "/" + reportDto.getArchiveName() + getReportExtension()).delete();
                                    System.out.println("delete zip");
                                    System.out.println("start tree web");
                                    Report fullTree = getFullTree(reportDto);
                                    getRealTask(fullTree.getTask(), reportDto.getTasks());
                                    saveIndexHtml(fullTree);
                                    System.out.println("finish tree web");
                                    System.out.println("start zip");
                                    ZipUtil.pack(
                                            new File(rootPath + "/" + reportDto.getCompanyName() + "/" + reportDto.getArchiveName()),
                                            new File(rootPath + "/" + reportDto.getCompanyName() + "/" + reportDto.getArchiveName() + ".zip")
                                    );
                                    System.out.println("finish zip");
                                    System.out.println("start tree zip");
                                    setPathFullPath(fullTree.getTask(), reportDto.getCompanyName() + "/" + reportDto.getArchiveName());
                                    getDeleteNotWantFiles(fullTree);
                                    saveIndexHtml(fullTree);
                                    System.out.println("finish tree zip");
                                    FOLDER_SERVICE.create("/" + reportDto.getCompanyName() + "/" + reportDto.getArchiveName());
                                    System.out.println("create folder db");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("error");
                                }
//                                finally {
//                                    System.out.println("delete zip finally");
//                                    File file = new File(rootPath + "/" + reportDto.getArchiveName() + getReportExtension());
//                                    if (file.exists()){
//                                        file.delete();
//                                    }
//                                }
                            }
                        }).start();
                        sendStatusCode(exchange, OK);
                    }
                }
            } else {
                sendStatusCode(exchange, FORBIDDEN);
            }
        } catch (UnrecognizedPropertyException e) {
            sendStatusCode(exchange, BAD_REQUEST);
        }
    }

    private static boolean existsFolder(String path) {
        return new File(path).exists();
    }

    private static boolean existsZip(String archiveName) {
        return new File(rootPath + "/" + archiveName + ".zip").exists();
    }

//    private static void downloadZip(String fileName) throws IOException {
//        System.out.println("start down "+fileName);
//        URL website = new URL(getDownloadZipURL() + fileName+"&customToken="+getToken());
////        URL website = new URL("https://download.jetbrains.com/idea/ideaIU-2018.3.1.tar.gz");
//        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
//        FileOutputStream fos = new FileOutputStream(getRootPath() + "/" + fileName + getReportExtension());
//        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//        System.out.println("finish down"+fileName);
//    }
}
