package tests;

import framework.driver.DriverFactory;
import framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public abstract class BaseTest {
    private static final Logger LOGGER = LogManager.getLogger(BaseTest.class);

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("") String browserParameter,
                      @Optional("") String headlessParameter) {
        String browser = resolveSetting(browserParameter, "browser");
        boolean headless = headlessParameter != null && !headlessParameter.isBlank()
                ? parseBooleanParameter(headlessParameter)
                : ConfigManager.getBoolean("headless");
        DriverFactory.createDriver(browser, headless);
        LOGGER.info("Starting test '{}' with browser '{}'", getClass().getSimpleName(), browser);
    }

    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    private static String resolveSetting(String testParameter, String propertyName) {
        if (testParameter != null && !testParameter.isBlank()) {
            return testParameter.trim();
        }
        return ConfigManager.getString(propertyName);
    }

    private static boolean parseBooleanParameter(String value) {
        if ("true".equalsIgnoreCase(value.trim())) {
            return true;
        }
        if ("false".equalsIgnoreCase(value.trim())) {
            return false;
        }
        throw new IllegalArgumentException("TestNG parameter 'headless' must be 'true' or 'false'.");
    }
}
