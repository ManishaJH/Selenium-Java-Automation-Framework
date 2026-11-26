package framework.listeners;

import com.aventstack.extentreports.ExtentTest;
import framework.driver.DriverFactory;
import framework.reports.ExtentReportManager;
import framework.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Path;

public class ExtentReportListener implements ITestListener, IExecutionListener {
    private static final Logger LOGGER = LogManager.getLogger(ExtentReportListener.class);

    @Override
    public void onExecutionStart() {
        LOGGER.info("Test execution started");
    }

    @Override
    public void onExecutionFinish() {
        ExtentReportManager.flush();
        LOGGER.info("Test execution finished; report flushed");
    }

    @Override
    public void onTestStart(ITestResult result) {
        String description = result.getMethod().getDescription();
        ExtentTest test = ExtentReportManager.createTest(
                result.getMethod().getMethodName(),
                description == null ? "" : description);
        test.assignCategory(result.getMethod().getGroups());
        LOGGER.info("Started test {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.getTest()
                .pass("Passed in " + durationMillis(result) + " ms");
        LOGGER.info("Test passed: {}", result.getMethod().getMethodName());
        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        Throwable failure = result.getThrowable();
        if (failure != null) {
            test.fail(failure);
            LOGGER.error("Test failed: {}", result.getMethod().getMethodName(), failure);
        } else {
            test.fail("Test failed without an exception detail.");
            LOGGER.error("Test failed without an exception detail: {}", result.getMethod().getMethodName());
        }

        attachFailureScreenshot(test, result);
        test.info("Execution time: " + durationMillis(result) + " ms");
        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String description = result.getMethod().getDescription();
        ExtentTest test = ExtentReportManager.getOrCreateTest(
                result.getMethod().getMethodName(), description == null ? "" : description);
        Throwable cause = result.getThrowable();
        if (cause == null) {
            test.skip("Test skipped.");
        } else {
            test.skip(cause);
        }
        LOGGER.warn("Test skipped: {}", result.getMethod().getMethodName());
        ExtentReportManager.removeTest();
    }

    private static void attachFailureScreenshot(ExtentTest test, ITestResult result) {
        try {
            Path screenshot = ScreenshotUtils.capture(
                    DriverFactory.getDriver(), result.getMethod().getMethodName());
            test.addScreenCaptureFromPath(screenshot.toString());
        } catch (RuntimeException exception) {
            test.warning("Failure screenshot could not be captured: " + exception.getMessage());
            LOGGER.warn("Could not capture screenshot for {}", result.getMethod().getMethodName(), exception);
        }
    }

    private static long durationMillis(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
