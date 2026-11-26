package framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {
    private static final String CONFIG_RESOURCE = "/config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigManager() {
    }

    public static String getString(String key) {
        validateKey(key);
        String override = System.getProperty(key);
        String value = override == null ? PROPERTIES.getProperty(key) : override;
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required configuration '%s' in %s or as a system property."
                            .formatted(key, CONFIG_RESOURCE));
        }
        return value.trim();
    }

    public static boolean getBoolean(String key) {
        String value = getString(key);
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException(
                "Configuration '%s' must be 'true' or 'false', but was '%s'.".formatted(key, value));
    }

    public static int getInt(String key) {
        String value = getString(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Configuration '%s' must be an integer, but was '%s'.".formatted(key, value), exception);
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ConfigManager.class.getResourceAsStream(CONFIG_RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Configuration resource not found: " + CONFIG_RESOURCE);
            }
            properties.load(input);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load " + CONFIG_RESOURCE, exception);
        }
    }

    private static void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Configuration key must not be null or blank.");
        }
    }
}
