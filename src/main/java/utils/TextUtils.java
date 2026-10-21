package utils;

public final class TextUtils {

    private TextUtils() {
    }

    /** "Rs. 500" -> 500 */
    public static int extractNumber(String text) {
        return Integer.parseInt(text.replaceAll("\\D", ""));
    }
}
