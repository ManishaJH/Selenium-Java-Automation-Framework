package testdata;

import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class SauceDemoTestData {
    private static final String RESOURCE = "/testdata.properties";
    private static final Properties PROPERTIES = loadProperties();

    private SauceDemoTestData() {
    }

    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required test data '%s' in %s.".formatted(key, RESOURCE));
        }
        return value.trim();
    }

    @DataProvider(name = "invalidCredentials")
    public static Object[][] invalidCredentials() {
        InvalidCredential[] credentials =
                JsonTestDataReader.read("/invalid-login-data.json", InvalidCredential[].class);
        Object[][] data = new Object[credentials.length][2];
        for (int index = 0; index < credentials.length; index++) {
            data[index][0] = credentials[index].username();
            data[index][1] = credentials[index].password();
        }
        return data;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = SauceDemoTestData.class.getResourceAsStream(RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Test data resource not found: " + RESOURCE);
            }
            properties.load(input);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load test data resource: " + RESOURCE, exception);
        }
    }
}
