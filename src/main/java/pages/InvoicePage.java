package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import utils.AllureHelper;

import java.nio.file.Path;

/** Opens the downloaded invoice file in a new browser tab so we can screenshot it. */
public class InvoicePage extends BasePage {

    private String originalTab;

    public InvoicePage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the downloaded invoice in a new tab")
    public InvoicePage open(Path invoiceFile) {
        originalTab = driver.getWindowHandle();
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(invoiceFile.toUri().toString());
        return this;
    }

    public String getContent() {
        return getText(By.tagName("body"));
    }

    @Step("Take a screenshot of the invoice and attach it to the report")
    public InvoicePage attachScreenshot() {
        AllureHelper.attachScreenshot(driver, "Invoice");
        return this;
    }

    public void close() {
        driver.close();
        driver.switchTo().window(originalTab);
    }
}
