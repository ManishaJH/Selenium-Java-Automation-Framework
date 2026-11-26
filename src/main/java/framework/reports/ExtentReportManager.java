package framework.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ExtentReportManager {
    private static final String REPORT_PATH = "reports/ExtentReport.html";
    private static final ExtentReports EXTENT = createExtentReports();
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    private ExtentReportManager() {
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = EXTENT.createTest(testName, description);
        CURRENT_TEST.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        ExtentTest test = CURRENT_TEST.get();
        if (test == null) {
            throw new IllegalStateException("No Extent test is associated with the current thread.");
        }
        return test;
    }

    public static ExtentTest getOrCreateTest(String testName, String description) {
        ExtentTest test = CURRENT_TEST.get();
        return test == null ? createTest(testName, description) : test;
    }

    public static void removeTest() {
        CURRENT_TEST.remove();
    }

    public static synchronized void flush() {
        EXTENT.flush();
    }

    private static ExtentReports createExtentReports() {
        try {
            Path report = Path.of(REPORT_PATH).toAbsolutePath().normalize();
            Files.createDirectories(report.getParent());
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(report.toString());
            sparkReporter.config().setDocumentTitle("Selenium Automation Results");
            sparkReporter.config().setReportName("SauceDemo Test Execution");

            ExtentReports reports = new ExtentReports();
            reports.attachReporter(sparkReporter);
            reports.setSystemInfo("Framework", "Selenium Java Automation Framework");
            reports.setSystemInfo("Java", System.getProperty("java.version"));
            return reports;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to initialize Extent report at " + REPORT_PATH, exception);
        }
    }
}
