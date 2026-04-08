package com.pestpredictor;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Main entry point for the Crop Pest Predictor application.
 *
 * Startup sequence:
 *   1. Spring Boot context starts (embedded Tomcat on port 8080)
 *   2. JavaFX WebView window opens and loads http://localhost:8080/login
 */
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class
})
@ServletComponentScan("com.pestpredictor.servlet")
public class MainApp extends SpringBootServletInitializer {

    public static void main(String[] args) {

        // Start Spring Boot in background thread
        Thread springThread = new Thread(() -> 
                SpringApplication.run(MainApp.class, args)
        );
        springThread.setDaemon(false);
        springThread.start();

        // Wait for server to start
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        // Launch JavaFX UI
        Application.launch(com.pestpredictor.view.JavaFXApp.class, args);
    }
}