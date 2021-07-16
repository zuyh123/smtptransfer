package com.haier.autotest.smtp.utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 包含并返回一些特定于项目的配置变量。
 */
public enum Configuration {
    /**
     * 实例
     */
    INSTANCE;

    private static final String CONFIG_FILE = "/configuration.properties";
    private static final String USER_CONFIG_FILE = ".fakeSMTP.properties";
    private final Properties config = new Properties();

    /**
     * 打开“{@code configuration.properties}”文件并映射数据。
     */
    Configuration() {
        InputStream in = getClass().getResourceAsStream(CONFIG_FILE);
        try {
            // 加载默认设置
            config.load(in);
            in.close();
            // 并从用户设置中覆盖它们
            loadFromUserProfile();
        } catch (IOException e) {
            LoggerFactory.getLogger(Configuration.class).error("", e);
        }
    }

    /**
     * 从“{@code configuration.properties}”文件返回特定条目的值。
     *
     * @param key a string representing the key from a key/value couple.
     * @return the value of the key, or an empty string if the key was not found.
     */
    public String get(String key) {
        if (config.containsKey(key)) {
            return config.getProperty(key);
        }
        return "";
    }

    public Properties getConfig() {
        return config;
    }

    /**
     * 设置特定条目的值。
     *
     * @param key   a string representing the key from a key/value couple.
     * @param value the value of the key.
     */
    public void set(String key, String value) {
        config.setProperty(key, value);
    }

    /**
     * 从文件加载配置。
     *
     * @param file file to load configuration.
     * @return INSTANCE.
     * @throws IOException
     */
    public Configuration loadFromFile(File file) throws IOException {
        if (file.exists() && file.canRead()) {
            FileInputStream fis = new FileInputStream(file);
            try {
                config.load(fis);
            } finally {
                IOUtils.closeQuietly(fis);
            }
        }
        return INSTANCE;
    }

    /**
     * 从用户配置文件目录中的 .fakesmtp.properties 文件加载配置。
     * Calls {@link Configuration#loadFromFile(File)}.
     *
     * @return INSTANCE.
     * @throws IOException
     */
    public Configuration loadFromUserProfile() throws IOException {
        return loadFromFile(new File(System.getProperty("user.home"), USER_CONFIG_FILE));
    }
}
