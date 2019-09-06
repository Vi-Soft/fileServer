package com.visoft.file.service.service.util;

import com.visoft.file.service.dto.AttachmentDocument;
import com.visoft.file.service.dto.FormType;
import com.visoft.file.service.dto.Report;
import com.visoft.file.service.dto.Task;
import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.service.AttachmentDocumentService;
import com.visoft.file.service.service.FormTypeService;
import com.visoft.file.service.util.TaskSorter;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.extern.log4j.Log4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.visoft.file.service.service.util.PropertiesService.getStaticServer;

@Log4j
public class PageService {

    private static String rootPath = PropertiesService.getRootPath();

    private static String loginHtml = PropertiesService.getLoginPage();

    private static String server = PropertiesService.getServerName();

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
                                "href=\"" + getStaticServer() + "/root.css" + "\""
                        ).replace(
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
        }
        String[] split = htmlString.split("<body>");
        htmlString = split[0] +
                "<body>\n" +
                "<div class=\"wrapper\">\n" +
                "  <div class=\"header\">\n" +
                "    <div class=\"exit\">\n" +
                "      <form action=\"" + server + "/api/logout\" method=\"post\">\n" +
                "        <input type=\"image\" src=\"" + getStaticServer() + "/imageLogout.png\" alt=\"Logout\" />\n" +
                "      </form>\n" +
                "      </div>\n" +
                "      <div class=\"logo\">Download Client</div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<a href=\"" + PropertiesService.getServerName() + folder + ".zip\" >\n" +
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
    ) throws FileNotFoundException {
        log.info("start saving html");
        String pathToProject = rootPath + "/" + tree.getCompanyName() + "/" + tree.getArchiveName();
        String htmlString = ("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "<style>\n" +
                "input[type=\"button\"]\n" +
                "{\n" +
                "    font-size:22px;\n" +
                "}\n" +
                "select\n" +
                "{\n" +
                "    font-size:22px;\n" +
                "}\n" +
                "p {\n" +
                "  line-height: 1.5;\n" +
                "}\n" +
                "body {\n" +
                "\t background-image: url(background.jpg);\n" +
                "\tbackground-attachment: fixed;\n" +
                "}\n" +
                ".searchResult{\n" +
                "\tmargin: 10px;\n" +
                "\tpadding: 0px 20px;\n" +
                "\t  background-color: rgba(175, 238, 238, 0.3);;\n" +
                "  border: 2px solid #008B8B;\n" +
                "  border-radius: 25px;\n" +
                "}\n" +
                "#search_text{\n" +
                "\n" +
                "\twidth: 297px;\n" +
                "\tpadding: 15px 0px 15px 20px;\n" +
                "\tfont-size: 16px;\n" +
                "\tfont-family: Montserrat, sans-serif;\n" +
                "\tborder: 10 none;\n" +
                "\theight: 52px;\n" +
                "\tmargin-right: 2;\n" +
                "\tborder-radius: 7px;\n" +
                "\tcolor: white;\n" +
                "\toutline: none;\n" +
                "\tbackground: grey;\n" +
                "\tfloat: left;\n" +
                "\tbox-sizing: border-box;\n" +
                "\ttransition: all 0.15s;\n" +
                "}\n" +
                "\n" +
                "#search_text:focus {\n" +
                "\tbackground: #008B8B;\n" +
                "}\n" +
                "\n" +
                ".keyword {\n" +
                "  max-width: 100%;\n" +
                "  color: #515151;\n" +
                "  font-size: 2rem;\n" +
                "  font-family: \"Playfair Display\", serif;\n" +
                "  font-weight: 700;\n" +
                "  letter-spacing: 1px;\n" +
                "  padding-bottom: 6px;\n" +
                "  text-align: center;\n" +
                "  border: 0;\n" +
                "  border-bottom: 5px solid #FFE399;\n" +
                "  outline: none;\n" +
                "}\n" +
                "@media screen and (max-width: 768px) {\n" +
                "  .keyword {\n" +
                "    font-size: 1.5rem;\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "::placeholder {\n" +
                "  color: #515151;\n" +
                "}\n" +
                "\n" +
                ".keyword:focus::placeholder {\n" +
                "  opacity: 0;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "/* RESULTS */\n" +
                ".info {\n" +
                "  width: 100%;\n" +
                "  text-align: left;\n" +
                "  padding: 15px;\n" +
                "}\n" +
                "\n" +
                "\n" +
                ".fa {\n" +
                "  color: #FFE399;\n" +
                "}\n" +
                "\n" +
                ".content {\n" +
                "  padding: 5px 25px;\n" +
                "}\n" +
                "\n" +
                "/* ARTICLE TITLE */\n" +
                ".title {\n" +
                "  color: #515151;\n" +
                "  font-family: \"Playfair Display\", serif;\n" +
                "  letter-spacing: 1px;\n" +
                "  text-align: left;\n" +
                "  font-size: 1.2rem;\n" +
                "  font-weight: 700;\n" +
                "}\n" +
                "\n" +
                "h2 {\n" +
                "  display: inline-block;\n" +
                "  padding: 0 5px 5px 0;\n" +
                "  border-bottom: 3px solid #FFE399;\n" +
                "}\n" +
                "\n" +
                "/* ARTICLE LINKS */\n" +
                "a, a:hover, a:visited, a:focus {\n" +
                "  color: #515151;\n" +
                "  text-decoration: none;\n" +
                "}\n" +
                "\n" +
                "a.more, a.more:hover, a.more:visited, a.more:focus {\n" +
                "  color: #606060;\n" +
                "}\n" +
                "\n" +
                "a.more:hover {\n" +
                "  color: #FFE399;\n" +
                "}\n" +
                "\n" +
                ".btn {\n" +
                "\tmargin-bottom:10px;\n" +
                "  display: inline-block;\n" +
                "  text-align: center;\n" +
                "  text-decoration: none;\n" +
                "  margin: 2px 0;\n" +
                "  border: solid 1px transparent;\n" +
                "  border-radius: 4px;\n" +
                "  padding: 0.5em 1em;\n" +
                "  color: #ffffff;\n" +
                "  background-color: #008B8B;\n" +
                "}\n" +
                ".btn:active {\n" +
                "  transform: translateY(1px);\n" +
                "  filter: saturate(150%);\n" +
                "}\n" +
                ".btn:hover,\n" +
                ".btn:focus {\n" +
                "  color: #008B8B;\n" +
                "  border-color: currentColor;\n" +
                "  background-color: white;\n" +
                "}\n" +
                ".wrapper {\n" +
                "    display: flex;\n" +
                "    font-size: 2.2em;\n" +
                "  }\n" +
                ".header {\n" +
                "text-align: center;\n" +
                "    border-radius: 25px;\n" +
                "    width: 90%;\n" +
                "    padding: 20px 15px;\n" +
                "    z-index: 99;\n" +
                "    display: flex;\n" +
                "    justify-content: space-between;\n" +
                "  }\n" +
                "  .exit {\n" +
                "    height: 40px;\n" +
                "    width: 40px;\n" +
                "    transform: scale(-1, 1);\n" +
                "    cursor: pointer;\n" +
                "  }\n" +
                "  .logo {\n" +
                "    margin: 0;\n" +
                "    color: #696969;\n" +
                "  }\n" +
                "  #footer{\n" +
                "    position: fixed;  Фиксированное положение \n" +
                "    left: 0; bottom: 0;  \n" +
                "    width: 90%; /* Ширина слоя */\n" +
                "  text-align: center;\n" +
                "  font-size: 20px;\n" +
                "      z-index: 99;                \n" +
                "    padding: 20px 15px;\n" +
                "\n" +
                "   \n" +
                "  }\n" +
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
                "<script>\n" +
                "\n" +
                "function searchClass(){\n" +
                "\n" +
                "\tvar e = document.getElementById(\"DocType\");\n" +
                "\tvar docTypeVal = e.options[e.selectedIndex].value;\n" +
                "\n" +
                "\tvar searchEl = document.getElementById(\"search_text\");\n" +
                "\tvar searchElVal = searchEl.value;\n" +
                "\t\n" +
                "\tvar searchResultDiv = document.getElementById(\"searchResult\");\n" +
                "\tsearchResultDiv.innerHTML = \"\";\n" +
                "\n" +
                "\n" +
                "\tvar elArr = document.getElementsByClassName(docTypeVal + searchElVal);\n" +
                "\tvar newElArr = Array.prototype.slice.call(elArr, 0);\n" +
                "\tArray.prototype.forEach.call(newElArr, function(el) {\n" +
                "\t\tvar attribute = el.getAttribute(\"href\");\n" +
                "\t\t//if (attribute.toLowerCase().indexOf(docTypeVal) === -1) return;\n" +
                "\t\tvar breakLine = document.createElement(\"BR\");\n" +
                "\t\tsearchResultDiv.appendChild(el.cloneNode(true));\n" +
                "\t\tsearchResultDiv.appendChild(breakLine);\n" +
                "\n" +
                "\t});\n" +
                "}\n" +
                "function showEmptyFolders(){\n" +
                "  var elArr = document.getElementsByClassName(\"empty-folder\");\n" +
                "  Array.prototype.forEach.call(elArr, function(el){\n" +
                "    el.classList.toggle(\"empty-folder\");\n" +
                "  });\n" +
                "}\n" +
                "\n" +
                "function hiddenEmptyFolders(){\n" +
                "  var elArr = document.getElementsByClassName('nested');\n" +
                "  var emptyEls = Array.prototype.filter.call(elArr, function(el){\n" +
                "    return el.getElementsByTagName(\"LI\").length === 0;\n" +
                "  });\n" +
                "    \n" +
                "  Array.prototype.forEach.call(emptyEls, function(el){\n" +
                "    var previousElementSibling = el.previousElementSibling;\n" +
                "    if(previousElementSibling.getElementsByTagName(\"A\").length === 0)\n" +
                "      previousElementSibling.classList.toggle(\"empty-folder\");\n" +
                "  });\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "</script>\n" +
                "<script type=\"text/javascript\">\n" +
                "jQuery(document).ready(function(){\n" +
                "\n" +
                "var search_number = 0;\n" +
                "var search_count = 0;\n" +
                "var count_text = 0;\n" +
                "var srch_numb = 0;\n" +
                "\n" +
                "function scroll_to_word(){\n" +
                "var pos = $('#text .selectHighlight').animate();\n" +
                "jQuery.scrollTo(\".selectHighlight\", 500, {offset:-150});\n" +
                "}\n" +
                "$('#clear_button').click(function() {\n" +
                "\tvar searchResultDiv = document.getElementById(\"searchResult\");\n" +
                "\tsearchResultDiv.innerHTML = \"\";\n" +
                "\n" +
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
                "<div class=\"search\">\n" +
                "\t\t<div class=\"searchbar\">\n" +
                "\t\t\t<select class=\"btn\" id=\"DocType\">\n" +
                "    <option selected value=\"Checklist-\">Checklist</option>\n" +
                "    <option value=\"NCR-\">NCR</option>\n" +
                "    <option value=\"RFI-\">RFI</option>\n" +
                "    <option value=\"POC-\">POC</option>\n" +
                "\t<option value=\"Approval_Of_Sub-Contractor-\">Approval_Of_Sub-Contractor</option>\n" +
                "\t<option value=\"Preliminary_Materials_Inspection-\">Preliminary_Materials_Inspection</option>\n" +
                "\t<option value=\"Approval_of_Supplier-\">Approval_of_Supplier</option>\n" +
                "\t<option value=\"Supervision_Reports-\">Supervision_Reports</option>\n" +
                "\t<option value=\"As_Made-\">As_Made</option>\n" +
                "\t<option value=\"Meetings_Summary-\">Meetings_Summary</option>\n" +
                "\t<option value=\"QC_Audits-\">QC_Audits</option>\n" +
                "\t<option value=\"Monthly_Reports-\">Monthly_Reports</option>\n" +
                "\t<option value=\"Construction_Documents-\">Construction_Documents</option>\n" +
                "\t<option value=\"Additional_Documents-\">Additional_Documents</option>\n" +
                "\t<option value=\"Drawings-\">Drawings</option>\n" +
                "   </select>\n" +
                "\t\t\t<input id=\"search_text\" type=\"text\" value=\"Search\" onblur=\"if (this.value=='') this.value='Search';\" onfocus=\"if (this.value=='Search') this.value='';\" />\n" +
                "\t\t\t<input class=\"btn\" id=\"searchClass\" type=\"button\" value=\"Search\" onClick=\"searchClass()\" />\n" +
                "\t\t\t<input class=\"btn\" id=\"clear_button\" type=\"button\" value=\"Clean\" />\n" +
                "<input class=\"btn\" type=\"button\" value=\"empty folder\" onClick=\"hiddenEmptyFolders()\" />\n" +
                "\t\t\t</div>\n" +
                "\t</div>\n" +
                "\n" +
                "</div>\n" +
                "<div>\n" +
                "<div id=\"searchResult\" class=\"searchResult\">\n" +
                "\t\n" +
                "</div>\n" +
                "<div id=\"count\" style=\"font-size:10pt;\"></div>\n" +
                "</div>" +
                "<div id='text'>\n") +
                "<h2>" +
                tree.getProjectName() +
                "</h2>" +
                "<ul id=\"myUL\">" +
                " <li><span class=\"box\">" +
                tree.getTask().getName() +
                "</span>" +
                getHtmlTree(tree.getTask(), formTypes, attachmentDocumentMap, forArchive, (tree.getCompanyName() + tree.getArchiveName()).length() + 2) +
                "</li>" +
                "</ul>" +
                "</div><script>\n" +
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
                "</html>";
        saveIndexHtml(htmlString, pathToProject);
        copyFilesToProjectFolder(pathToProject);
        log.info("finish saving html");
    }

    private static void saveIndexHtml(String indexHtmlBody, String path) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(path + "/index.html")) {
            out.println(indexHtmlBody);
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
                        }
                    }
                    AttachmentDocument attachmentDocument = new AttachmentDocumentService().getAttachmentDocument(attachmentDocumentMap, path);
                    if (attachmentDocument == null) {
                        htmlTree = htmlTree + "><a class=\"" + classWithId + "\" href=\""
                                + task.getPath()
                                + "\" target=\"_blank\" path=\"-\" type=\"-\" description=\"-\" certificate=\"-\" comment=\"-\" uploadDate=\"-\" fileName=\"-\">"
                                + task.getName()
                                + "</a></li>\n";
                    } else {
                        htmlTree = htmlTree + "><a class=\"" + classWithId + "\" href=\""
                                + task.getPath()
                                + "\" target=\"_blank\" path=\"" + attachmentDocument.getPath()
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

    private static String getLoginRedirectPage() {
        return convertHtmlToString("front/html/loginRedirectPage.html");
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