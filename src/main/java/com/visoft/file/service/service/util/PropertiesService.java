package com.visoft.file.service.service.util;

import com.visoft.file.service.web.handler.GeneralHandlerProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesService {

    public static String getReportExtension() {
        return getProperties().getProperty("report.extension");
    }

    static String getLoginPage() {
        return getProperties().getProperty("login.html.url");
    }

    public static String getRootPath() {
        return getProperties().getProperty("root.path");
    }

    static String getServerName() {
        return getProperties().getProperty("server.domain");
    }

    public static String getDBName() {
        return getProperties().getProperty("db");
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