package com.visoft.files.service.util;

import com.visoft.files.dto.Report;
import com.visoft.files.dto.Task;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.*;
import java.util.List;
import java.util.Objects;

public class PageService {

    private static String rootPath = PropertiesService.getRootPath();

    public static void redirect(HttpServerExchange exchange, String redirectUrl) {
        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"refresh\" content=\"0; url=");
        htmlString.append(redirectUrl);
        htmlString.append(" />\n" +
                "</head>\n" +
                "</html>");
        exchange.getResponseSender().send(htmlString.toString());
    }

    public static void getMainUserHtml(HttpServerExchange exchange, List<String> folders) {
        StringBuilder htmlString = new StringBuilder();
        htmlString.append(
                "<!DOCTYPE HTML>\n" +
                        "<html>\n" +
                        " <head>\n" +
                        "  <meta charset=\"utf-8\">\n" +
                        "  <title>Main</title>\n" +
                        " </head>\n" +
                        " <body>\n"
        );
        for (String folder : folders) {
            htmlString.append("<p><a href=\"" + folder + "\">" + getFolderName(folder) + "</a></p>\n");
        }

        htmlString.append("</body>\n" +
                "</html>\n");
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
        exchange.getResponseSender().send(htmlString.toString());
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
            htmlString = sb.toString().replace("r.png",PropertiesService.getServerName()+"/api/r");
            htmlString = htmlString.replace("g.png",PropertiesService.getServerName()+"/api/g");
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
        exchange.getResponseSender().send(htmlString);
    }

    public static void saveIndexHtml(Report tree) throws FileNotFoundException {
        StringBuilder htmlString = new StringBuilder();
        htmlString.append(
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                        "<style>\n" +
                        "ul, #myUL {\n" +
                        "  list-style-type: none;\n" +
                        "}\n" +
                        "\n" +
                        "#red{\n" +
                        "color: red\n" +
                        "}\n" +
                        "\n" +
                        "#grey{\n" +
                        "  color: grey\n" +
                        "}" +
                        "#myUL {\n" +
                        "  margin: 0;\n" +
                        "  padding: 0;\n" +
                        "}\n" +
                        "\n" +
                        ".box, .paperclip {\n" +
                        "  cursor: pointer;\n" +
                        "  -webkit-user-select: none; /* Safari 3.1+ */\n" +
                        "  -moz-user-select: none; /* Firefox 2+ */\n" +
                        "  -ms-user-select: none; /* IE 10+ */\n" +
                        "  user-select: none;\n" +
                        "}\n" +
                        ".box::before {\n" +
                        "  content: \"\\2610\";\n" +
                        "  color: black;\n" +
                        "  display: inline-block;\n" +
                        "  margin-right: 6px;\n" +
                        "}\n" +
                        "\n" +
                        ".check-box::before {\n" +
                        "  content: \"\\2611\"; \n" +
                        "  color: dodgerblue;\n" +
                        "}\n" +
                        "\n" +
                        ".nested {\n" +
                        "  display: none;\n" +
                        "}\n" +
                        "\n" +
                        ".active {\n" +
                        "  display: block;\n" +
                        "}\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n"

        );
        htmlString.append("<h2>" + tree.getProjectName() + "</h2>");
        htmlString.append("<ul id=\"myUL\">");
        htmlString.append(" <li><span class=\"box\">" + tree.getTask().getName() + "</span>");
        htmlString.append(getHtmlTree(tree.getTask()));
        htmlString.append("</li>");
        htmlString.append("</ul>");
        htmlString.append("<script>\n" +
                "var toggler = document.getElementsByClassName(\"box\");\n" +
                "var i;\n" +
                "for (i = 0; i < toggler.length; i++) {\n" +
                "  toggler[i].addEventListener(\"click\", function() {\n" +
                "    this.parentElement.querySelector(\".nested\").classList.toggle(\"active\");\n" +
                "    this.classList.toggle(\"check-box\");\n" +
                "  });\n" +
                "}\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>");
        String pathToProject = rootPath + "/" + tree.getCompanyName() + "/" + tree.getArchiveName();
        saveIndexHtml(htmlString.toString(), pathToProject);
        copyImageToProjectFolder(pathToProject);
    }

    private static void saveIndexHtml(String indexHtmlBody, String path) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(path + "/index.html")) {
            out.println(indexHtmlBody);
        }
    }

    private static String getHtmlTree(Task mainTask) {
        String htmlTree = "<ul class=\"nested\">\n";
        for (Task task : mainTask.getTasks()) {
            if (task.getTasks() != null) {
                htmlTree = htmlTree + "<li>";
                htmlTree = htmlTree + addImage(task);
                htmlTree = htmlTree + "<span class=\"box\"";
                htmlTree = addColorId(htmlTree, task);
                htmlTree = htmlTree + ">" + task.getName() + "</span>";
            } else {
                htmlTree = htmlTree + "<li";
                htmlTree = addColorId(htmlTree, task);
                htmlTree = htmlTree + ">" + addImage(task) + task.getName() + "</li>\n";
            }
            if (task.getTasks() != null) {
                htmlTree = htmlTree + getHtmlTree(task);
            }

        }
        htmlTree = htmlTree + "</li></ul>\n";
        return htmlTree;
    }

    private static String addColorId(String htmlTree, Task task) {
        switch (task.getIcon()) {
            case 1:
                htmlTree = htmlTree + " id= \"grey\"";
                break;
            case 2:
                htmlTree = htmlTree + " id= \"red\"";
                break;
        }
        return htmlTree;
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

    private static void copyImageToProjectFolder(String path) {
        try (BufferedInputStream gOriginal = new BufferedInputStream(Objects.requireNonNull(PageService.class.getClassLoader().getResourceAsStream("image/g.png")));
             BufferedInputStream rOriginal = new BufferedInputStream(Objects.requireNonNull(PageService.class.getClassLoader().getResourceAsStream("image/r.png")));
             BufferedOutputStream gCopy = new BufferedOutputStream(new FileOutputStream(new File(path + "/g.png")));
             BufferedOutputStream rCopy = new BufferedOutputStream(new FileOutputStream(new File(path + "/r.png")))
        ) {
            int b = 0;
            while (b != -1) {
                b = gOriginal.read();
                gCopy.write(b);
            }
            b = 0;
            while (b != -1) {
                b = rOriginal.read();
                rCopy.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFolderName(String path) {
        String[] split = path.split("/");
        return split[split.length - 1];
    }

//    private static String getHtmlTree(Task mainTask) {
//        String htmlTree = "<ul class=\"nested\">\n";
//        for (Task task : mainTask.getTasks()) {
//            if (task.getTasks() != null) {
//                htmlTree = htmlTree + "<li><span class=\"box\"";
//                htmlTree = addColorId(htmlTree, task);
//                htmlTree = htmlTree + ">" + task.getName() + "</span>";
//            } else {
//                htmlTree = htmlTree + "<li";
//                htmlTree = addColorId(htmlTree, task);
//                htmlTree = htmlTree + ">" + task.getName() + "</li>\n";
//            }
//            if (task.getTasks() != null) {
//                htmlTree = htmlTree + getHtmlTree(task);
//            }
//
//        }
//        htmlTree = htmlTree + "</li></ul>\n";
//        return htmlTree;
//    }
//
//    private static String addColorId(String htmlTree, Task task) {
//        switch (task.getIcon()) {
//            case 1:
//                htmlTree = htmlTree + " id= \"grey\"";
//                break;
//            case 2:
//                htmlTree = htmlTree + " id= \"red\"";
//                break;
//        }
//        return htmlTree;
//    }
}
