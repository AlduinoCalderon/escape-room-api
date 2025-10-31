package com.escaperoom.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentConfig {
    private static Dotenv dotenv;
    
    public static void load() {
        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
        } catch (Exception e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
        }
    }
    
    public static String getProperty(String key, String defaultValue) {
        if (dotenv == null) {
            return defaultValue;
        }
        String value = dotenv.get(key);
        return value != null ? value : defaultValue;
    }
    
    public static String getProperty(String key) {
        if (dotenv == null) {
            throw new RuntimeException("Environment not loaded. Key: " + key);
        }
        String value = dotenv.get(key);
        if (value == null) {
            throw new RuntimeException("Required environment variable not found: " + key);
        }
        return value;
    }
}

