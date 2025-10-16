package com.example.nikonbe;

import com.example.nikonbe.config.core.DotenvConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class NikonBeApplication {

    public static void main(String[] args) {
        // Tự động format code trước khi chạy ứng dụng
        formatCodeBeforeStart();

        SpringApplication app = new SpringApplication(NikonBeApplication.class);
        app.addInitializers(new DotenvConfig());
        app.run(args);
    }

    /**
     * Tự động format code sử dụng Maven Spotless plugin
     */
    private static void formatCodeBeforeStart() {
        try {
            System.out.println("==> Auto-formatting code before starting application...");

            // Tìm thư mục root của project
            String projectRoot = findProjectRoot();
            if (projectRoot == null) {
                System.out.println("==> Could not find project root, skipping format");
                return;
            }

            // Chạy lệnh Maven spotless:apply
            ProcessBuilder processBuilder = new ProcessBuilder();

            // Kiểm tra OS để sử dụng command phù hợp
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                processBuilder.command("cmd", "/c", "mvn", "spotless:apply", "-q");
            } else {
                processBuilder.command("bash", "-c", "mvn spotless:apply -q");
            }

            processBuilder.directory(new File(projectRoot));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);

            if (finished && process.exitValue() == 0) {
                System.out.println("==> Code formatting completed successfully!");
            } else {
                System.out.println("==> Code formatting failed or timed out");
            }

        } catch (Exception e) {
            System.out.println("⚠ Error during code formatting: " + e.getMessage());
        }
    }

    private static String findProjectRoot() {
        try {
            Path currentPath = Paths.get("").toAbsolutePath();

            while (currentPath != null) {
                Path pomPath = currentPath.resolve("pom.xml");
                if (Files.exists(pomPath)) {
                    return currentPath.toString();
                }
                currentPath = currentPath.getParent();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
