package pages;

import io.qameta.allure.Step;
import models.CartItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.components.CartTable;

import java.util.List;

public class CartPage extends BasePage {

    private final By proceedToCheckoutButton = By.cssSelector("a.check_out");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public List<CartItem> getCartItems() {
        return CartTable.readRows(waitAllVisible(CartTable.PRODUCT_ROWS));
    }

    @Step("Click 'Proceed To Checkout'")
    public CheckoutPage clickProceedToCheckout() {
        click(proceedToCheckoutButton);
        return new CheckoutPage(driver);
    }
}
