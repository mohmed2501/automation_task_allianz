package pages;

import io.qameta.allure.Step;
import models.PaymentCard;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PaymentPage extends BasePage {

    private final By nameOnCard = By.cssSelector("input[data-qa='name-on-card']");
    private final By cardNumber = By.cssSelector("input[data-qa='card-number']");
    private final By cvc = By.cssSelector("input[data-qa='cvc']");
    private final By expiryMonth = By.cssSelector("input[data-qa='expiry-month']");
    private final By expiryYear = By.cssSelector("input[data-qa='expiry-year']");
    private final By payButton = By.cssSelector("button[data-qa='pay-button']");
    private final By successMessage = By.cssSelector("#success_message .alert-success");

    public PaymentPage(WebDriver driver) {
        super(driver);
    }

    @Step("Enter payment details")
    public PaymentPage fillPaymentDetails(PaymentCard card) {
        type(nameOnCard, card.nameOnCard());
        type(cardNumber, card.cardNumber());
        type(cvc, card.cvc());
        type(expiryMonth, card.expiryMonth());
        type(expiryYear, card.expiryYear());
        return this;
    }

    /**
     * The site flashes "Your order has been placed successfully!" for a moment and then
     * redirects, so we try to catch it right after clicking and hand it to the next page.
     */
    @Step("Click 'Pay and Confirm Order'")
    public OrderPlacedPage clickPayAndConfirm() {
        click(payButton);
        String flashMessage = null;
        try {
            flashMessage = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .ignoring(StaleElementReferenceException.class)
                    .until(d -> {
                        WebElement msg = d.findElement(successMessage);
                        return msg.isDisplayed() ? msg.getText().trim() : null;
                    });
        } catch (TimeoutException e) {
            // page already redirected before we could read the message
        }
        return new OrderPlacedPage(driver, flashMessage);
    }
}
