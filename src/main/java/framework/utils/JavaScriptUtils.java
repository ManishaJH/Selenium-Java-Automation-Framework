package framework.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class JavaScriptUtils {
    private JavaScriptUtils() {
    }

    public static Object execute(WebDriver driver, String script, Object... arguments) {
        if (!(driver instanceof JavascriptExecutor executor)) {
            throw new IllegalArgumentException("The supplied WebDriver does not support JavaScript execution.");
        }
        return executor.executeScript(script, arguments);
    }

    public static void scrollIntoView(WebDriver driver, WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Element to scroll into view must not be null.");
        }
        execute(driver, "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
    }
}
