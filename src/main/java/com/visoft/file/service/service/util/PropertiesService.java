package com.visoft.file.service.service.util;

import com.visoft.file.service.handler.GeneralHandlerProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesService {

    private static String APPLICATION = "application.properties";

    private static String DB = "db";

    private static String LOGIN_HTML_URL = "login.html.url";

    private static String ROOT_PATH = "root.path";

    private static String SERVER = "server.domain";

    public static String getDBName() {
        return getProperties().getProperty(DB);
    }

    static String getLoginPage() {
        return getProperties().getProperty(LOGIN_HTML_URL);
    }

    public static String getRootPath() {
        return getProperties().getProperty(ROOT_PATH);
    }

    static String getServerName() {
        return getProperties().getProperty(SERVER);
    }

    private static Properties getProperties() {
        Properties prop = new Properties();
        InputStream input = GeneralHandlerProvider.class.getClassLoader().getResourceAsStream(APPLICATION);
        try {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
