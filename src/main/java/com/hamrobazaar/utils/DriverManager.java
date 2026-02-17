package com.hamrobazaar.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class DriverManager {
    
    private static final Logger log = LogManager.getLogger(DriverManager.class);
    
    
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    
   
    public static WebDriver getDriver() {
        WebDriver currentDriver = driver.get();
        System.out.println("DriverManager.getDriver() called - Thread: " + Thread.currentThread().getId());
        System.out.println("Driver in ThreadLocal: " + currentDriver);
        log.info("DriverManager.getDriver() - Thread: {}, Driver: {}", Thread.currentThread().getId(), currentDriver);
        return currentDriver;
    }
    
    
    public static void setDriver(WebDriver driverInstance) {
        driver.set(driverInstance);
        log.info("WebDriver instance set for thread: {}", Thread.currentThread().getId());
    }
    
    
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