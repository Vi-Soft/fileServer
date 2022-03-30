package com.visoft.file.service.service;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.networknt.config.Config;
import com.visoft.file.service.Version;
import com.visoft.file.service.dto.*;
import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.Role;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.User;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.visoft.file.service.service.DI.DependencyInjectionService.*;
import static com.visoft.file.service.service.ErrorConst.*;
import static com.visoft.file.service.service.StatusConst.*;
import static com.visoft.file.service.service.util.EmailService.sendError;
import static com.visoft.file.service.service.util.EmailService.sendSuccess;
import static com.visoft.file.service.service.util.EncoderService.getEncode;
import static com.visoft.file.service.service.util.JWTService.generate;
import static com.visoft.file.service.service.util.PageService.saveIndexHtml;
import static com.visoft.file.service.service.util.PropertiesService.*;
import static com.visoft.file.service.service.util.SenderService.*;
import static org.zeroturnaround.zip.commons.FileUtilsV2_2.deleteQuietly;

@Log4j
public class ReportService {

    private static final String rootPath = getRootPath();

    private static final Map<Long, MultiExport> exportPool = new HashMap<>();

    @Synchronized
    private static ReportDto getRequestBody(HttpServerExchange exchange) {
        log.info(GET_REQUEST_BODY);
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
        log.info(RETURN + ": " + reportDto);
        return reportDto;
    }

    private static String validateReportDto(ReportDto dto) {
        String validateProjectNameResult = validateProjectName(dto);
        if (validateProjectNameResult != null) {
            sendWarn(RETURN, validateProjectNameResult);
            return validateProjectNameResult;
        }
        String validateCompanyNameResult = validateCompanyName(dto);
        if (validateCompanyNameResult != null) {
            sendWarn(RETURN, validateCompanyNameResult);
            return validateCompanyNameResult;
        }
        String validateArchiveNameResult = validateArchiveName(dto);
        if (validateArchiveNameResult != null) {
            sendWarn(RETURN, validateArchiveNameResult);
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
                sendWarn(TASK_NAME_NOT_CORRECT, "id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId);
                return TASK_NAME_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            if (id == null || id.isEmpty()) {
                sendWarn(TASK_ID_NOT_CORRECT, "id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId);
                return TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            ids.add(id);
            if (parentId == null || parentId.isEmpty()) {
                sendWarn(PARENT_TASK_ID_NOT_CORRECT, "id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId);
                return PARENT_TASK_ID_NOT_CORRECT + " id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId;
            }
            if (task.getParentId().equals("-1")) {
                parentIdNullCount++;
            }
            if (orderInGroup == null) {
                sendWarn(ORDER_IN_GROUP_NOT_CORRECT, "id:" + id + "name:" + name + " order:" + orderInGroup + " parentId:" + parentId);
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
        sendInfo(REPORT_DTO_VALIDATION_SUCCESS, dto.getArchiveName());
        return null;
    }


    private static String validateProjectName(ReportDto dto) {
        if (validateName(dto.getProjectName())) {
            sendWarn(RETURN, PROJECT_NAME_NOT_CORRECT);
            return PROJECT_NAME_NOT_CORRECT;
        }
        sendInfo(PROJECT_NAME_VALIDATE_SUCCESS, dto.getProjectName());
        return null;
    }

    private static String validateCompanyName(ReportDto dto) {
        if (validateName(dto.getCompanyName())) {
            sendWarn(RETURN, COMPANY_NAME_NOT_CORRECT);
            return COMPANY_NAME_NOT_CORRECT;
        }
        sendInfo(COMPANY_NAME_VALIDATE_SUCCESS, dto.getCompanyName());
        return null;
    }

    private static String validateArchiveName(ReportDto dto) {
        if (validateName(dto.getArchiveName())) {
            sendWarn(RETURN, ARCHIVE_NAME_NOT_CORRECT);
            return ARCHIVE_NAME_NOT_CORRECT;
        }
        sendInfo(ARCHIVE_NAME_VALIDATE_SUCCESS, dto.getArchiveName());
        return null;
    }

    private static boolean validateName(String name) {
        return name == null || name.equals("");
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
                getRealTask(taskTask, tasks, formTypes, commonLogBookMap, version);
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
                    sendInfo(ALREADY_UNZIP, archiveName);
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

    private static void downloadZip(ReportDto reportDto) throws IOException {
        String fileName = reportDto.getArchiveName();
        sendInfo(START_DOWNLOAD, fileName);
        sendInfo(START_DOWN,  fileName);
        URL website = new URL(reportDto.getUrl() + "?archiveName=" + fileName + "&customToken=" + getToken());
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(getRootPath() + "/" + fileName + getReportExtension());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        sendInfo(FINISH_DOWNLOAD, fileName);
    }

    private static boolean existsFolder(String path) {
        return new File(path).exists();
    }

    public void unzip(HttpServerExchange exchange) throws IOException {
        log.info(START_UNZIP);
        ReportDto reportDto = getRequestBody(exchange);
        log.info(REPORT_DTO + reportDto);

        if (reportDto != null) {
            if (reportDto.getArchiveName() != null) {

                if (reportDto.getTimestamp() != 0) {
                    if (exportPool.size() >= 3) {
                        sendError(reportDto.getArchiveName() + "\n\nPlease wait \nMax downloads already running", reportDto.getEmail());
                        sendWarn(RETURN, MAX_DOWNLOADS_PER_PROJECT);
                        send(exchange, BAD_REQUEST);
                        return;
                    }

                    if (exportPool.entrySet()
                            .stream()
                            .anyMatch(entry ->
                                    entry.getKey() != reportDto.getTimestamp()
                                            && entry.getValue().getProjectName().equals(reportDto.getProjectName())
                            )
                    ) {
                        sendError(reportDto.getArchiveName() + "\n\nPlease wait \nMax downloads per project already running", reportDto.getEmail());
                        sendWarn(RETURN, MAX_DOWNLOADS_PER_PROJECT);
                        send(exchange, BAD_REQUEST);
                        return;
                    }
                }

                if (validateToken(reportDto.getCustomToken())) {
                    String validateReportDtoResult = validateReportDto(reportDto);
                    if (validateReportDtoResult != null) {
                        send(exchange, BAD_REQUEST);
                        sendWarn(NOT_CORRECT_REPORT_DTO, validateReportDtoResult);
                    } else {
                        sortByParentIdAndOrderInGroup(reportDto);
                        sendInfo(SORT_REPORT_DTO, reportDto.getArchiveName());
                        String validateZipResult = validateZip(reportDto.getArchiveName(), reportDto.getCompanyName());
                        sendInfo(VALIDATE_ZIP, reportDto.getArchiveName());
                        if (validateZipResult != null) {
                            send(exchange, BAD_REQUEST);
                            send(exchange, validateZipResult);
                            sendWarn(NOT_CORRECT_ZIP, validateZipResult);
                        } else {
                            new Thread(() -> {
                                try {
                                    downloadZip(reportDto);
                                    sendInfo(START_UNZIP, reportDto.getArchiveName());
                                    String path = rootPath + "/" + reportDto.getArchiveName() + getReportExtension();
                                    String archivePath = Paths.get(
                                            rootPath,
                                            reportDto.getCompanyName(),
                                            reportDto.getArchiveName()
                                    ).toString();

                                    zipUnpack(path, archivePath);

                                    sendInfo(FINISH_UNZIP, reportDto.getArchiveName());

                                    addToMutualArchive(reportDto, path, reportDto.getArchiveName());

                                    removeFile(reportDto.getArchiveName() + getReportExtension());

                                    buildTree(reportDto);

                                    Folder folder = new Folder(
                                            getFolderName(reportDto),
                                            reportDto.getCount() > 1 && exportPool.containsKey(reportDto.getTimestamp())
                                                    ? exportPool.get(reportDto.getTimestamp()).getMutualPath() : null,
                                            reportDto.getProjectName(),
                                            getMainTaskName(reportDto),
                                            Instant.now()
                                    );

                                    FOLDER_SERVICE.create(folder);
                                    sendInfo(FOLDER_CREATED, folder.getFolder());

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
                                            sendInfo(CREATING_NEW_USER + user,
                                                    USER_PASSWORD + randomPassword);

                                            USER_SERVICE.create(user);
                                            Token createdUserToken = new Token(
                                                    generate(ObjectId.get()),
                                                    user.getId()
                                            );

                                            log.info(CREATE_TOKEN);
                                            TOKEN_SERVICE.create(createdUserToken);
                                            sendInfo(TOKEN_CREATED, createdUserToken.toString());
                                        } else {
                                            sendInfo(PASSWORD_CHANGED, randomPassword);
                                            user.getFolders().add(folder.getId());
                                            user.setPassword(getEncode(randomPassword));
                                            USER_SERVICE.update(user, user.getId());
                                            Token token = TOKEN_SERVICE.findByUserId(user.getId());
                                            if (token == null) {
                                                TOKEN_SERVICE.create(new Token(
                                                        generate(ObjectId.get()),
                                                        user.getId())
                                                );
                                            } else {
                                                token.setToken(generate(ObjectId.get()));
                                                token.setExpiration(Instant.now().plusSeconds(10800L));
                                                TOKEN_SERVICE.update(token, token.getId());
                                            }
                                        }

                                        sendInfo(CALCULATED_USER, user.getId().toString());
                                    } else {
                                        sendInfo(REPORT_DTO_PASSWORD, reportDto.getPassword());
                                    }

                                    if (isDownloadDone(reportDto.getTimestamp()))
                                        exportPool.remove(reportDto.getTimestamp());

                                    sendSuccess(
                                            reportDto.getArchiveName(),
                                            reportDto.getEmail(),
                                            reportDto.getVersion(),
                                            randomPassword
                                    );
                                    sendInfo(EMAIL_SEND, reportDto.getEmail());
                                } catch (MongoWriteException e) {
                                    if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                                        String description = "Folder '" + getFolderName(reportDto) + "' already exists";
                                        log.error(description);
                                        sendError(reportDto.getArchiveName() + "\n\n" + description + "\n\nPlease try again later", reportDto.getEmail());
                                        removeFile(Paths.get(reportDto.getCompanyName(), reportDto.getArchiveName()).toString());
                                        removeFile(Paths.get(reportDto.getCompanyName(), reportDto.getArchiveName() + getReportExtension()).toString());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    log.error(e.getMessage());
                                    sendError(reportDto.getArchiveName() + "\n" + e.getMessage(), reportDto.getEmail());
                                    log.error(SEND_ERROR_EMAIL);
                                    removeFile(Paths.get(reportDto.getCompanyName(), reportDto.getArchiveName()).toString());
                                    removeFile(Paths.get(reportDto.getCompanyName(), reportDto.getArchiveName() + getReportExtension()).toString());
                                } finally {
                                    removeFile(reportDto.getArchiveName() + getReportExtension());
                                    exportPool.remove(reportDto.getTimestamp());
                                }
                            }).start();
                            send(exchange, OK);
                            sendInfo(UNZIP_SUCCESS, reportDto.getArchiveName());
                        }
                    }
                } else {
                    sendWarn(RETURN, FORBIDDEN);
                    send(exchange, INVALID_TOKEN, FORBIDDEN);
                }
            } else {
                sendWarn(RETURN, BAD_REQUEST);
                sendError(reportDto.getArchiveName() + "\n" + reportDto.getErrorMassage(), reportDto.getEmail());
                send(exchange, ARCHIVE_NAME_IS_EMPTY, BAD_REQUEST);
            }
        } else {
            sendWarn(RETURN, BAD_REQUEST);
            send(exchange, REPORT_DTO_IS_EMPTY, BAD_REQUEST);
        }
    }

    private String getFolderName(ReportDto reportDto) {
        return "/" + reportDto.getCompanyName() + "/" + reportDto.getArchiveName();
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private void buildTree(ReportDto reportDto) {
        log.info(START_WEB_TREE);

        String archivePath = Paths.get(
                rootPath,
                reportDto.getCompanyName(),
                reportDto.getArchiveName()
        ).toString();

        Map<String, FormType> formTypeMap =
                reportDto.getFormTypes()
                        .parallelStream()
                        .filter(distinctByKey(FormType::getPath))
                        .collect(Collectors.toMap(FormType::getPath, a -> a));
        Map<String, AttachmentDocument> attachmentDocumentMap =
                reportDto.getAttachmentDocuments()
                        .parallelStream()
                        .filter(distinctByKey(AttachmentDocument::getPath))
                        .collect(Collectors.toMap(AttachmentDocument::getPath, a -> a));
        Map<String, CommonLogBook> commonLogBookMap =
                reportDto.getCommonLogBooks()
                        .parallelStream()
                        .filter(distinctByKey(CommonLogBook::getFullPath))
                        .collect(Collectors.toMap(CommonLogBook::getFullPath, a -> a));
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
        log.info(FINISH_WEB_TREE);

        sendInfo(START_ZIP, reportDto.getArchiveName());
        zipPack(archivePath, Paths.get(
                rootPath,
                reportDto.getCompanyName(),
                reportDto.getArchiveName() + ".zip").toString());
        sendInfo(FINISH_ZIP, reportDto.getArchiveName());

        log.info(START_ZIP_TREE);
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
        log.info(FINISH_ZIP_TREE);
    }

    @Synchronized
    private void addToMutualArchive(ReportDto reportDto, String path, String archiveName) {
        if (reportDto.getCount() > 1) {
            String fullMutualPath = Paths.get(
                    rootPath,
                    reportDto.getCompanyName(),
                    String.valueOf(reportDto.getTimestamp())
            ).toString();

            sendInfo(START_UNZIP_FOLDER, fullMutualPath);

            zipUnpack(
                    path,
                    Paths.get(fullMutualPath,
                            archiveName).toString()
            );
            sendInfo(FINISH_UNZIP_FOLDER, fullMutualPath);

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
                    sendInfo(START_ZIP_FOLDER, fullMutualPath);
                    zipPack(
                            fullMutualPath,
                            fullMutualPath + ".zip"
                    );
                    sendInfo(FINISH_ZIP_FOLDER, fullMutualPath);
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

    private void zipPack(String pathFrom, String pathTo) {
        ZipUtil.pack(
                new File(pathFrom),
                new File(pathTo)
        );
    }

    private void zipUnpack(String pathFrom, String pathTo) {
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
        sendInfo(DELETE, path);
    }

    private boolean validateToken(String token) {
        log.info(VALIDATE_TOKEN);
        boolean result = token != null && !token.isEmpty() && token.equals(getToken());
        sendInfo(RETURN, Boolean.toString(result));
        return result;
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


}
