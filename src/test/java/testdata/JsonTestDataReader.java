package testdata;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public final class JsonTestDataReader {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonTestDataReader() {
    }

    public static <T> T read(String resourcePath, Class<T> targetType) {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new IllegalArgumentException("JSON resource path must not be null or blank.");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("JSON target type must not be null.");
        }

        try (InputStream input = JsonTestDataReader.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("JSON test data resource not found: " + resourcePath);
            }
            return OBJECT_MAPPER.readValue(input, targetType);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read JSON test data resource: " + resourcePath, exception);
        }
    }
}
