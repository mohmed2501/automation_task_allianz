package utils;

import models.User;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Uses the site's public API to create and delete accounts.
 * This keeps every test class independent: login and checkout tests get their own fresh user
 * without depending on the register test passing first.
 */
public final class ApiHelper {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String BASE_URL = ConfigReader.get("baseUrl");

    private ApiHelper() {
    }

    public static void createAccount(User user) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("name", user.name());
        form.put("email", user.email());
        form.put("password", user.password());
        form.put("title", user.title());
        form.put("birth_date", String.valueOf(user.birthDay()));
        form.put("birth_month", String.valueOf(user.birthMonth()));
        form.put("birth_year", String.valueOf(user.birthYear()));
        form.put("firstname", user.firstName());
        form.put("lastname", user.lastName());
        form.put("company", user.company());
        form.put("address1", user.address1());
        form.put("address2", user.address2());
        form.put("country", user.country());
        form.put("zipcode", user.zipcode());
        form.put("state", user.state());
        form.put("city", user.city());
        form.put("mobile_number", user.mobileNumber());

        String body = send("POST", "/api/createAccount", form);
        if (!body.replace(" ", "").contains("\"responseCode\":201")) {
            throw new IllegalStateException("Could not create test account via API. Response: " + body);
        }
    }

    /** Best-effort cleanup: never fails the test run. */
    public static void deleteAccount(User user) {
        try {
            send("DELETE", "/api/deleteAccount", Map.of("email", user.email(), "password", user.password()));
        } catch (RuntimeException e) {
            System.out.println("Cleanup failed for " + user.email() + ": " + e.getMessage());
        }
    }

    private static String send(String method, String path, Map<String, String> form) {
        String body = form.entrySet().stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            return CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            throw new IllegalStateException("API call failed: " + path, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("API call interrupted: " + path, e);
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
