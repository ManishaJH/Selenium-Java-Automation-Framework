package framework.utils;

import framework.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WaitUtils {
    private WaitUtils() {
    }

    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return createWait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return createWait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForPresence(WebDriver driver, By locator) {
        return createWait(driver).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static boolean waitForInvisibility(WebDriver driver, By locator) {
        return createWait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    private static WebDriverWait createWait(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null when creating an explicit wait.");
        }
        int timeoutSeconds = ConfigManager.getInt("explicit.wait.seconds");
        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("Configuration 'explicit.wait.seconds' must be greater than zero.");
        }
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }
}
