package pages;

import io.qameta.allure.Step;
import models.CartItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.TextUtils;

import java.util.List;

/** "All Products" page, which also shows the search results. */
public class ProductsPage extends BasePage {

    private final By pageHeader = By.cssSelector(".features_items h2.title");
    private final By searchInput = By.id("search_product");
    private final By searchButton = By.id("submit_search");
    private final By productCards = By.cssSelector(".features_items .product-image-wrapper");
    private final By cartModal = By.cssSelector("#cartModal .modal-content");
    private final By continueShoppingButton = By.cssSelector("#cartModal button.close-modal");

    // Locators inside a single product card
    private final By cardName = By.cssSelector(".productinfo p");
    private final By cardPrice = By.cssSelector(".productinfo h2");
    private final By cardAddToCart = By.cssSelector(".product-overlay a.add-to-cart");
    private final By cardViewProduct = By.cssSelector(".choose a");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    /** "ALL PRODUCTS" before searching, "SEARCHED PRODUCTS" after. */
    public String getPageHeader() {
        return getText(pageHeader);
    }

    @Step("Search for product '{productName}'")
    public ProductsPage search(String productName) {
        type(searchInput, productName);
        click(searchButton);
        return this;
    }

    public List<WebElement> getProductCards() {
        return waitAllVisible(productCards);
    }

    public List<String> getProductNames() {
        return getProductCards().stream()
                .map(card -> card.findElement(cardName).getText().trim())
                .toList();
    }

    public boolean areAllProductsVisible() {
        List<WebElement> cards = getProductCards();
        return !cards.isEmpty() && cards.stream().allMatch(WebElement::isDisplayed);
    }

    /** position starts at 1 (1 = first product). Returns what was added, for verifying the cart later. */
    @Step("Hover over product #{position} and click 'Add to cart'")
    public CartItem hoverAndAddToCart(int position) {
        WebElement card = getProductCards().get(position - 1);
        String name = card.findElement(cardName).getText().trim();
        int price = TextUtils.extractNumber(card.findElement(cardPrice).getText());

        hoverAndClick(card, card.findElement(cardAddToCart));
        waitVisible(cartModal);
        return CartItem.of(name, price, 1);
    }

    @Step("Click 'Continue Shopping'")
    public ProductsPage clickContinueShopping() {
        click(continueShoppingButton);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(cartModal));
        return this;
    }

    @Step("Hover over product #{position} and click 'View Product'")
    public ProductDetailsPage hoverAndViewProduct(int position) {
        WebElement card = getProductCards().get(position - 1);
        hoverAndClick(card, card.findElement(cardViewProduct));
        return new ProductDetailsPage(driver);
    }
}
