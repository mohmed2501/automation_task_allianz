package tests;

import base.BaseTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import models.CartItem;
import models.User;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.InvoicePage;
import pages.OrderPlacedPage;
import pages.ProductDetailsPage;
import pages.ProductsPage;
import utils.AllureHelper;
import utils.ApiHelper;
import utils.FileHelper;
import utils.TestDataFactory;

import java.nio.file.Path;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Feature("Search, Cart and Payment")
public class PurchaseTests extends BaseTest {

    private static final int SECOND_PRODUCT_QUANTITY = 4;

    private User user;

    @BeforeClass(alwaysRun = true)
    public void createUser() {
        user = TestDataFactory.newUser();
        ApiHelper.createAccount(user);
    }

    @Test(description = "Search, add products to the cart and pay for them")
    public void searchAddToCartAndPay() {
        String searchTerm = TestDataFactory.randomSearchTerm();

        loginAsPrecondition();

        // 1-3: home page -> All Products
        assertTrue(homePage.isHomePageVisible(), "Home page is not visible");
        ProductsPage productsPage = homePage.clickProducts();
        assertEquals(productsPage.getPageHeader().toUpperCase(), "ALL PRODUCTS");
        assertTrue(productsPage.getCurrentUrl().contains("/products"), "Not on the products page");

        // 4-6: search
        productsPage.search(searchTerm);
        assertEquals(productsPage.getPageHeader().toUpperCase(), "SEARCHED PRODUCTS");
        assertTrue(productsPage.areAllProductsVisible(), "Not all searched products are visible");
        AllureHelper.attachText("Search results for '" + searchTerm + "'",
                String.join("\n", productsPage.getProductNames()));

        // 7: first product -> add to cart, continue shopping
        CartItem firstAdded = productsPage.hoverAndAddToCart(1);
        productsPage.clickContinueShopping();

        // 8-11: second product -> details, quantity 4, add to cart, view cart
        ProductDetailsPage details = productsPage.hoverAndViewProduct(2);
        assertTrue(details.isOpened(), "Product details page is not opened");
        CartItem secondAdded = CartItem.of(details.getProductName(), details.getProductPrice(), SECOND_PRODUCT_QUANTITY);
        CartPage cartPage = details
                .setQuantity(SECOND_PRODUCT_QUANTITY)
                .clickAddToCart()
                .clickViewCart();

        // 12-13: verify cart
        List<CartItem> expectedItems = List.of(firstAdded, secondAdded);
        verifyCartItems(cartPage.getCartItems(), expectedItems);

        // 14-15: checkout -> address + order review
        CheckoutPage checkoutPage = cartPage.clickProceedToCheckout();
        verifyAddress(checkoutPage.getDeliveryAddress(), "Delivery address");
        verifyAddress(checkoutPage.getBillingAddress(), "Billing address");
        verifyCartItems(checkoutPage.getOrderItems(), expectedItems);
        assertEquals(checkoutPage.getTotalAmount(), firstAdded.total() + secondAdded.total(), "Total amount is wrong");

        // 16-19: comment, place order, pay
        OrderPlacedPage orderPlaced = checkoutPage
                .enterCommentAndPlaceOrder(TestDataFactory.randomComment())
                .fillPaymentDetails(TestDataFactory.newPaymentCard(user))
                .clickPayAndConfirm();
        verifyOrderSuccess(orderPlaced);

        // 20-21: download invoice, open it, screenshot into the report
        Path invoice = orderPlaced.downloadInvoice();
        String invoiceText = FileHelper.readFile(invoice);
        AllureHelper.attachText("Invoice file content", invoiceText);
        assertTrue(invoiceText.contains(String.valueOf(firstAdded.total() + secondAdded.total())),
                "Invoice does not contain the order total");

        InvoicePage invoicePage = new InvoicePage(driver).open(invoice).attachScreenshot();
        assertFalse(invoicePage.getContent().isBlank(), "Invoice is empty");
        invoicePage.close();
    }

    @Step("Precondition: log in (checkout requires a logged-in user)")
    private void loginAsPrecondition() {
        homePage = homePage.clickSignupLogin().login(user.email(), user.password());
        assertEquals(homePage.getLoggedInText(), "Logged in as " + user.name());
    }

    @Step("Verify products, prices, quantities and totals")
    private void verifyCartItems(List<CartItem> actualItems, List<CartItem> expectedItems) {
        assertEquals(actualItems.size(), expectedItems.size(), "Wrong number of products in the cart");
        for (CartItem expected : expectedItems) {
            CartItem actual = actualItems.stream()
                    .filter(item -> item.name().equals(expected.name()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Product not found in cart: " + expected.name()));

            assertEquals(actual.price(), expected.price(), "Wrong price for " + expected.name());
            assertEquals(actual.quantity(), expected.quantity(), "Wrong quantity for " + expected.name());
            // the line total must equal price x quantity (checks the 4x product calculation)
            assertEquals(actual.total(), actual.price() * actual.quantity(), "Wrong total for " + expected.name());
        }
    }

    @Step("Verify {label} matches the registered user")
    private void verifyAddress(String address, String label) {
        List<String> expectedParts = List.of(
                user.firstName(), user.lastName(), user.company(), user.address1(), user.address2(),
                user.city(), user.state(), user.zipcode(), user.country(), user.mobileNumber());
        for (String part : expectedParts) {
            assertTrue(address.contains(part), label + " is missing '" + part + "'. Actual:\n" + address);
        }
    }

    @Step("Verify the order was placed successfully")
    private void verifyOrderSuccess(OrderPlacedPage orderPlaced) {
        String flash = orderPlaced.getFlashSuccessMessage();
        if (flash != null) {
            assertEquals(flash, "Your order has been placed successfully!");
        } else {
            AllureHelper.attachText("Note", "The flash message disappeared before it could be read; "
                    + "verified via the order confirmation page instead.");
        }
        assertEquals(orderPlaced.getHeader().toUpperCase(), "ORDER PLACED!");
        assertTrue(orderPlaced.getConfirmationText().contains("Your order has been confirmed"),
                "Order confirmation text is missing");
    }

    @AfterClass(alwaysRun = true)
    public void deleteUser() {
        ApiHelper.deleteAccount(user);
    }
}
