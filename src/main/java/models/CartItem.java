package models;

/** One product line in the cart: name, unit price, quantity and line total. */
public record CartItem(String name, int price, int quantity, int total) {

    /** Builds an expected item whose total is calculated as price x quantity. */
    public static CartItem of(String name, int price, int quantity) {
        return new CartItem(name, price, quantity, price * quantity);
    }
}
