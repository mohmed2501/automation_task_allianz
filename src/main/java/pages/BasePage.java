package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.time.Duration;
import java.util.List;

/** Shared, wait-based helpers. Every page extends this, so tests never touch raw Selenium. */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout")));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> waitAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void click(By locator) {
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(locator)));
    }

    protected void clickElement(WebElement element) {
        scrollIntoView(element);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (ElementClickInterceptedException e) {
            jsClick(element); // something (usually an ad) is covering the element
        }
    }

    protected void type(By locator, String text) {
        WebElement element = waitVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitVisible(locator).getText().trim();
    }

    protected boolean isVisible(By locator) {
        try {
            return waitVisible(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void selectByValue(By locator, String value) {
        new Select(waitVisible(locator)).selectByValue(value);
    }

    protected void selectByText(By locator, String text) {
        new Select(waitVisible(locator)).selectByVisibleText(text);
    }

    protected void check(By checkbox) {
        WebElement element = waitVisible(checkbox);
        if (!element.isSelected()) {
            clickElement(element);
        }
    }

    /** Hovers over one element, then clicks another one (e.g. hover a product card, click its button). */
    protected void hoverAndClick(WebElement hoverTarget, WebElement clickTarget) {
        scrollIntoView(hoverTarget);
        try {
            new Actions(driver)
                    .moveToElement(hoverTarget)
                    .pause(Duration.ofMillis(500))
                    .moveToElement(clickTarget)
                    .click()
                    .perform();
        } catch (WebDriverException e) {
            jsClick(clickTarget);
        }
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
