package framework.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class ElementUtils {
    private ElementUtils() {
    }

    public static void click(WebDriver driver, By locator) {
        WaitUtils.waitForClickable(driver, locator).click();
    }

    public static void enterText(WebDriver driver, By locator, String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text to enter must not be null.");
        }
        WebElement element = WaitUtils.waitForVisible(driver, locator);
        element.clear();
        element.sendKeys(text);
    }

    public static String getVisibleText(WebDriver driver, By locator) {
        return WaitUtils.waitForVisible(driver, locator).getText();
    }
}
