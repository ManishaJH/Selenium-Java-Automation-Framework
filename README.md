# Selenium Java Automation Framework

A reusable Selenium WebDriver framework for testing [SauceDemo](https://www.saucedemo.com/) with Java 17-compatible bytecode, Maven, and TestNG. The project can be built with JDK 17 or a newer JDK, including JDK 23.

## Architecture

The framework separates browser lifecycle, configuration, page interactions, test scenarios, reporting, and logging:

- **Driver lifecycle:** `DriverFactory` creates Chrome, Firefox, or Edge WebDriver instances in a `ThreadLocal`. `BaseTest` initializes a driver before each test method and always closes it afterward.
- **Configuration:** `ConfigManager` reads `config.properties` and supports system property overrides. TestNG browser parameters take precedence for the browser; system properties take precedence over the properties file.
- **Page Object Model:** Page classes own their locators and user-facing actions. Test classes own assertions and scenario orchestration. Locators use Selenium `By` rather than PageFactory.
- **Utilities:** Explicit waits and common element operations are centralized. Screenshot capture is reused by the failure listener.
- **Reporting and logging:** A TestNG listener records results in a thread-associated Extent test and attaches a screenshot when a failed test still has an available driver. Log4j2 writes to the console and a rolling file.
- **Test data:** SauceDemo credentials and checkout values are kept in a test resource properties file, not repeated in test methods.

## Project structure

```text
SeleniumFramework/
├── pom.xml
├── README.md
├── reports/                         # Extent HTML report output
├── screenshots/                     # Failure screenshots
├── logs/                             # Rolling Log4j2 output
└── src/
    ├── main/
    │   ├── java/framework/
    │   │   ├── base/                 # Reserved for shared framework base types
    │   │   ├── config/               # ConfigManager
    │   │   ├── driver/               # DriverFactory
    │   │   ├── listeners/            # TestNG reporting listener
    │   │   ├── pages/                # LoginPage, ProductsPage, CartPage, CheckoutPage
    │   │   ├── reports/              # ExtentReportManager
    │   │   └── utils/                # Wait, element, JavaScript, screenshot utilities
    │   └── resources/
    │       ├── config.properties
    │       └── log4j2.xml
    └── test/
        ├── java/
        │   ├── testdata/             # SauceDemo test-data provider
        │   └── tests/                # BaseTest and SauceDemo tests
        └── resources/
            ├── testdata.properties
            ├── invalid-login-data.json
            └── testng.xml
```

## Technology stack

| Component | Technology |
|---|---|
| Language / bytecode | Java 17 (`maven.compiler.release=17`) |
| Build | Maven |
| Browser automation | Selenium WebDriver 4 |
| Test runner | TestNG |
| Driver binaries | WebDriverManager |
| Design approach | Page Object Model |
| Reporting | ExtentReports |
| Logging | Log4j2 |
| Test data libraries | Java Properties and Jackson Databind |

Invalid login data is deserialized from JSON with `JsonTestDataReader`; valid login credentials and checkout details are loaded from Properties.

## Prerequisites and setup

Install:

- JDK 17 or newer. JDK 23 works; there is no need to downgrade. Maven compiles with Java 17 compatibility using the compiler `release` setting.
- Maven 3.8 or newer.
- Chrome, Firefox, or Edge, depending on the browser selected for the run.
- Network access on the first run so Maven can resolve dependencies and WebDriverManager can obtain a compatible driver.

Check the environment:

```bash
java -version
mvn -version
```

Open the project root (`SeleniumFramework`) in IntelliJ IDEA and allow Maven to import the project. From a terminal in the project root, run:

```bash
mvn clean test
```

To compile and package without running browser tests:

```bash
mvn clean package -DskipTests
```

## Browser and execution options

Defaults are set in `src/main/resources/config.properties`:

```properties
browser=chrome
headless=false
base.url=https://www.saucedemo.com/
explicit.wait.seconds=10
```

Override browser or headless mode from Maven with Java system properties:

```bash
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge -Dheadless=true
mvn clean test -Dheadless=true
```

Supported `browser` values are `chrome`, `firefox`, and `edge`. Browser and headless can also be supplied as TestNG XML parameters; when supplied, they take precedence over system properties and `config.properties`.

The TestNG suite currently enables method-level parallel execution with three worker threads:

```xml
<suite parallel="methods" thread-count="3" data-provider-thread-count="3">
```

Change these counts in `src/test/resources/testng.xml` to suit the execution machine. Each test method receives its own thread-local driver. Be aware that parallel execution starts multiple browser processes and uses additional CPU and memory.

## Reports, screenshots, and logs

- Extent HTML report: `reports/ExtentReport.html`
- Failure screenshots: `screenshots/`
- Log4j2 rolling log: `logs/framework.log`
- Maven Surefire results: `target/surefire-reports/`

The listener captures a screenshot on a test failure when the current thread still has a live WebDriver. Screenshot names include the test name, timestamp, and thread ID.

## Test data

Valid credentials and checkout fields are in `src/test/resources/testdata.properties`, while invalid login cases are in `src/test/resources/invalid-login-data.json`.

The properties file contains:

```properties
username=standard_user
password=secret_sauce
invalid.username=invalid_user
invalid.password=invalid_password
checkout.firstName=Test
checkout.lastName=User
checkout.postalCode=10001
```

These are SauceDemo's public practice-application credentials, not credentials for a private account. Keep real secrets out of source control. `SauceDemoTestData` reads properties and exposes JSON-backed invalid credentials through a TestNG data provider. Use `JsonTestDataReader` to map other JSON resources into typed Java classes.

## Adding a page and test

1. Add a page class under `src/main/java/framework/pages`. Pass the current `WebDriver` in its constructor, keep page locators private, and expose focused page actions or state queries.
2. Use `WaitUtils` or `ElementUtils` for explicit-wait interactions. Avoid fixed sleeps and keep assertions out of page objects.
3. Add a TestNG test under `src/test/java/tests`, extending `BaseTest`. Obtain the driver with `getDriver()`, create page objects, and make assertions in the test.
4. Put reusable values in `src/test/resources/testdata.properties` and read them through `SauceDemoTestData`.
5. Add the test class to `src/test/resources/testng.xml` if it is not already included, then run `mvn clean test`.

Use TestNG groups such as `smoke` and `regression` to categorize tests. The sample suite demonstrates priorities, a method dependency, and a data provider.

## Current validation

The complete suite was run on JDK 23 with Java 17 bytecode compatibility using:

```bash
mvn clean test -Dheadless=true
```

The run completed with 9 tests, 0 failures, 0 errors, and 0 skipped.
