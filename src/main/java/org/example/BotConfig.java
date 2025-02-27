package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = BotConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл config.properties не найден");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации", e);
        }
    }

    public static String getBotToken() {
        return properties.getProperty("bot.token");
    }

    public static String getBotUsername() {
        return properties.getProperty("bot.username");
    }
}
