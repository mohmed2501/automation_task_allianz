package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.TextUtils;

public class ProductDetailsPage extends BasePage {

    private final By productInfo = By.cssSelector(".product-information");
    private final By productName = By.cssSelector(".product-information h2");
    private final By productPrice = By.cssSelector(".product-information span span");
    private final By quantityInput = By.id("quantity");
    private final By addToCartButton = By.cssSelector("button.cart");
    private final By viewCartLink = By.cssSelector("#cartModal a[href='/view_cart']");

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isOpened() {
        return isVisible(productInfo) && getCurrentUrl().contains("/product_details/");
    }

    public String getProductName() {
        return getText(productName);
    }

    public int getProductPrice() {
        return TextUtils.extractNumber(getText(productPrice));
    }

    @Step("Set quantity to {quantity}")
    public ProductDetailsPage setQuantity(int quantity) {
        type(quantityInput, String.valueOf(quantity));
        return this;
    }

    @Step("Click 'Add to cart'")
    public ProductDetailsPage clickAddToCart() {
        click(addToCartButton);
        return this;
    }

    @Step("Click 'View Cart'")
    public CartPage clickViewCart() {
        click(viewCartLink);
        return new CartPage(driver);
    }
}
