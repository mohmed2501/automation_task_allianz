package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginSignupPage extends BasePage {

    // Signup side
    private final By newUserSignupHeader = By.cssSelector(".signup-form h2");
    private final By signupName = By.cssSelector("input[data-qa='signup-name']");
    private final By signupEmail = By.cssSelector("input[data-qa='signup-email']");
    private final By signupButton = By.cssSelector("button[data-qa='signup-button']");
    private final By signupError = By.cssSelector(".signup-form form p");

    // Login side
    private final By loginHeader = By.cssSelector(".login-form h2");
    private final By loginEmail = By.cssSelector("input[data-qa='login-email']");
    private final By loginPassword = By.cssSelector("input[data-qa='login-password']");
    private final By loginButton = By.cssSelector("button[data-qa='login-button']");
    private final By loginError = By.cssSelector(".login-form form p");

    public LoginSignupPage(WebDriver driver) {
        super(driver);
    }

    public String getNewUserSignupHeader() {
        return getText(newUserSignupHeader);
    }

    public String getLoginHeader() {
        return getText(loginHeader);
    }

    @Step("Sign up with name '{name}' and email '{email}'")
    public AccountInfoPage signUp(String name, String email) {
        type(signupName, name);
        type(signupEmail, email);
        click(signupButton);
        return new AccountInfoPage(driver);
    }

    @Step("Log in with email '{email}'")
    public HomePage login(String email, String password) {
        type(loginEmail, email);
        type(loginPassword, password);
        click(loginButton);
        return new HomePage(driver);
    }

    public String getSignupErrorMessage() {
        return getText(signupError);
    }

    public String getLoginErrorMessage() {
        return getText(loginError);
    }
}
