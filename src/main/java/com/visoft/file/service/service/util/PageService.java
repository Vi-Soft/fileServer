package com.visoft.file.service.service.util;

import com.visoft.file.service.dto.*;
import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.service.AttachmentDocumentService;
import com.visoft.file.service.service.FormTypeService;
import com.visoft.file.service.util.TaskSorter;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.extern.log4j.Log4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.visoft.file.service.service.util.PropertiesService.getStaticServer;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Log4j
public class PageService {

    private static String rootPath = PropertiesService.getRootPath();

    private static String loginHtml = PropertiesService.getLoginPage();

    private static String server = PropertiesService.getServerName();

    private static String staticServer = PropertiesService.getStaticServer();

    public static void redirectToLoginPage(HttpServerExchange exchange) {
        exchange
                .getResponseSender()
                .send(
                        getLoginRedirectPage()
                                .replace(
                                        "content=\"\"",
                                        "content=\"0; url=" + loginHtml + "\""
                                )
                );
    }

    public static void getMainUserHtml(HttpServerExchange exchange, List<Folder> folders) {
        StringBuilder htmlString = new StringBuilder();
        for (Folder folder : folders) {
            htmlString
                    .append("<div>\n")
                    .append("    <a href=\"")
                    .append(folder.getFolder())
                    .append("\">\n")
                    .append("        <table class=\"btn\">\n")
                    .append("            <tr>\n")
                    .append("                <td>")
                    .append(getFolderName(folder.getFolder()))
                    .append("</td>\n")
                    .append("                <td>&nbsp;/&nbsp;</td>\n")
                    .append("                <td>")
                    .append(folder.getProjectName())
                    .append("</td>\n").append("                <td>&nbsp;/&nbsp;</td>\n")
                    .append("                <td>")
                    .append(folder.getTaskName())
                    .append("</td>\n")
                    .append("            </tr>\n")
                    .append("        </table>\n")
                    .append("    </a>\n")
                    .append("</div>");
        }
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
        exchange.getResponseSender().send(
                getRootPage()
                        .replace(
                                "replaceServer=\"\"",
                                "href=\"" + staticServer + "/root.css" + "\""
                        )
                        .replace(
                                "src=\"\"",
                                "src=\"" + staticServer + "/imageLogout.png\""
                        )
                        .replace(
                                "action=\"\"",
                                "action=\"" + server + "/api/logout\""
                        )
                        .replace(
                                "<replace/>",
                                htmlString.toString()
                        )
        );
    }

    public static void getFolderUserHtml(HttpServerExchange exchange, String folder) throws IOException {
        String htmlString;
        try (BufferedReader br = new BufferedReader(new FileReader(rootPath + folder + "/index.html"))) {

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            htmlString = sb.toString().replace("r.png", getStaticServer() + "/r.png");
            htmlString = htmlString.replace("g.png", getStaticServer() + "/g.png");
            htmlString = htmlString.replace("background.jpg", getStaticServer() + "/background.jpg");
            htmlString = htmlString.replace("imageDownload.png", getStaticServer() + "/imageDownload.png");
            htmlString = htmlString.replace("imageLogout.png", getStaticServer() + "/imageLogout.png");
            htmlString = htmlString.replace("tree.css", getStaticServer() + "/tree.css");
            htmlString = htmlString.replace("main.js", getStaticServer() + "/main.js");
            htmlString = htmlString.replace("toggler.js", getStaticServer() + "/toggler.js");
        }
        String[] split = htmlString.split("<body>");
        htmlString = split[0] +
                "<body>\n" +
                "<div class=\"wrapper\">\n" +
                "  <div class=\"header\">\n" +
                "    <div class=\"exit\">\n" +
                "      <form action=\"" + server + "/api/logout\" method=\"post\">\n" +
                "        <input type=\"image\" src=\"" + staticServer + "/imageLogout.png\" alt=\"Logout\" />\n" +
                "      </form>\n" +
                "      </div>\n" +
                "      <div class=\"logo\">Download Client</div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<a href=\"" + server + folder + ".zip\" >\n" +
                "\t\t<button class=\"btn\"> <h2>Download</h2></button></p>\n" +
                "\t</a>" +
                split[1];
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
        exchange.getResponseSender().send(htmlString);
    }

    public static void saveIndexHtml(
            Report tree,
            Map<String, FormType> formTypes,
            Map<String, AttachmentDocument> attachmentDocumentMap,
            boolean forArchive
    ) {
        log.info("start saving html");
        String pathToProject = rootPath + "/" + tree.getCompanyName() + "/" + tree.getArchiveName();
        String treeHtml = getTreePage()
                .replace(
                        "projectName",
                        tree.getProjectName()
                )
                .replace(
                        "<taskTree/>",
                        getHtmlTree(
                                tree.getTask(),
                                formTypes,
                                attachmentDocumentMap,
                                forArchive,
                                (tree.getCompanyName() + tree.getArchiveName()).length() + 2)
                );
        saveIndexHtml(treeHtml, pathToProject);
        copyFilesToProjectFolder(pathToProject);
        log.info("finish saving html");
    }

    private static void saveIndexHtml(String indexHtmlBody, String path) {
        try (PrintWriter out = new PrintWriter(path + "/index.html")) {
            out.println(indexHtmlBody);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String getHtmlTree(
            Task mainTask,
            Map<String, FormType> formTypes,
            Map<String, AttachmentDocument> attachmentDocumentMap,
            boolean forArchive,
            int startPathWith
    ) {
        TaskSorter.byTaskName(mainTask.getTasks());
        String htmlTree = "<ul class=\"nested\">\n";
        for (Task task : mainTask.getTasks()) {
            if (task.getTasks() != null && !task.getTasks().isEmpty()) {
                htmlTree = htmlTree + "<li>";
                htmlTree = htmlTree + addImage(task);
                htmlTree = htmlTree + "<span class=\"box\"";
                htmlTree = addNameColor(htmlTree, task);
                htmlTree = htmlTree + ">" + task.getName() + getDetails(task) + "</span>";
            } else {
                htmlTree = htmlTree + "<li";
                if (task.getIcon() == 3) {
                    String[] split = task.getPath().split("/");
                    String classWithId = "";
                    String path = null;
                    if (split.length > 1) {
                        FormType formType;
                        if (forArchive) {
                            path = Paths.get("/" + task.getPath()).toString()
                                    .substring(
                                            0,
                                            task
                                                    .getPath()
                                                    .length() - split[split.length - 1]
                                                    .length()
                                    );
                            formType = new FormTypeService()
                                    .getFormType(
                                            formTypes,
                                            path
                                    );
                        } else {
                            path = Paths.get(task.getPath()).toString()
                                    .substring(
                                            0,
                                            task
                                                    .getPath()
                                                    .length() - 1 - split[split.length - 1]
                                                    .length()
                                    ).substring(startPathWith);
                            formType = new FormTypeService()
                                    .getFormType(
                                            formTypes,
                                            path
                                    );
                        }
                        if (formType != null) {
                            classWithId = formType.getType().getValue() + "-" + split[split.length - 2];
                        } else {
                            log.warn("Not formType path: " + path);
                        }
                    }
                    AttachmentDocument attachmentDocument = null;
                    if (path != null) {
                        attachmentDocument =
                                new AttachmentDocumentService()
                                        .getAttachmentDocument(
                                                attachmentDocumentMap,
                                                Paths.get(
                                                        path,
                                                        task.getName()
                                                ).toString()
                                        );
                    }
                    if (attachmentDocument == null) {
                        log.warn("Not found attachment document path: " + Paths.get(
                                path,
                                task.getName()
                        ).toString());
                        htmlTree = htmlTree + "><a class=\"" + classWithId + "\" href=\""
                                + task.getPath()
                                + "\" target=\"_blank\" type=\"1\" description=\"1\" certificate=\"1\" comment=\"1\" uploadDate=\"1\" fileName=\"1\">"
                                + task.getName()
                                + "</a></li>\n";
                    } else {
                        htmlTree = htmlTree + "><a class=\"" + classWithId + "\" href=\""
                                + task.getPath()
                                + "\" target=\"_blank\""
                                + "\" type=\"" + attachmentDocument.getType()
                                + "\" description=\"" + attachmentDocument.getDescription()
                                + "\" certificate=\"" + attachmentDocument.getCertificate()
                                + "\" comment=\"" + attachmentDocument.getComment()
                                + "\" uploadDate=\"" + attachmentDocument.getUploadDate()
                                + "\" fileName=\"" + attachmentDocument.getFileName()
                                + "\">" + task.getName() + "</a></li>\n";
                    }

                } else {
                    htmlTree = addNameColor(htmlTree, task);
                    htmlTree = htmlTree + ">" + addImage(task) + task.getName() + "</li>\n";
                }
            }
            if (task.getTasks() != null) {
                htmlTree = htmlTree + getHtmlTree(task, formTypes, attachmentDocumentMap, forArchive, startPathWith);
            }

        }
        htmlTree = htmlTree + "</li></ul>\n";
        return htmlTree;
    }

    private static String addNameColor(String htmlTree, Task task) {
        String color = "black";
        if (task.getColor() == null) {
            switch (task.getIcon()) {
                case 1:
                    color = "grey";
                    break;
                case 2:
                    color = "red";
            }
        } else {
            color = task.getColor();
        }
        return htmlTree + " style=\"color: " + color + "\"";
    }

    private static String getDetails(Task task) {
        StringBuilder htmlDetails = new StringBuilder();
        if (task.getDetail() != null && !task.getDetail().isEmpty()) {
            for (Map.Entry<String, String> entry : task.getDetail().entrySet()) {
                htmlDetails.append("<span lass=\"box\" style=\"color: ").append(entry.getValue()).append("\">").append(entry.getKey()).append("</span>");
            }
        }
        return htmlDetails.toString();
    }

    private static String addImage(Task task) {
        switch (task.getIcon()) {
            case 1:
                return "<img src=\"g.png\" width=\"15\" height=\"15\" alt=\"grey\">";
            case 2:
                return "<img src=\"r.png\" width=\"15\" height=\"15\" alt=\"red\">";
        }
        return "";
    }

    private static void copyFilesToProjectFolder(String path) {
        copyFileToProjectFolder("file/g.png", path + "/g.png");
        copyFileToProjectFolder("file/r.png", path + "/r.png");
        copyFileToProjectFolder("file/background.jpg", path + "/background.jpg");
        copyFileToProjectFolder("front/css/tree.css", path + "/tree.css");
        copyFileToProjectFolder("front/js/main.js", path + "/main.js");
        copyFileToProjectFolder("front/js/toggler.js", path + "/toggler.js");
    }

    private static void copyFileToProjectFolder(String pathFrom, String pathTo) {
        try {
            Files.copy(Objects.requireNonNull(PageService.class.getClassLoader().getResourceAsStream(pathFrom)), Paths.get(pathTo), REPLACE_EXISTING);
        } catch (IOException e) {
            log.warn(e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getFolderName(String path) {
        String[] split = path.split("/");
        return split[split.length - 1];
    }

    private static String getLoginRedirectPage() {
        return convertHtmlToString("front/html/loginRedirectPage.html");
    }

    private static String getTreePage() {
        return convertHtmlToString("front/html/tree.html");
    }

    private static String getRootPage() {
        return convertHtmlToString("front/html/root.html");
    }

    private static String convertHtmlToString(String file) {
        InputStream resourceAsStream = PageService.class.getClassLoader().getResourceAsStream(file);
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(resourceAsStream),
                            StandardCharsets.UTF_8
                    )
            );
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
            return null;
        }
        return contentBuilder.toString();
    }
}