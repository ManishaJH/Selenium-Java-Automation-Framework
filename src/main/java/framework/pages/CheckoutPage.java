package framework.pages;

import framework.utils.ElementUtils;
import framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class CheckoutPage {
    private static final By PAGE_TITLE = By.cssSelector(".title");
    private static final By FIRST_NAME = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE_BUTTON = By.cssSelector("[data-test='continue']");
    private static final By FINISH_BUTTON = By.cssSelector("[data-test='finish']");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");
    private static final By CONFIRMATION_MESSAGE = By.cssSelector(".complete-header");
    private static final By SUMMARY_ITEM_NAMES = By.cssSelector(".cart_item .inventory_item_name");

    private final WebDriver driver;

    public CheckoutPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null.");
        }
        this.driver = driver;
    }

    public String getPageTitle() {
        return ElementUtils.getVisibleText(driver, PAGE_TITLE);
    }

    public void enterCustomerInformation(String firstName, String lastName, String postalCode) {
        ElementUtils.enterText(driver, FIRST_NAME, firstName);
        ElementUtils.enterText(driver, LAST_NAME, lastName);
        ElementUtils.enterText(driver, POSTAL_CODE, postalCode);
    }

    public void continueToOverview() {
        ElementUtils.click(driver, CONTINUE_BUTTON);
    }

    public void finishOrder() {
        ElementUtils.click(driver, FINISH_BUTTON);
    }

    public String getErrorMessage() {
        return ElementUtils.getVisibleText(driver, ERROR_MESSAGE);
    }

    public String getConfirmationMessage() {
        return ElementUtils.getVisibleText(driver, CONFIRMATION_MESSAGE);
    }

    public List<String> getOrderItemNames() {
        WaitUtils.waitForPresence(driver, SUMMARY_ITEM_NAMES);
        return driver.findElements(SUMMARY_ITEM_NAMES).stream()
                .map(item -> item.getText())
                .toList();
    }
}
