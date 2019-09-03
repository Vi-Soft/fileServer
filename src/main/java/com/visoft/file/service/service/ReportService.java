package com.visoft.file.service.service;

import com.networknt.config.Config;
import com.visoft.file.service.dto.*;
import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.service.util.SenderService;
import io.undertow.server.HttpServerExchange;
import lombok.extern.log4j.Log4j;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.visoft.file.service.service.DI.DependencyInjectionService.FOLDER_SERVICE;
import static com.visoft.file.service.service.ErrorConst.*;
import static com.visoft.file.service.service.util.EmailService.sendError;
import static com.visoft.file.service.service.util.EmailService.sendSuccess;
import static com.visoft.file.service.service.util.PageService.saveIndexHtml;
import static com.visoft.file.service.service.util.PropertiesService.*;
import static org.zeroturnaround.zip.commons.FileUtilsV2_2.deleteQuietly;

@Log4j
public class ReportService {

    private static String rootPath = getRootPath();

    private static ReportDto getRequestBody(HttpServerExchange exchange) {
        log.info("getRequestBody");
        ReportDto reportDto;
        exchange.startBlocking();
        InputStream is = exchange.getInputStream();
        String s = (new Scanner(is, "UTF-8")).useDelimiter("\\A").next();
        try {
            reportDto = Config.getInstance().getMapper().readValue(s, ReportDto.class);
        } catch (IOException e) {
            log.warn(e.getMessage());
            return null;
        }
        log.info(RETURN + reportDto);
        return reportDto;
    }

    private static String validateReportDto(ReportDto dto) {
        String validateProjectNameResult = validateProjectName(dto);
        if (validateProjectNameResult != null) {
            log.warn(RETURN + validateProjectNameResult);
            return validateProjectNameResult;
        }
        String validateCompanyNameResult = validateCompanyName(dto);
        if (validateCompanyNameResult != null) {
            log.warn(RETURN + validateCompanyNameResult);
            return validateCompanyNameResult;
        }
        String validateArchiveNameResult = validateArchiveName(dto);
        if (validateArchiveNameResult != null) {
            log.warn(RETURN + validateArchiveNameResult);
            return validateArchiveNameResult;
        }
        int parentIdNullCount = 0;
        Set<Long> ids = new HashSet<>();
        for (TaskDto task : dto.getTasks()) {
            String name = task.getName();
            Long id = task.getId();
            Long parentId = task.getParentId();
            Long orderInGroup = task.getOrderInGroup();
            Integer icon = task.getIcon();
            if (name == null || name.equals("")) {
                log.warn(TASK_NAME_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId);
                return TASK_NAME_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            if (id == null || id < 1) {
                log.warn(TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId);
                return TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            ids.add(id);
            if (parentId == null || parentId == 0 || parentId < -1) {
                log.warn(PARENT_TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId);
                return PARENT_TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            if (task.getParentId() == -1) {
                parentIdNullCount++;
            }
            if (orderInGroup == null) {
                log.warn(ORDER_IN_GROUP_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId);
                return ORDER_IN_GROUP_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            if (icon == null || icon < 0 || icon > 2) {
                log.warn(ICON_NOT_CORRECT);
                return ICON_NOT_CORRECT;
            }
        }
        if (parentIdNullCount > 1) {
            log.warn(MORE_ONE_EQUALS_PARENT_TASK_ID);
            return MORE_ONE_EQUALS_PARENT_TASK_ID;
        }
        if (ids.size() != dto.getTasks().size()) {
            log.warn(MORE_ONE_EQUALS_TASK_ID);
            return MORE_ONE_EQUALS_TASK_ID;
        }
        log.info(RETURN + SUCCESS);
        return null;
    }


    private static String validateProjectName(ReportDto dto) {
        if (validateName(dto.getProjectName())) {
            log.warn(RETURN + PROJECT_NAME_NOT_CORRECT);
            return PROJECT_NAME_NOT_CORRECT;
        }
        log.info(RETURN + SUCCESS);
        return null;
    }

    private static String validateCompanyName(ReportDto dto) {
        if (validateName(dto.getCompanyName())) {
            log.warn(RETURN + COMPANY_NAME_NOT_CORRECT);
            return COMPANY_NAME_NOT_CORRECT;
        }
        log.info(RETURN + SUCCESS);
        return null;
    }

    private static String validateArchiveName(ReportDto dto) {
        if (validateName(dto.getArchiveName())) {
            log.warn(RETURN + ARCHIVE_NAME_NOT_CORRECT);
            return ARCHIVE_NAME_NOT_CORRECT;
        }
        log.info(RETURN + SUCCESS);
        return null;
    }

    private static boolean validateName(String name) {
        return name == null || name.equals("");
    }

    public void unzip(HttpServerExchange exchange) throws IOException {
        log.info("unzip");
        ReportDto reportDto = getRequestBody(exchange);
        System.out.print(reportDto);
        log.info("report body:\n" + reportDto);
        if (reportDto != null) {
            if (validateToken(reportDto.getCustomToken())) {
                String validateReportDtoResult = validateReportDto(reportDto);
                if (validateReportDtoResult != null) {
                    SenderService.send(exchange, BAD_REQUEST);
                    log.warn("not correct report dto: " + validateReportDtoResult);
                } else {
                    sortByParentIdAndOrderInGroup(reportDto);
                    log.info("sort report dto");
                    String validateZipResult = validateZip(reportDto.getArchiveName(), reportDto.getCompanyName());
                    log.info("validate zip");
                    if (validateZipResult != null) {
                        SenderService.send(exchange, BAD_REQUEST);
                        SenderService.send(exchange, validateZipResult);
                        log.warn("not correct zip: " + validateZipResult);
                    } else {
                        new Thread(() -> {
                            try {
                                downloadZip(reportDto);
                                log.info("start unzip: " + reportDto.getArchiveName());
                                ZipUtil.unpack(
                                        new File(rootPath + "/" + reportDto.getArchiveName() + getReportExtension()),
                                        new File(
                                                Paths.get(
                                                        rootPath,
                                                        reportDto.getCompanyName(),
                                                        reportDto.getArchiveName()
                                                ).toString()
                                        )
                                );
                                log.info("finish unzip: " + reportDto.getArchiveName());
                                removeFile(reportDto.getArchiveName() + getReportExtension());
                                log.info("start tree web");
                                System.out.println(reportDto.getFormTypes().size());
                                Map<String, FormType> formTypeMap = reportDto.getFormTypes().parallelStream().collect(Collectors.toMap(FormType::getPath, a -> a));
                                System.out.println("ddddddd" + formTypeMap);
                                Report fullTree = getFullTree(reportDto);
                                getRealTask(
                                        fullTree.getTask(),
                                        reportDto.getTasks(),
                                        formTypeMap
                                );
                                saveIndexHtml(
                                        fullTree,
                                        formTypeMap,
                                        true
                                );
                                log.info("finish tree web");
                                log.info("start zip: " + reportDto.getArchiveName());
                                System.out.println("start zip" + reportDto.getArchiveName());
                                ZipUtil.pack(
                                        new File(Paths.get(
                                                rootPath,
                                                reportDto.getCompanyName(),
                                                reportDto.getArchiveName()).toString()
                                        ),
                                        new File(Paths.get(
                                                rootPath,
                                                reportDto.getCompanyName(),
                                                reportDto.getArchiveName() + ".zip").toString()
                                        )
                                );
                                log.info("start finish: " + reportDto.getArchiveName());
                                log.info("start tree zip");
                                setPathFullPath(
                                        fullTree.getTask(),
                                        reportDto.getCompanyName() + "/" + reportDto.getArchiveName()
                                );
                                getDeleteNotWantFiles(fullTree);
                                saveIndexHtml(
                                        fullTree,
                                        formTypeMap,
                                        false
                                );
                                log.info("finish tree zip");
                                FOLDER_SERVICE.create(
                                        new Folder(
                                                "/" + reportDto.getCompanyName() + "/" + reportDto.getArchiveName(),
                                                reportDto.getProjectName(),
                                                getMainTaskName(reportDto)

                                        )

                                );
                                log.info("createUser folder db");
                                sendSuccess(reportDto.getArchiveName(), reportDto.getEmail());
                                log.info("send email success ");
                            } catch (Exception e) {
                                log.error(e.getMessage());
                                sendError(reportDto.getArchiveName() + "\n" + e.getMessage(), reportDto.getEmail());
                                log.error("send error email");
                                removeFile(Paths.get(reportDto.getCompanyName(), reportDto.getArchiveName()).toString());
                                removeFile(Paths.get(reportDto.getCompanyName(), reportDto.getArchiveName() + getReportExtension()).toString());
                            } finally {
                                removeFile(reportDto.getArchiveName() + getReportExtension());
                            }
                        }).start();
                        SenderService.send(exchange, OK);
                        log.info("send OK");
                    }
                }
            } else {
                log.warn(RETURN + FORBIDDEN);
                SenderService.send(exchange, FORBIDDEN);
            }
        } else {
            log.warn(RETURN + BAD_REQUEST);
            SenderService.send(exchange, BAD_REQUEST);
        }
    }

    private String getMainTaskName(ReportDto dto){
        for (TaskDto task : dto.getTasks()) {
            if (task.getParentId()==-1){
                return task.getName();
            }
        }
        return null;
    }

    private void removeFile(String path) {
        deleteQuietly(new File(Paths.get(rootPath, path).toString()));
        log.info("delete " + path);

    }

    private boolean validateToken(String token) {
        log.info("validateToken");
        boolean result = token != null && !token.isEmpty() && token.equals(getToken());
        log.info(RETURN + result);
        return result;
    }


    private static Report getFullTree(ReportDto reportDto) {
        Report report = Report.builder()
                .projectName(reportDto.getProjectName())
                .companyName(reportDto.getCompanyName())
                .archiveName(reportDto.getArchiveName())
                .build();
        Task task = new Task(report.getProjectName(), null, new ArrayList<>(), -10000L, 0, null, null);
        task.setPath(reportDto.getCompanyName());
        List<Task> tasks = new ArrayList<>();

        String[] list = new File(rootPath + "/" + report.getCompanyName() + "/" + report.getArchiveName()).list();
        if (list != null && list.length != 0) {
            for (String s : list) {
                Task currentTask = new Task(s, null, new ArrayList<>(), 100000L, 0, null, null);
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

    private static void getRealTask(Task task, List<TaskDto> tasks, Map<String,FormType> formTypes) {
        if (task.getTasks() != null && !task.getTasks().isEmpty()) {
            for (Task taskTask : task.getTasks()) {
                for (TaskDto taskDto : tasks) {
                    FormType formType = new FormTypeService().getFormType(formTypes, taskTask.getPath());
                    if (formType == null) {
                        if (taskTask.getName().equals(taskDto.getId().toString())) {
                            taskTask.setIcon(taskDto.getIcon());
                            taskTask.setName(taskDto.getName());
                            taskTask.setOrderInGroup(taskDto.getOrderInGroup());
                            taskTask.setColor(taskDto.getColor());
                            taskTask.setDetail(taskDto.getDetail());
                        }
                    }else {
                        taskTask.setType(formType.getType());
                    }
                }
                getRealTask(taskTask, tasks, formTypes);
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
                    return ALREADY_UNZIP;
                }
            }
        }
        return null;
    }

    private static void sortByParentIdAndOrderInGroup(List<Task> tasks) {
        tasks.sort(Comparator
                .comparing(Task::getOrderInGroup)
        );
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
                        Task currentTask = new Task(s, null, new ArrayList<>(), 10L, 0, null, null);
                        currentTask.setPath(Paths.get(path, currentTask.getName()).toString());
                        task.setTasks(tasks);
                        Task ss = getTreeByFileSystem(currentTask, path + "/" + currentTask.getName(), companyName, projectName);
                        tasks.add(ss);
                    }
                }
            } else {
                task.setIcon(3);
                task.setPath(path.substring(1));
            }
        }
        return task;
    }

    private static void downloadZip(ReportDto reportDto) throws IOException {
        String fileName = reportDto.getArchiveName();
        log.info("start download: " + fileName);
        System.out.println("start down " + fileName);
        URL website = new URL(reportDto.getUrl() + "?archiveName=" + fileName + "&customToken=" + getToken());
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(getRootPath() + "/" + fileName + getReportExtension());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        log.info("finish download: " + fileName);
    }

    private static boolean existsFolder(String path) {
        return new File(path).exists();
    }


}