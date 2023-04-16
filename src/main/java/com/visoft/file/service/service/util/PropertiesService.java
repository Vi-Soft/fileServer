package com.visoft.file.service.service.util;

import com.visoft.file.service.web.handler.GeneralHandlerProvider;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesService {

    static String getEmailSuccessMessage() {
        return getProperty("email.success.message");
    }

    static String getEmailErrorMessage() {
        return getProperty("email.error.message");
    }

    static String getEmailPassword() {
        return getProperty("email.password");
    }

    static String getEmailSubject() {
        return getProperty("email.subject");
    }

    static String getEmailLogin() {
        return getProperty("email.login");
    }

    static String getEmailTo() {
        return getProperty("email.to");
    }

    public static String getToken() {
        return getProperty("token");
    }

    public static String getReportExtension() {
        return getProperty("report.extension");
    }

    public static String getStaticServer() {
        return getProperty("static.server");
    }

    static String getLoginPage() {
        return getProperty("login.html.url");
    }

    public static String getRootPath() {
        return getProperty("root.path");
    }

    public static int getFolderLifeDuration() {
        final String folderLifeDuration = getProperty("scheduler.folder.duration");
        if (StringUtils.isEmpty(folderLifeDuration)) {
            return 0;
        }
        return Integer.parseInt(folderLifeDuration);
    }

    static String getServerName() {
        return getProperty("server.domain");
    }

    public static String getDBName() {
        return getProperty("db");
    }

    private static String getProperty(String value) {
        return getProperties().getProperty(value);
    }

    private static Properties getProperties() {
        Properties prop = new Properties();
        InputStream input = GeneralHandlerProvider
                .class
                .getClassLoader()
                .getResourceAsStream("application.properties");
        try {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
