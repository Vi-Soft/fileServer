package com.visoft.files.service.util;

import com.visoft.files.handler.GraphqlHandlerProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesService {

    public static String getLoginPage() {
        return getProperties().getProperty("login");
    }

    public static String getRootPath() {
        return getProperties().getProperty("rootPath");
    }

    public static String getServerName() {
        return getProperties().getProperty("server");
    }

    private static Properties getProperties() {
        Properties prop = new Properties();
        InputStream input = GraphqlHandlerProvider.class.getClassLoader().getResourceAsStream("file.properties");
        try {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
