package framework.pages;

import framework.utils.ElementUtils;
import framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage {
    private static final By PAGE_TITLE = By.cssSelector(".title");
    private static final By CART_ITEMS = By.cssSelector(".cart_item");
    private static final By ITEM_NAME = By.cssSelector(".inventory_item_name");
    private static final By ITEM_ACTION = By.cssSelector("button");
    private static final By CHECKOUT_BUTTON = By.cssSelector("[data-test='checkout']");

    private final WebDriver driver;

    public CartPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null.");
        }
        this.driver = driver;
    }

    public String getPageTitle() {
        return ElementUtils.getVisibleText(driver, PAGE_TITLE);
    }

    public List<String> getItemNames() {
        WaitUtils.waitForVisible(driver, PAGE_TITLE);
        List<WebElement> items = driver.findElements(CART_ITEMS);
        return items.stream()
                .map(item -> item.findElement(ITEM_NAME).getText())
                .toList();
    }

    public void removeItem(String productName) {
        findCartItem(productName).findElement(ITEM_ACTION).click();
    }

    public void proceedToCheckout() {
        ElementUtils.click(driver, CHECKOUT_BUTTON);
    }

    private WebElement findCartItem(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name must not be null or blank.");
        }
        return driver.findElements(CART_ITEMS).stream()
                .filter(item -> item.findElement(ITEM_NAME).getText().equals(productName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cart item not found: " + productName));
    }
}
