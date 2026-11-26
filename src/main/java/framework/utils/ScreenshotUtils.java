package framework.utils;

import framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {
    private static final Logger LOGGER = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private ScreenshotUtils() {
    }

    public static Path capture(WebDriver driver, String testName) {
        if (!(driver instanceof TakesScreenshot screenshotDriver)) {
            throw new IllegalArgumentException("The supplied WebDriver does not support screenshots.");
        }
        if (testName == null || testName.isBlank()) {
            throw new IllegalArgumentException("Test name must not be null or blank.");
        }

        String directory = System.getProperty("screenshot.directory", "screenshots").trim();
        if (directory.isBlank()) {
            throw new IllegalArgumentException("Screenshot directory must not be blank.");
        }
        String safeTestName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String filename = "%s-%s-%d.png".formatted(
                safeTestName, LocalDateTime.now().format(TIMESTAMP_FORMAT), Thread.currentThread().getId());
        Path screenshotPath = Path.of(directory).toAbsolutePath().normalize().resolve(filename);

        try {
            Files.createDirectories(screenshotPath.getParent());
            Files.copy(screenshotDriver.getScreenshotAs(OutputType.FILE).toPath(), screenshotPath);
            LOGGER.info("Saved screenshot to {}", screenshotPath);
            return screenshotPath;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save screenshot to " + screenshotPath, exception);
        }
    }
}
