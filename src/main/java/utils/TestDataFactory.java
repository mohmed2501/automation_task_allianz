package utils;

import models.PaymentCard;
import models.User;
import net.datafaker.Faker;

import java.time.Year;

/** Generates fresh random test data for every run, so tests never clash with old data. */
public final class TestDataFactory {

    private static final Faker FAKER = new Faker();

    /** Countries offered in the site's country dropdown. */
    private static final String[] COUNTRIES =
            {"India", "United States", "Canada", "Australia", "Israel", "New Zealand", "Singapore"};

    /** Search terms that always return at least two products. */
    private static final String[] SEARCH_TERMS = {"Top", "Jeans", "Dress", "Tshirt"};

    private TestDataFactory() {
    }

    public static User newUser() {
        String firstName = FAKER.name().firstName().replaceAll("[^A-Za-z]", "");
        String lastName = FAKER.name().lastName().replaceAll("[^A-Za-z]", "");
        return new User(
                FAKER.options().option("Mr", "Mrs"),
                firstName + " " + lastName,
                uniqueEmail(firstName),
                randomPassword(),
                FAKER.number().numberBetween(1, 29),
                FAKER.number().numberBetween(1, 13),
                FAKER.number().numberBetween(1970, 2004),
                firstName,
                lastName,
                FAKER.company().name(),
                FAKER.address().streetAddress(),
                FAKER.address().secondaryAddress(),
                FAKER.options().option(COUNTRIES),
                FAKER.address().state(),
                FAKER.address().city(),
                FAKER.address().zipCode(),
                FAKER.number().digits(10));
    }

    public static String uniqueEmail(String prefix) {
        return prefix.toLowerCase() + "." + System.currentTimeMillis() + FAKER.number().digits(3) + "@testmail.com";
    }

    public static String randomPassword() {
        return FAKER.internet().password(10, 16, true, false, true);
    }

    public static String randomSearchTerm() {
        return FAKER.options().option(SEARCH_TERMS);
    }

    public static String randomComment() {
        return FAKER.lorem().sentence(12);
    }

    public static PaymentCard newPaymentCard(User user) {
        return new PaymentCard(
                user.firstName() + " " + user.lastName(),
                FAKER.number().digits(16),
                FAKER.number().digits(3),
                String.format("%02d", FAKER.number().numberBetween(1, 13)),
                String.valueOf(Year.now().getValue() + FAKER.number().numberBetween(1, 6)));
    }
}
