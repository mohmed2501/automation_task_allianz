package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;

/** Home page + the header menu that is shared by every page. */
public class HomePage extends BasePage {

    private final By logo = By.cssSelector(".logo img");
    private final By slider = By.id("slider");
    private final By signupLoginLink = By.cssSelector("a[href='/login']");
    private final By productsLink = By.cssSelector("a[href='/products']");
    private final By loggedInAs = By.xpath("//a[contains(normalize-space(), 'Logged in as')]");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Step("Navigate to the home page")
    public HomePage open() {
        driver.get(ConfigReader.get("baseUrl"));
        return this;
    }

    public boolean isHomePageVisible() {
        return driver.getTitle().equals("Automation Exercise") && isVisible(logo) && isVisible(slider);
    }

    @Step("Click 'Signup / Login'")
    public LoginSignupPage clickSignupLogin() {
        click(signupLoginLink);
        return new LoginSignupPage(driver);
    }

    @Step("Click 'Products'")
    public ProductsPage clickProducts() {
        click(productsLink);
        return new ProductsPage(driver);
    }

    /** Returns e.g. "Logged in as John Smith" */
    public String getLoggedInText() {
        return getText(loggedInAs);
    }
}
