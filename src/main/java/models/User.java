package models;

/** All the data needed to register a user. Built with random values by TestDataFactory. */
public record User(
        String title,          // "Mr" or "Mrs"
        String name,
        String email,
        String password,
        int birthDay,
        int birthMonth,
        int birthYear,
        String firstName,
        String lastName,
        String company,
        String address1,
        String address2,
        String country,
        String state,
        String city,
        String zipcode,
        String mobileNumber) {
}
