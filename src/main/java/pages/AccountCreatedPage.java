package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AccountCreatedPage extends BasePage {

    private final By header = By.cssSelector("h2[data-qa='account-created']");
    private final By continueButton = By.cssSelector("a[data-qa='continue-button']");

    public AccountCreatedPage(WebDriver driver) {
        super(driver);
    }

    public String getHeader() {
        return getText(header);
    }

    @Step("Click 'Continue'")
    public HomePage clickContinue() {
        click(continueButton);
        return new HomePage(driver);
    }
}
