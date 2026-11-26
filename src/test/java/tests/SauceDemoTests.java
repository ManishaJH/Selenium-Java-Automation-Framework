package tests;

import framework.pages.CartPage;
import framework.pages.CheckoutPage;
import framework.pages.LoginPage;
import framework.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import testdata.SauceDemoTestData;

import java.util.List;

public class SauceDemoTests extends BaseTest {
    private static final String BACKPACK = "Sauce Labs Backpack";

    @Test(groups = {"smoke"}, priority = 1)
    public void successfulLogin() {
        ProductsPage productsPage = loginWithValidCredentials();

        Assert.assertEquals(productsPage.getPageTitle(), "Products");
    }

    @Test(groups = {"regression"}, dataProvider = "invalidCredentials",
            dataProviderClass = SauceDemoTestData.class, priority = 2)
    public void invalidLoginShowsError(String username, String password) {
        LoginPage loginPage = new LoginPage(getDriver()).open();
        loginPage.login(username, password);

        Assert.assertTrue(loginPage.getErrorMessage().contains("do not match"),
                "Expected an invalid-credentials message.");
    }

    @Test(groups = {"regression"}, priority = 3)
    public void emptyUsernameAndPasswordShowValidation() {
        LoginPage loginPage = new LoginPage(getDriver()).open();
        loginPage.submit();

        Assert.assertTrue(loginPage.getErrorMessage().contains("Username is required"),
                "Expected required-username validation when both fields are empty.");

        loginPage.enterUsername(SauceDemoTestData.get("username"));
        loginPage.submit();
        Assert.assertTrue(loginPage.getErrorMessage().contains("Password is required"),
                "Expected required-password validation when the password is empty.");
    }

    @Test(groups = {"smoke"}, dependsOnMethods = "successfulLogin", priority = 4)
    public void productsPageDisplaysInventory() {
        ProductsPage productsPage = loginWithValidCredentials();

        Assert.assertEquals(productsPage.getPageTitle(), "Products");
        Assert.assertFalse(productsPage.getProductNames().isEmpty(),
                "Expected the products page to display inventory.");
    }

    @Test(groups = {"smoke"}, priority = 5)
    public void addProductToCart() {
        ProductsPage productsPage = loginWithValidCredentials();
        productsPage.addProductToCart(BACKPACK);

        Assert.assertEquals(productsPage.getCartItemCount(), 1);
    }

    @Test(groups = {"regression"}, priority = 6)
    public void removeProductFromCart() {
        ProductsPage productsPage = loginWithValidCredentials();
        productsPage.addProductToCart(BACKPACK);
        productsPage.openCart();
        new CartPage(getDriver()).removeItem(BACKPACK);

        Assert.assertTrue(new CartPage(getDriver()).getItemNames().isEmpty(),
                "Expected the cart to be empty after removing its product.");
    }

    @Test(groups = {"smoke"}, priority = 7)
    public void cartContainsAddedProduct() {
        ProductsPage productsPage = loginWithValidCredentials();
        productsPage.addProductToCart(BACKPACK);
        productsPage.openCart();

        Assert.assertEquals(new CartPage(getDriver()).getItemNames(), List.of(BACKPACK));
    }

    @Test(groups = {"smoke"}, priority = 8)
    public void completeCheckoutSuccessfully() {
        ProductsPage productsPage = loginWithValidCredentials();
        productsPage.addProductToCart(BACKPACK);
        productsPage.openCart();

        CartPage cartPage = new CartPage(getDriver());
        cartPage.proceedToCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.enterCustomerInformation(
                SauceDemoTestData.get("checkout.firstName"),
                SauceDemoTestData.get("checkout.lastName"),
                SauceDemoTestData.get("checkout.postalCode"));
        checkoutPage.continueToOverview();
        Assert.assertEquals(checkoutPage.getOrderItemNames(), List.of(BACKPACK));
        checkoutPage.finishOrder();

        Assert.assertEquals(checkoutPage.getConfirmationMessage(), "Thank you for your order!");
    }

    @Test(groups = {"regression"}, priority = 9)
    public void logoutReturnsToLoginPage() {
        ProductsPage productsPage = loginWithValidCredentials();
        productsPage.logout();

        Assert.assertTrue(new LoginPage(getDriver()).isLoginFormDisplayed(),
                "Expected logout to return to the login form.");
    }

    private ProductsPage loginWithValidCredentials() {
        LoginPage loginPage = new LoginPage(getDriver()).open();
        loginPage.login(SauceDemoTestData.get("username"), SauceDemoTestData.get("password"));
        return new ProductsPage(getDriver());
    }
}
