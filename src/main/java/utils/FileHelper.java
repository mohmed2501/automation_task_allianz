package utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public final class FileHelper {

    private FileHelper() {
    }

    /** Creates the folder if needed and deletes anything already inside it. */
    public static void cleanDirectory(Path dir) {
        try {
            Files.createDirectories(dir);
            try (Stream<Path> files = Files.list(dir)) {
                for (Path file : files.toList()) {
                    Files.deleteIfExists(file);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Waits until a fully downloaded file whose name starts with the prefix appears in the folder. */
    public static Path waitForDownload(Path dir, String prefix, Duration timeout) {
        Instant deadline = Instant.now().plus(timeout);
        while (Instant.now().isBefore(deadline)) {
            Optional<Path> file = findCompletedFile(dir, prefix);
            if (file.isPresent()) {
                return file.get();
            }
            sleep(500);
        }
        throw new AssertionError("No file starting with '" + prefix + "' was downloaded to " + dir);
    }

    private static Optional<Path> findCompletedFile(Path dir, String prefix) {
        try (Stream<Path> files = Files.list(dir)) {
            return files
                    .filter(f -> f.getFileName().toString().startsWith(prefix))
                    .filter(f -> !f.toString().endsWith(".crdownload") && !f.toString().endsWith(".tmp"))
                    .filter(f -> f.toFile().length() > 0)
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static String readFile(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
