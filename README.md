# Automation Exercise – Selenium POM Framework

UI tests for https://automationexercise.com using **Java 17, Selenium 4, TestNG, Maven, Allure and Datafaker**.

## Project structure

```
src
├── main/java
│   ├── driver/DriverManager.java        launches Chrome/Edge, blocks ads, sets download folder
│   ├── models/                          User, CartItem, PaymentCard (plain data records)
│   ├── pages/                           one class per page (Page Object Model)
│   │   ├── BasePage.java                shared wait/click/type/hover helpers
│   │   ├── HomePage, LoginSignupPage, AccountInfoPage, AccountCreatedPage
│   │   ├── ProductsPage, ProductDetailsPage, CartPage, CheckoutPage
│   │   ├── PaymentPage, OrderPlacedPage, InvoicePage
│   │   └── components/CartTable.java    cart table reused by Cart + Checkout pages
│   └── utils/
│       ├── TestDataFactory.java         dynamic random data (Datafaker)
│       ├── ApiHelper.java               create/delete accounts through the site API
│       ├── ConfigReader, FileHelper, AllureHelper, TextUtils
├── main/resources/config.properties     baseUrl, browser, headless, timeout
└── test/java
    ├── base/BaseTest.java               open browser + site before each test, screenshot on failure
    └── tests/RegisterTests, LoginTests, PurchaseTests
```

## How the design works

* **Pages** only know locators and actions. **Tests** only call page methods and assert.
* Page methods return the next page (`homePage.clickSignupLogin().signUp(...)`), so a test reads like the scenario.
* **Dynamic data**: every run generates a new user (name, unique email, password, DOB, address, country...),
  a random search term, a random comment and random card details.
* **Independent tests**: Login and Purchase tests create their own user through the site's API in
  `@BeforeClass`, so they don't depend on the register test. All created accounts are deleted afterwards.

## Run

```
mvn clean test                       # whole suite
mvn clean test -Dheadless=true       # without opening a browser window
mvn clean test -Dbrowser=edge
```

Single tests can also be run from IntelliJ (right-click a test → Run).

## Report

```
allure serve target/allure-results
```

Every test gets a final screenshot, whether it passed or failed. It is attached to the report as
"PASSED - testName" or "FAILED - testName" and also saved as a file:

```
target/screenshots/passed/registerNewAccount_20261020_153000.png
target/screenshots/failed/loginWithValidCredentials_20261020_153112.png
```

The report also contains every step, the search results, the invoice text and
a screenshot of the opened invoice.

## Notes

* The site is full of Google ads that cover buttons. The driver blocks the ad domains, and clicks fall
  back to a JavaScript click if something still covers the element.
* "Your order has been placed successfully!" is only shown for a split second before redirecting.
  The test asserts it when it can catch it, and always asserts the "Order Placed!" confirmation page.
* Checkout requires a logged-in user, so the purchase test logs in first as a precondition.
