package com.visoft.file.service.service.util;

import com.visoft.file.service.dto.Report;
import com.visoft.file.service.dto.Task;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.extern.log4j.Log4j;

import java.io.*;
import java.util.List;
import java.util.Objects;

@Log4j
public class PageService {

    private static String rootPath = PropertiesService.getRootPath();

    private static String loginHtml = PropertiesService.getLoginPage();

    private static String server = PropertiesService.getServerName();

    public static void redirectToLoginPage(HttpServerExchange exchange) {
        String htmlString = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"refresh\" content=\"0; url=" + loginHtml + "\"/>\n" +
                "</head>\n" +
                "</html>";
        exchange.getResponseSender().send(htmlString);
    }

    public static void getMainUserHtml(HttpServerExchange exchange, List<String> folders) {
        StringBuilder htmlString = new StringBuilder();
        htmlString.append(
                "<!DOCTYPE HTML>\n" +
                        "<html>\n" +
                        " <head>\n" +
                        "  <meta charset=\"utf-8\">\n" +
                        "  <title>Root</title>\n" +
                        " </head>\n" +
                        " <body>\n" +
                        "<form action=\"" + server + "/api/logout\" method=\"post\">\n" +
                        "    <input type=\"submit\" value=\"Logout\" />\n" +
                        "</form>"
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
            htmlString = sb.toString().replace("r.png", PropertiesService.getServerName() + "/static/r");
            htmlString = htmlString.replace("g.png", PropertiesService.getServerName() + "/static/g");
            htmlString = htmlString.replace("highlight.js", PropertiesService.getServerName() + "/static/getHighlight");
            htmlString = htmlString.replace("jquery.js", PropertiesService.getServerName() + "/static/getScrollToMin");
            htmlString = htmlString.replace("scrollTo-min.js", PropertiesService.getServerName() + "/static/scrollTo-min");
        }
        String[] split = htmlString.split("<body>");
        htmlString = split[0] +
                "<body>\n" +
                "<form action=\"" + server + "/api/logout\" method=\"post\">\n" +
                "    <input type=\"submit\" value=\"Logout\" />\n" +
                "</form>" +
                "<a href=\"" + PropertiesService.getServerName() + folder + ".zip\">\n" +
                "\t\t<button>Download</button></p>\n" +
                "\t</a>" +
                split[1];
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
        exchange.getResponseSender().send(htmlString);
    }

    public static void saveIndexHtml(Report tree) throws FileNotFoundException {
        log.info("report: " + tree);
        StringBuilder htmlString = new StringBuilder();
        htmlString.append(
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                        "<style>\n" +
                        "ul, #myUL {\n" +
                        "list-style-type: none;\n" +
                        "}\n" +
                        "#text{\n" +
                        "margin:0 23% 0;\n" +
                        "}\n" +
                        ".highlight {\n" +
                        "background-color: rgba(255, 255, 153, 0.404);\n" +
                        "}\n" +
                        ".selectHighlight {\n" +
                        "background-color:rgba(255, 225, 0, 0.479); \n" +
                        "background-color:rgba(255, 225, 0, 0.479); \n" +
                        "-moz-border-radius: 2px; / FF1+ / \n" +
                        "-webkit-border-radius: 1px; / Saf3-4 / \n" +
                        "border-radius: 1 px; / Opera 10.5, IE 9, Saf5, Chrome / \n" +
                        "-moz-box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7); / FF3.5+ / \n" +
                        "-webkit-box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7); / Saf3.0+, Chrome / \n" +
                        "box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7); / Opera 10.5+, IE 9.0 / \n" +
                        "/ padding:0px 0px; /\n" +
                        "margin:0 0px;\n" +
                        "color:#ff0000;\n" +
                        "}\n" +
                        ".finded{\n" +
                        "color:white;background: #8A8A7B;\n" +
                        "}\n" +
                        "#red{\n" +
                        "color: red\n" +
                        "}\n" +
                        "\n" +
                        "#grey{\n" +
                        "color: grey\n" +
                        "}#gold{\n" +
                        "color: gold\n" +
                        "}#myUL {\n" +
                        "margin: 0;\n" +
                        "padding: 0;\n" +
                        "}\n" +
                        "\n" +
                        ".box, .paperclip {\n" +
                        "cursor: pointer;\n" +
                        "-webkit-user-select: none; / Safari 3.1+ /\n" +
                        "-moz-user-select: none; / Firefox 2+ /\n" +
                        "-ms-user-select: none; / IE 10+ /\n" +
                        "user-select: none;\n" +
                        "}\n" +
                        ".box::before {\n" +
                        "content: \"\\2610\";\n" +
                        "color: black;\n" +
                        "display: inline-block;\n" +
                        "margin-right: 6px;\n" +
                        "}\n" +
                        "\n" +
                        ".check-box::before {\n" +
                        "content: \"\\2611\"; \n" +
                        "color: dodgerblue;\n" +
                        "}\n" +
                        "\n" +
                        ".nested {\n" +
                        "display: none;\n" +
                        "}\n" +
                        "\n" +
                        ".active {\n" +
                        "display: block;\n" +
                        "}\n" +
                        ".highlight {\n" +
                        "background-color: rgba(255, 225, 0, 0.479);\n" +
                        "-moz-border-radius: 1px; / FF1+ /\n" +
                        "-webkit-border-radius: 1px; / Saf3-4 /\n" +
                        "border-radius: 5px; / Opera 10.5, IE 9, Saf5, Chrome /\n" +
                        "-moz-box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7); / FF3.5+ /\n" +
                        "-webkit-box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7); / Saf3.0+, Chrome /\n" +
                        "box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7); / Opera 10.5+, IE 9.0 /\n" +
                        "}\n" +
                        "\n" +
                        ".highlight {\n" +
                        "padding:1px 4px; \n" +
                        "margin:0 0px;\n" +
                        "}\n" +
                        "</style>\n" +
                        "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.2.0/jquery.min.js\"></script>\n" +
                        "<script type=\"text/javascript\" src=\"jquery.js\"></script>\n" +
                        "<script type=\"text/javascript\" src=\"highlight.js\"></script>\n" +
                        "<script type=\"text/javascript\" src=\"scrollTo-min.js\" ></script>\n" +
                        "<script>\n" +
                        "\n" +
                        "function Do () {\n" +
                        "var toggler = document.getElementsByClassName(\"box\");\n" +
                        "var i;\n" +
                        "for (i = 0; i < toggler.length; i++) {\n" +
                        "//toggler[i].addEventListener(\"click\", function() {\n" +
                        "toggler[i].parentElement.querySelector(\".nested\").classList.toggle(\"active\");\n" +
                        "toggler[i].classList.toggle(\"check-box\");\n" +
                        "//});\n" +
                        "}\n" +
                        "\n" +
                        "}</script>\n" +
                        "<script type=\"text/javascript\">\n" +
                        "jQuery(document).ready(function(){\n" +
                        "\n" +
                        "var search_number = 0;\n" +
                        "var search_count = 0;\n" +
                        "var count_text = 0;\n" +
                        "var srch_numb = 0;\n" +
                        "\n" +
                        "function scroll_to_word(){\n" +
                        "// $('#text .selectHighlight').click(() => {\n" +
                        "// $('html, body').animate({\n" +
                        "// scrollTop: $('.selectHighlight').offset().top\n" +
                        "// }, 200);\n" +
                        "// });\n" +
                        "var pos = $('#text .selectHighlight').animate();\n" +
                        "jQuery.scrollTo(\".selectHighlight\", 500, {offset:-150});\n" +
                        "}\n" +
                        "\n" +
                        "$('#search_text').bind('keyup oncnange', function() {\n" +
                        "$('#text').removeHighlight();\n" +
                        "txt = $('#search_text').val();\n" +
                        "if (txt == '') return;\n" +
                        "$('#text').highlight(txt);\n" +
                        "search_count = $('#text span.highlight').size() - 1;\n" +
                        "count_text = search_count + 1;\n" +
                        "search_number = 0;\n" +
                        "$('#text').selectHighlight(search_number);\n" +
                        "if ( search_count >= 0 ) scroll_to_word();\n" +
                        "$('#count').html('Total matches: <b>'+count_text+'</b>');\n" +
                        "});\n" +
                        "\n" +
                        "$('#clear_button').click(function() {\n" +
                        "$('#text').removeHighlight();\n" +
                        "$('#search_text').val('Search');\n" +
                        "$('#count').html('');\n" +
                        "jQuery.scrollTo(0, 500, {queue:true});\n" +
                        "});\n" +
                        "\n" +
                        "$('#prev_search').click(function() {\n" +
                        "if (search_number == 0) return;\n" +
                        "$('#text .selectHighlight').removeClass('selectHighlight');\n" +
                        "search_number--;\n" +
                        "srch_numb = search_number + 1;\n" +
                        "$('#text').selectHighlight(search_number);\n" +
                        "if ( search_count >= 0 ) { \n" +
                        "scroll_to_word();\n" +
                        "$('#count').html('Shown: <b>'+srch_numb+'</b> of '+$('#text span.highlight').size());\n" +
                        "}\n" +
                        "});\n" +
                        "\n" +
                        "$('#next_search').click(function() {\n" +
                        "if (search_number == search_count) return;\n" +
                        "$('#text .selectHighlight').removeClass('selectHighlight');\n" +
                        "search_number++;\n" +
                        "srch_numb = search_number + 1;\n" +
                        "$('#text').selectHighlight(search_number);\n" +
                        "if ( search_count >= 0 ) { \n" +
                        "scroll_to_word();\n" +
                        "$('#count').html('Shown: <b>'+srch_numb+'</b> of '+$('#text span.highlight').size());\n" +
                        "}\n" +
                        "});\n" +
                        "\n" +
                        "});\n" +
                        "\n" +
                        "</script>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div id=\"search_block\" style=\"position: inherit; display: block; top: 7px; background-color: #f0f0f0;\" >\n" +
                        "<input id=\"prev_search\" type=\"button\" value=\"<\" />\n" +
                        "<input id=\"search_text\" type=\"text\" value=\"Search\" onblur=\"if (this.value=='') this.value='Search';\" onfocus=\"if (this.value=='Search') this.value='';\" />\n" +
                        "<input id=\"next_search\" type=\"button\" value=\">\" />\n" +
                        "<input type=\"button\" id=\"butt\" onclick=\"Do();\" value=\"Open\"/>\n" +
                        "<input id=\"clear_button\" type=\"button\" value=\"X\" />\n" +
                        "\n" +
                        "\n" +
                        "<div id=\"count\" style=\"font-size:10pt;\"></div>\n" +
                        "</div>\n" +
                        "<div id='text'>\n"
        );
        htmlString.append("<h2>" + tree.getProjectName() + "</h2>");
        htmlString.append("<ul id=\"myUL\">");
        htmlString.append(" <li><span class=\"box\">" + tree.getTask().getName() + "</span>");
        htmlString.append(getHtmlTree(tree.getTask()));
        htmlString.append("</li>");
        htmlString.append("</ul>");
        htmlString.append("</div><script>\n" +
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
        copyFilesToProjectFolder(pathToProject);
    }

    private static void saveIndexHtml(String indexHtmlBody, String path) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(path + "/index.html")) {
            out.println(indexHtmlBody);
        }
    }

    private static String getHtmlTree(Task mainTask) {
        String htmlTree = "<ul class=\"nested\">\n";
        for (Task task : mainTask.getTasks()) {
            if (task.getTasks() != null && !task.getTasks().isEmpty()) {
                htmlTree = htmlTree + "<li>";
                htmlTree = htmlTree + addImage(task);
                htmlTree = htmlTree + "<span class=\"box\"";
                htmlTree = addColorId(htmlTree, task);
                htmlTree = htmlTree + ">" + task.getName() + "</span>";
            } else {
                htmlTree = htmlTree + "<li";
                if (task.getIcon() == 3) {
                    htmlTree = htmlTree + "><a href=\"" + task.getPath() + "\" target=\"_blank\">" + task.getName() + "</a></li>\n";
                } else {
                    htmlTree = addColorId(htmlTree, task);
                    htmlTree = htmlTree + ">" + addImage(task) + task.getName() + "</li>\n";
                }
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

    private static void copyFilesToProjectFolder(String path) {
        copyFileToProjectFolder("file/g.png", path + "/g.png");
        copyFileToProjectFolder("file/r.png", path + "/r.png");
        copyFileToProjectFolder("file/highlight.js", path + "/highlight.js");
        copyFileToProjectFolder("file/jquery.js", path + "/jquery.js");
        copyFileToProjectFolder("file/scrollTo-min.js", path + "/scrollTo-min.js");
    }

    private static void copyFileToProjectFolder(String pathFrom, String pathTo) {
        try (BufferedInputStream gOriginal = new BufferedInputStream(Objects.requireNonNull(PageService.class.getClassLoader().getResourceAsStream(pathFrom)));
             BufferedOutputStream gCopy = new BufferedOutputStream(new FileOutputStream(new File(pathTo)))
        ) {
            int b = 0;
            while (b != -1) {
                b = gOriginal.read();
                gCopy.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFolderName(String path) {
        String[] split = path.split("/");
        return split[split.length - 1];
    }
}