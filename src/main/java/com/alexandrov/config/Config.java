package com.alexandrov.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String baseUrl() { return get("redmine.url"); }
    public static String editorApiKey() { return get("redmine.editor.apikey"); }
    public static String executorApiKey() { return get("redmine.executor.apikey"); }
    public static String projectId() { return get("redmine.project.id"); }
}