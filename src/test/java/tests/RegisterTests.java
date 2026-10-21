package tests;

import base.BaseTest;
import io.qameta.allure.Feature;
import models.User;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import pages.AccountCreatedPage;
import pages.AccountInfoPage;
import pages.HomePage;
import pages.LoginSignupPage;
import utils.ApiHelper;
import utils.TestDataFactory;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Registration")
public class RegisterTests extends BaseTest {

    private final List<User> usersToDelete = new ArrayList<>();

    @Test(description = "Register a new account")
    public void registerNewAccount() {
        User user = TestDataFactory.newUser();
        usersToDelete.add(user);

        assertTrue(homePage.isHomePageVisible(), "Home page is not visible");

        LoginSignupPage loginSignupPage = homePage.clickSignupLogin();
        assertEquals(loginSignupPage.getNewUserSignupHeader(), "New User Signup!");

        AccountInfoPage accountInfoPage = loginSignupPage.signUp(user.name(), user.email());
        assertEquals(accountInfoPage.getHeader().toUpperCase(), "ENTER ACCOUNT INFORMATION");

        accountInfoPage.fillAccountDetails(user);
        assertEquals(accountInfoPage.getEmail(), user.email(), "Pre-filled email is wrong");

        AccountCreatedPage accountCreatedPage = accountInfoPage
                .checkNewsletter()
                .checkSpecialOffers()
                .fillAddressDetails(user)
                .clickCreateAccount();
        assertEquals(accountCreatedPage.getHeader().toUpperCase(), "ACCOUNT CREATED!");

        HomePage loggedInHome = accountCreatedPage.clickContinue();
        assertEquals(loggedInHome.getLoggedInText(), "Logged in as " + user.name());
    }

    @Test(description = "Register using an already registered email")
    public void registerWithExistingEmail() {
        User existingUser = TestDataFactory.newUser();
        ApiHelper.createAccount(existingUser); // precondition: the email is already registered
        usersToDelete.add(existingUser);

        assertTrue(homePage.isHomePageVisible(), "Home page is not visible");

        LoginSignupPage loginSignupPage = homePage.clickSignupLogin();
        assertEquals(loginSignupPage.getNewUserSignupHeader(), "New User Signup!");

        loginSignupPage.signUp(existingUser.name(), existingUser.email());
        assertEquals(loginSignupPage.getSignupErrorMessage(), "Email Address already exist!");
    }

    @AfterClass(alwaysRun = true)
    public void deleteTestAccounts() {
        usersToDelete.forEach(ApiHelper::deleteAccount);
    }
}
