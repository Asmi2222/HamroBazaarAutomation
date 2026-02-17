package com.hamrobazaar.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtil - Utility class to capture screenshots
 * Saves screenshots to the screenshots folder
 */
public class ScreenshotUtil {
    
    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_DIR = "screenshots/";
    
    /**
     * Capture screenshot and save to file
     * @param driver WebDriver instance
     * @param testName Name of the test (for filename)
     * @return Path to the screenshot file
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        
        log.info("=== SCREENSHOT CAPTURE STARTED ===");
        log.info("Test name: {}", testName);
        log.info("Driver null? {}", (driver == null));
        
        if (driver == null) {
            log.error("WebDriver is null. Cannot capture screenshot.");
            System.err.println("ERROR: WebDriver is NULL - cannot capture screenshot!");
            return null;
        }
        
        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_DIR);
            log.info("Screenshot directory: {}", screenshotDir.getAbsolutePath());
            
            if (!screenshotDir.exists()) {
                boolean created = screenshotDir.mkdirs();
                log.info("Created screenshots directory: {} - Success: {}", SCREENSHOT_DIR, created);
                System.out.println("Created screenshots directory: " + screenshotDir.getAbsolutePath());
            }
            
            // Generate timestamp for unique filename
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            
            // Create filename
            String fileName = testName + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_DIR + fileName;
            
            log.info("Screenshot will be saved to: {}", filePath);
            System.out.println("Taking screenshot: " + filePath);
            
            // Take screenshot
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            File destination = new File(filePath);
            
            log.info("Screenshot source: {}", source.getAbsolutePath());
            log.info("Screenshot destination: {}", destination.getAbsolutePath());
            
            // Copy screenshot to destination
            FileUtils.copyFile(source, destination);
            
            log.info("Screenshot captured successfully: {}", filePath);
            System.out.println("✓ Screenshot saved: " + filePath);
            log.info("=== SCREENSHOT CAPTURE COMPLETED ===");
            
            return filePath;
            
        } catch (Exception e) {
            log.error("Failed to capture screenshot: {}", e.getMessage(), e);
            System.err.println("ERROR capturing screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Capture screenshot with default filename
     * @param driver WebDriver instance
     * @return Path to the screenshot file
     */
    public static String captureScreenshot(WebDriver driver) {
        return captureScreenshot(driver, "Screenshot");
    }
    
    /**
     * Capture screenshot for failed test (used by TestListener)
     * @param testName Name of the failed test
     * @return Path to the screenshot file
     */
    public static String captureScreenshot(String testName) {
        // This method will be used by ExtentReportListener
        // It will get the driver from DriverManager
        
        System.out.println("=== captureScreenshot(testName) called ===");
        System.out.println("Test name: " + testName);
        
        try {
            log.info("Getting WebDriver from DriverManager");
            System.out.println("Getting driver from DriverManager...");
            
            WebDriver driver = DriverManager.getDriver();
            
            System.out.println("Driver from DriverManager: " + driver);
            log.info("Driver from DriverManager: {}", driver);
            
            if (driver == null) {
                log.error("WebDriver is null in DriverManager. Cannot capture screenshot.");
                System.err.println("ERROR: Driver is NULL from DriverManager!");
                return null;
            }
            
            System.out.println("Calling captureScreenshot with driver...");
            String result = captureScreenshot(driver, testName + "_FAILED");
            System.out.println("Screenshot result: " + result);
            
            return result;
            
        } catch (Exception e) {
            log.error("Failed to capture screenshot for test: {}", testName, e);
            System.err.println("EXCEPTION in captureScreenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Delete old screenshots (optional cleanup method)
     * Deletes screenshots older than specified days
     * @param days Number of days to keep screenshots
     */
    public static void cleanupOldScreenshots(int days) {
        try {
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                return;
            }
            
            File[] files = screenshotDir.listFiles();
            if (files == null) {
                return;
            }
            
            long cutoffTime = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);
            
            int deletedCount = 0;
            for (File file : files) {
                if (file.isFile() && file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                }
            }
            
            if (deletedCount > 0) {
                log.info("Cleaned up {} old screenshots", deletedCount);
            }
            
        } catch (Exception e) {
            log.error("Failed to cleanup old screenshots: {}", e.getMessage(), e);
        }
    }
}