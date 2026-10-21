package pages;

import driver.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.FileHelper;

import java.nio.file.Path;
import java.time.Duration;

public class OrderPlacedPage extends BasePage {

    private final By header = By.cssSelector("h2[data-qa='order-placed']");
    private final By confirmationText = By.xpath("//p[contains(., 'Congratulations')]");
    private final By downloadInvoiceButton = By.cssSelector("a[href^='/download_invoice']");

    private final String flashSuccessMessage;

    public OrderPlacedPage(WebDriver driver, String flashSuccessMessage) {
        super(driver);
        this.flashSuccessMessage = flashSuccessMessage;
    }

    /** The short-lived message caught on the payment page, or null if it disappeared too fast. */
    public String getFlashSuccessMessage() {
        return flashSuccessMessage;
    }

    public String getHeader() {
        return getText(header);
    }

    public String getConfirmationText() {
        return getText(confirmationText);
    }

    @Step("Click 'Download Invoice' and wait for the file")
    public Path downloadInvoice() {
        FileHelper.cleanDirectory(DriverManager.DOWNLOAD_DIR);
        click(downloadInvoiceButton);
        return FileHelper.waitForDownload(DriverManager.DOWNLOAD_DIR, "invoice", Duration.ofSeconds(20));
    }
}
