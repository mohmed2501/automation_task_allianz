package pages.components;

import models.CartItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.TextUtils;

import java.util.List;

/**
 * The products table appears on both the Cart page and the Checkout page ("Review Your Order"),
 * so it lives in one reusable component instead of being duplicated.
 */
public final class CartTable {

    public static final By PRODUCT_ROWS = By.cssSelector("tr[id^='product-']");

    private CartTable() {
    }

    public static List<CartItem> readRows(List<WebElement> rows) {
        return rows.stream().map(row -> new CartItem(
                row.findElement(By.cssSelector(".cart_description h4 a")).getText().trim(),
                TextUtils.extractNumber(row.findElement(By.cssSelector(".cart_price p")).getText()),
                TextUtils.extractNumber(row.findElement(By.cssSelector(".cart_quantity button")).getText()),
                TextUtils.extractNumber(row.findElement(By.cssSelector(".cart_total_price")).getText())
        )).toList();
    }
}
