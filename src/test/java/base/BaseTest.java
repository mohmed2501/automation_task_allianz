package base;

import driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.HomePage;
import utils.AllureHelper;

/** Steps 1 and 2 of every scenario: launch the browser and open the site. */
public abstract class BaseTest {

    protected WebDriver driver;
    protected HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverManager.initDriver();
        driver = DriverManager.getDriver();
        homePage = new HomePage(driver).open();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        switch (result.getStatus()) {
            case ITestResult.SUCCESS -> AllureHelper.captureResultScreenshot(driver, testName, "passed");
            case ITestResult.FAILURE -> AllureHelper.captureResultScreenshot(driver, testName, "failed");
            default -> { } // skipped tests have nothing useful on screen
        }
        DriverManager.quitDriver();
    }
}
