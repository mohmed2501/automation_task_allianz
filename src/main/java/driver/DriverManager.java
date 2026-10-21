package driver;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import utils.ConfigReader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

/**
 * Creates and holds the WebDriver (one per thread).
 * Selenium Manager (built into Selenium 4) downloads the right driver binary automatically.
 */
public final class DriverManager {

    public static final Path DOWNLOAD_DIR = Paths.get("target", "downloads").toAbsolutePath();

    /** The site is full of Google ads that cover buttons, so we block the ad domains. */
    private static final String BLOCK_ADS = "--host-resolver-rules="
            + "MAP *.googlesyndication.com 127.0.0.1, "
            + "MAP *.doubleclick.net 127.0.0.1, "
            + "MAP *.googleadservices.com 127.0.0.1, "
            + "MAP *.adtrafficquality.google 127.0.0.1, "
            + "MAP fundingchoicesmessages.google.com 127.0.0.1";

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void initDriver() {
        String browser = ConfigReader.get("browser").toLowerCase();
        WebDriver driver = switch (browser) {
            case "edge" -> new EdgeDriver(withCommonOptions(new EdgeOptions()));
            case "chrome" -> new ChromeDriver(withCommonOptions(new ChromeOptions()));
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        DRIVER.set(driver);
    }

    private static <T extends ChromiumOptions<T>> T withCommonOptions(T options) {
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments(BLOCK_ADS, "--start-maximized", "--disable-notifications");
        if (ConfigReader.getBoolean("headless")) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        options.setExperimentalOption("prefs", Map.of(
                "download.default_directory", DOWNLOAD_DIR.toString(),
                "download.prompt_for_download", false,
                "download.directory_upgrade", true));
        return options;
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
