package framework.pages;

import framework.utils.ElementUtils;
import framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ProductsPage {
    private static final By PAGE_TITLE = By.cssSelector(".title");
    private static final By PRODUCT_CARDS = By.cssSelector(".inventory_item");
    private static final By PRODUCT_NAME = By.cssSelector(".inventory_item_name");
    private static final By PRODUCT_ACTION = By.cssSelector("button");
    private static final By CART_LINK = By.cssSelector(".shopping_cart_link");
    private static final By CART_BADGE = By.cssSelector(".shopping_cart_badge");
    private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
    private static final By LOGOUT_LINK = By.id("logout_sidebar_link");

    private final WebDriver driver;

    public ProductsPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null.");
        }
        this.driver = driver;
    }

    public String getPageTitle() {
        return ElementUtils.getVisibleText(driver, PAGE_TITLE);
    }

    public List<String> getProductNames() {
        WaitUtils.waitForPresence(driver, PRODUCT_CARDS);
        return driver.findElements(PRODUCT_CARDS).stream()
                .map(card -> card.findElement(PRODUCT_NAME).getText())
                .toList();
    }

    public void addProductToCart(String productName) {
        findProductCard(productName).findElement(PRODUCT_ACTION).click();
    }

    public void removeProductFromCart(String productName) {
        findProductCard(productName).findElement(PRODUCT_ACTION).click();
    }

    public int getCartItemCount() {
        List<WebElement> badges = driver.findElements(CART_BADGE);
        return badges.isEmpty() ? 0 : Integer.parseInt(badges.get(0).getText());
    }

    public void openCart() {
        ElementUtils.click(driver, CART_LINK);
    }

    public void logout() {
        ElementUtils.click(driver, MENU_BUTTON);
        ElementUtils.click(driver, LOGOUT_LINK);
    }

    private WebElement findProductCard(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name must not be null or blank.");
        }
        WaitUtils.waitForPresence(driver, PRODUCT_CARDS);
        return driver.findElements(PRODUCT_CARDS).stream()
                .filter(card -> card.findElement(PRODUCT_NAME).getText().equals(productName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + productName));
    }
}
