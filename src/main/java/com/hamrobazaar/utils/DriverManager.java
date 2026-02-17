package com.hamrobazaar.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * DriverManager - Thread-safe WebDriver manager
 * Manages WebDriver instances for parallel test execution
 */
public class DriverManager {
    
    private static final Logger log = LogManager.getLogger(DriverManager.class);
    
    // ThreadLocal to store WebDriver instances for each thread
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    
    /**
     * Get the WebDriver instance for current thread
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        WebDriver currentDriver = driver.get();
        System.out.println("DriverManager.getDriver() called - Thread: " + Thread.currentThread().getId());
        System.out.println("Driver in ThreadLocal: " + currentDriver);
        log.info("DriverManager.getDriver() - Thread: {}, Driver: {}", Thread.currentThread().getId(), currentDriver);
        return currentDriver;
    }
    
    /**
     * Set the WebDriver instance for current thread
     * @param driverInstance WebDriver instance to set
     */
    public static void setDriver(WebDriver driverInstance) {
        driver.set(driverInstance);
        log.info("WebDriver instance set for thread: {}", Thread.currentThread().getId());
    }
    
    /**
     * Quit and remove the WebDriver instance for current thread
     */
    public static void quitDriver() {
        if (driver.get() != null) {
            try {
                driver.get().quit();
                log.info("WebDriver quit successfully for thread: {}", Thread.currentThread().getId());
            } catch (Exception e) {
                log.error("Error while quitting WebDriver: {}", e.getMessage(), e);
            } finally {
                driver.remove();
            }
        }
    }
}