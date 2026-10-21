package utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AllureHelper {

    private static final Path SCREENSHOTS_DIR = Paths.get("target", "screenshots");
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private AllureHelper() {
    }

    /** Attaches a screenshot to the Allure report. */
    public static void attachScreenshot(WebDriver driver, String name) {
        if (driver == null) {
            return;
        }
        byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), "png");
    }

    /**
     * Attaches a screenshot to the Allure report AND saves it as a file,
     * e.g. target/screenshots/passed/registerNewAccount_20261020_153000.png
     */
    public static void captureResultScreenshot(WebDriver driver, String testName, String status) {
        if (driver == null) {
            return;
        }
        byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(status.toUpperCase() + " - " + testName, "image/png", new ByteArrayInputStream(png), "png");

        try {
            Path folder = Files.createDirectories(SCREENSHOTS_DIR.resolve(status.toLowerCase()));
            Files.write(folder.resolve(testName + "_" + LocalDateTime.now().format(TIMESTAMP) + ".png"), png);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save screenshot for " + testName, e);
        }
    }

    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content, ".txt");
    }
}
