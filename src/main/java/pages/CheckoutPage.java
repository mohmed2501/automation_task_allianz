package pages;

import io.qameta.allure.Step;
import models.CartItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.components.CartTable;
import utils.TextUtils;

import java.util.List;

public class CheckoutPage extends BasePage {

    private final By deliveryAddress = By.id("address_delivery");
    private final By billingAddress = By.id("address_invoice");
    private final By totalAmount = By.xpath("//tr[.//b[normalize-space()='Total Amount']]//p[@class='cart_total_price']");
    private final By commentBox = By.cssSelector("textarea[name='message']");
    private final By placeOrderButton = By.cssSelector("a[href='/payment']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public String getDeliveryAddress() {
        return getText(deliveryAddress);
    }

    public String getBillingAddress() {
        return getText(billingAddress);
    }

    public List<CartItem> getOrderItems() {
        return CartTable.readRows(waitAllVisible(CartTable.PRODUCT_ROWS));
    }

    public int getTotalAmount() {
        return TextUtils.extractNumber(getText(totalAmount));
    }

    @Step("Enter comment and click 'Place Order'")
    public PaymentPage enterCommentAndPlaceOrder(String comment) {
        type(commentBox, comment);
        click(placeOrderButton);
        return new PaymentPage(driver);
    }
}
