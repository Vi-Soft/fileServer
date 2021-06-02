package com.visoft.file.service.service;

import com.networknt.config.Config;
import com.visoft.file.service.Version;
import com.visoft.file.service.dto.*;
import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.Role;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.User;
import com.visoft.file.service.service.util.SenderService;
import io.undertow.server.HttpServerExchange;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
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
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.visoft.file.service.service.DI.DependencyInjectionService.*;
import static com.visoft.file.service.service.ErrorConst.*;
import static com.visoft.file.service.service.util.EmailService.sendError;
import static com.visoft.file.service.service.util.EmailService.sendSuccess;
import static com.visoft.file.service.service.util.EncoderService.getEncode;
import static com.visoft.file.service.service.util.JWTService.generate;
import static com.visoft.file.service.service.util.PageService.saveIndexHtml;
import static com.visoft.file.service.service.util.PropertiesService.*;
import static org.zeroturnaround.zip.commons.FileUtilsV2_2.deleteQuietly;

@Log4j
public class ReportService {

    private static final String rootPath = getRootPath();

    private static final Map<Long, MultiExport> exportPool = new HashMap<>();

    @Synchronized
    private static ReportDto getRequestBody(HttpServerExchange exchange) {
        log.info("getRequestBody");
        ReportDto reportDto;
        exchange.startBlocking();
        InputStream is = exchange.getInputStream();
        String s = (new Scanner(is, "UTF-8")).useDelimiter("\\A").next();
        try {
            reportDto = Config.getInstance().getMapper().readValue(s, ReportDto.class);
        } catch (Exception e) {
            log.warn(e.getMessage());
            reportDto = new ReportDto();
            try {
                reportDto.setEmail(Config.getInstance().getMapper().readValue(s, EmailReportDto.class).getEmail());
                reportDto.setErrorMassage(e.getMessage());
                return reportDto;
            } catch (IOException ioException) {
                log.warn(ioException.getMessage());
                return null;
            }
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
        Set<String> ids = new HashSet<>();
        for (TaskDto task : dto.getTasks()) {
            String name = task.getName();
            String id = task.getId();
            String parentId = task.getParentId();
            Long orderInGroup = task.getOrderInGroup();
            Integer icon = task.getIcon();
            if (name == null || name.isEmpty()) {
                log.warn(TASK_NAME_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId);
                return TASK_NAME_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            if (id == null || id.isEmpty()) {
                log.warn(TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId);
                return TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            ids.add(id);
            if (parentId == null || parentId.isEmpty()) {
                log.warn(PARENT_TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId);
                return PARENT_TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            if (task.getParentId().equals("-1")) {
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

    public void unzip(HttpServerExchange exchange) throws IOException{
        log.info("unzip");
        ReportDto reportDto = getRequestBody(exchange);

        if (reportDto != null) {
            if (reportDto.getArchiveName() != null) {

                if (exportPool.size() >= 3)
                    sendError(reportDto.getArchiveName() + "\nMax downloads already running, please wait", reportDto.getEmail());

                if (exportPool.entrySet()
                        .stream()
                        .anyMatch(entry ->
                                entry.getKey() != reportDto.getTimestamp()
                                        && entry.getValue().getProjectName().equals(reportDto.getProjectName())
                        ))
                    sendError(reportDto.getArchiveName() + "\nMax downloads per project already running, please wait", reportDto.getEmail());

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
                                    String path = rootPath + "/" + reportDto.getArchiveName() + getReportExtension();
                                    String archivePath = Paths.get(
                                            rootPath,
                                            reportDto.getCompanyName(),
                                            reportDto.getArchiveName()
                                    ).toString();

                                    zipUnpack(path, archivePath);

                                    log.info("finish unzip: " + reportDto.getArchiveName());

                                    addToMutualArchive(reportDto, path, reportDto.getArchiveName());

                                    removeFile(reportDto.getArchiveName() + getReportExtension());

                                    buildTree(reportDto);

                                    Folder folder = new Folder(
                                            "/" + reportDto.getCompanyName() + "/" + reportDto.getArchiveName(),
                                            reportDto.getCount() > 1 && exportPool.containsKey(reportDto.getTimestamp())
                                                    ? exportPool.get(reportDto.getTimestamp()).getMutualPath() : null,
                                            reportDto.getProjectName(),
                                            getMainTaskName(reportDto),
                                            Instant.now()
                                    );

                                    FOLDER_SERVICE.create(folder);
                                    log.info("created folder db");

                                    String randomPassword = null;

                                    if (reportDto.getPassword() != null) {
                                        User user = USER_SERVICE.findByLogin(reportDto.getEmail());
                                        randomPassword = USER_SERVICE.getRandomPassword();
                                        if (user == null) {
                                            user = new User(
                                                    reportDto.getEmail(),
                                                    getEncode(randomPassword),
                                                    Role.USER,
                                                    Collections.singletonList(folder.getId())
                                            );
                                            USER_SERVICE.create(user);
                                            Token createdUserToken = new Token(
                                                    generate(ObjectId.get()),
                                                    user.getId()
                                            );
                                            TOKEN_SERVICE.create(createdUserToken);
                                        } else {
                                            user.getFolders().add(folder.getId());
                                            user.setPassword(getEncode(randomPassword));
                                            USER_SERVICE.update(user, user.getId());
                                        }

                                        log.info("calculated user");
                                    }

                                    if (isDownloadDone(reportDto.getTimestamp()))
                                        exportPool.remove(reportDto.getTimestamp());

                                    sendSuccess(
                                            reportDto.getArchiveName(),
                                            reportDto.getEmail(),
                                            reportDto.getVersion(),
                                            randomPassword
                                    );
                                    log.info("send email success ");
                                } catch (Exception e) {
                                    e.printStackTrace();
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
                sendError(reportDto.getArchiveName() + "\n" + reportDto.getErrorMassage(), reportDto.getEmail());
                SenderService.send(exchange, BAD_REQUEST);
            }
        } else {
            log.warn(RETURN + BAD_REQUEST);
            SenderService.send(exchange, BAD_REQUEST);
        }

    }

    private void buildTree(ReportDto reportDto) {
        log.info("start tree web");

        String archivePath = Paths.get(
                rootPath,
                reportDto.getCompanyName(),
                reportDto.getArchiveName()
        ).toString();

        Map<String, FormType> formTypeMap = reportDto.getFormTypes().parallelStream().collect(Collectors.toMap(FormType::getPath, a -> a));
        Map<String, AttachmentDocument> attachmentDocumentMap = reportDto.getAttachmentDocuments().parallelStream().collect(Collectors.toMap(AttachmentDocument::getPath, a -> a));
        Map<String, CommonLogBook> commonLogBookMap = reportDto.getCommonLogBooks().parallelStream().collect(Collectors.toMap(CommonLogBook::getFullPath, a -> a));
        Report fullTree = getFullTree(reportDto);
        getRealTask(
                fullTree.getTask(),
                reportDto.getTasks(),
                formTypeMap,
                commonLogBookMap,
                reportDto.getVersion()
        );
        saveIndexHtml(
                fullTree,
                formTypeMap,
                attachmentDocumentMap,
                true
        );
        log.info("finish tree web");

        log.info("start zip: " + reportDto.getArchiveName());
        zipPack(archivePath, Paths.get(
                rootPath,
                reportDto.getCompanyName(),
                reportDto.getArchiveName() + ".zip").toString());
        log.info("finish zip: " + reportDto.getArchiveName());

        log.info("start tree zip");
        setPathFullPath(
                fullTree.getTask(),
                reportDto.getCompanyName() + "/" + reportDto.getArchiveName()
        );
        getDeleteNotWantFiles(fullTree);
        saveIndexHtml(
                fullTree,
                formTypeMap,
                attachmentDocumentMap,
                false
        );
        log.info("finish tree zip");
    }

    @Synchronized
    private void addToMutualArchive(ReportDto reportDto, String path, String archiveName) {
        if (reportDto.getCount() > 1) {
            log.info("start unzip to mutual folder");
            String fullMutualPath = Paths.get(
                    rootPath,
                    reportDto.getCompanyName(),
                    String.valueOf(reportDto.getTimestamp())
            ).toString();

            zipUnpack(
                    path,
                    Paths.get(fullMutualPath,
                            archiveName).toString()
            );
            log.info("finish unzip to mutual folder");
            if (!exportPool.containsKey(reportDto.getTimestamp())) {
                exportPool.put(reportDto.getTimestamp(), MultiExport.builder()
                        .size(reportDto.getCount())
                        .mutualPath(
                                "/" +
                                reportDto.getCompanyName() +
                                "/" +
                                reportDto.getTimestamp()
                        )
                        .projectName(reportDto.getProjectName())
                        .count(1)
                        .paths(new ArrayList<>(Collections.singletonList(path)))
                        .build());
            } else {
                MultiExport multiExport = exportPool.get(reportDto.getTimestamp());
                multiExport.getPaths().add(path);
                multiExport.setCount(multiExport.getCount() + 1);

                if (isDownloadDone(reportDto.getTimestamp())) {
                    log.info("start zip mutual folder");
                    zipPack(
                            fullMutualPath,
                            fullMutualPath + ".zip"
                    );
                    log.info("finish zip mutual folder");
                }

            }

        } else {
            exportPool.put(reportDto.getTimestamp(), MultiExport.builder()
                    .size(1)
                    .projectName(reportDto.getProjectName())
                    .count(1)
                    .build());
        }
    }

    private boolean isDownloadDone(Long timeStamp) {
        MultiExport multiExport = exportPool.get(timeStamp);
        if (multiExport != null)
            return multiExport.getCount() == multiExport.getSize();
        return true;
    }

    private void zipPack (String pathFrom, String pathTo) {
        ZipUtil.pack(
                new File(pathFrom),
                new File(pathTo)
        );
    }

    private void zipUnpack (String pathFrom, String pathTo) {
        ZipUtil.unpack(
                new File(pathFrom),
                new File(pathTo)
        );
    }

    private String getMainTaskName(ReportDto dto) {
        for (TaskDto task : dto.getTasks()) {
            if (task.getParentId().equals("-1")) {
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

    private static void getRealTask(
            Task task,
            List<TaskDto> tasks,
            Map<String, FormType> formTypes,
            Map<String, CommonLogBook> commonLogBookMap,
            Version version) {
        if (task.getTasks() != null && !task.getTasks().isEmpty()) {
            for (Task taskTask : task.getTasks()) {
                if (version == Version.RU) {
                    CommonLogBook commonLogBook = new CommonLogBookService().getCommonLogBook(commonLogBookMap, taskTask.getPath());
                    if (commonLogBook != null) {
                        taskTask.setName(commonLogBook.getFullName());
                        taskTask.setOrderInGroup(commonLogBook.getOrderInGroup());
                    }
                }
                for (TaskDto taskDto : tasks) {
                    FormType formType = new FormTypeService().getFormType(formTypes, taskTask.getPath());
                    if (formType == null) {
                        if (taskTask.getName().equals(taskDto.getId())) {
                            taskTask.setIcon(taskDto.getIcon());
                            taskTask.setName(taskDto.getName());
                            taskTask.setOrderInGroup(taskDto.getOrderInGroup());
                            taskTask.setColor(taskDto.getColor());
                            taskTask.setDetail(taskDto.getDetail());
                        }
                    } else {
                        taskTask.setType(formType.getType());
                    }
                }
                getRealTask(taskTask, tasks, formTypes,commonLogBookMap,version);
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