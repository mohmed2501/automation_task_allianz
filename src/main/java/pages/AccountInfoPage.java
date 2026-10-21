package pages;

import io.qameta.allure.Step;
import models.User;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** The long signup form: "Enter Account Information" + "Address Information". */
public class AccountInfoPage extends BasePage {

    private final By header = By.cssSelector(".login-form h2.title b");

    // Account information
    private final By titleMr = By.id("id_gender1");
    private final By titleMrs = By.id("id_gender2");
    private final By name = By.id("name");
    private final By email = By.id("email");
    private final By password = By.id("password");
    private final By days = By.id("days");
    private final By months = By.id("months");
    private final By years = By.id("years");
    private final By newsletter = By.id("newsletter");
    private final By specialOffers = By.id("optin");

    // Address information
    private final By firstName = By.id("first_name");
    private final By lastName = By.id("last_name");
    private final By company = By.id("company");
    private final By address1 = By.id("address1");
    private final By address2 = By.id("address2");
    private final By country = By.id("country");
    private final By state = By.id("state");
    private final By city = By.id("city");
    private final By zipcode = By.id("zipcode");
    private final By mobileNumber = By.id("mobile_number");
    private final By createAccountButton = By.cssSelector("button[data-qa='create-account']");

    public AccountInfoPage(WebDriver driver) {
        super(driver);
    }

    public String getHeader() {
        return getText(header);
    }

    /** Email is pre-filled from the signup step and read-only, so we read it back to verify. */
    public String getEmail() {
        return waitVisible(email).getDomProperty("value");
    }

    @Step("Fill account details: title, name, password, date of birth")
    public AccountInfoPage fillAccountDetails(User user) {
        click(user.title().equals("Mr") ? titleMr : titleMrs);
        type(name, user.name());
        type(password, user.password());
        selectByValue(days, String.valueOf(user.birthDay()));
        selectByValue(months, String.valueOf(user.birthMonth()));
        selectByValue(years, String.valueOf(user.birthYear()));
        return this;
    }

    @Step("Select 'Sign up for our newsletter!'")
    public AccountInfoPage checkNewsletter() {
        check(newsletter);
        return this;
    }

    @Step("Select 'Receive special offers from our partners!'")
    public AccountInfoPage checkSpecialOffers() {
        check(specialOffers);
        return this;
    }

    @Step("Fill address details")
    public AccountInfoPage fillAddressDetails(User user) {
        type(firstName, user.firstName());
        type(lastName, user.lastName());
        type(company, user.company());
        type(address1, user.address1());
        type(address2, user.address2());
        selectByText(country, user.country());
        type(state, user.state());
        type(city, user.city());
        type(zipcode, user.zipcode());
        type(mobileNumber, user.mobileNumber());
        return this;
    }

    @Step("Click 'Create Account'")
    public AccountCreatedPage clickCreateAccount() {
        click(createAccountButton);
        return new AccountCreatedPage(driver);
    }
}
