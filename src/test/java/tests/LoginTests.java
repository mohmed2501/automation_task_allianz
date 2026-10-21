package tests;

import base.BaseTest;
import io.qameta.allure.Feature;
import models.User;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginSignupPage;
import utils.ApiHelper;
import utils.TestDataFactory;

import static org.testng.Assert.assertEquals;

@Feature("Login")
public class LoginTests extends BaseTest {

    private User registeredUser;

    @BeforeClass(alwaysRun = true)
    public void createUser() {
        registeredUser = TestDataFactory.newUser();
        ApiHelper.createAccount(registeredUser);
    }

    @Test(description = "Login using invalid credentials")
    public void loginWithInvalidCredentials() {
        String wrongEmail = TestDataFactory.uniqueEmail("wrong");
        String wrongPassword = TestDataFactory.randomPassword();

        LoginSignupPage loginPage = homePage.clickSignupLogin();
        assertEquals(loginPage.getLoginHeader(), "Login to your account");

        loginPage.login(wrongEmail, wrongPassword);
        assertEquals(loginPage.getLoginErrorMessage(), "Your email or password is incorrect!");
    }

    @Test(description = "Login using valid credentials")
    public void loginWithValidCredentials() {
        LoginSignupPage loginPage = homePage.clickSignupLogin();
        assertEquals(loginPage.getLoginHeader(), "Login to your account");

        HomePage loggedInHome = loginPage.login(registeredUser.email(), registeredUser.password());
        assertEquals(loggedInHome.getLoggedInText(), "Logged in as " + registeredUser.name());
    }

    @AfterClass(alwaysRun = true)
    public void deleteUser() {
        ApiHelper.deleteAccount(registeredUser);
    }
}
