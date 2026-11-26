package framework.pages;

import framework.config.ConfigManager;
import framework.utils.ElementUtils;
import framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private static final By USERNAME = By.cssSelector("[data-test='username']");
    private static final By PASSWORD = By.cssSelector("[data-test='password']");
    private static final By LOGIN_BUTTON = By.cssSelector("[data-test='login-button']");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    private final WebDriver driver;

    public LoginPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null.");
        }
        this.driver = driver;
    }

    public LoginPage open() {
        driver.get(ConfigManager.getString("base.url"));
        WaitUtils.waitForVisible(driver, USERNAME);
        return this;
    }

    public LoginPage enterUsername(String username) {
        ElementUtils.enterText(driver, USERNAME, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        ElementUtils.enterText(driver, PASSWORD, password);
        return this;
    }

    public void submit() {
        ElementUtils.click(driver, LOGIN_BUTTON);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        submit();
    }

    public String getErrorMessage() {
        return ElementUtils.getVisibleText(driver, ERROR_MESSAGE);
    }

    public boolean isLoginFormDisplayed() {
        return WaitUtils.waitForVisible(driver, USERNAME).isDisplayed();
    }
}
