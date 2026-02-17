package com.hamrobazaar.utils;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;



/**
 * ExtentReportListener - TestNG Listener that updates ExtentReport
 * This class listens to TestNG events and logs them to ExtentReport
 */
public class ExtentReportListener implements ITestListener {
    
    // Log4j2 Logger
    private static final Logger log = LogManager.getLogger(ExtentReportListener.class);
    
    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    
    /**
     * Called when test suite starts
     */
    @Override
    public void onStart(ITestContext context) {
        log.info("========================================");
        log.info("Test Suite Started: {}", context.getName());
        log.info("========================================");
    }
    
    /**
     * Called when test suite finishes
     */
    @Override
    public void onFinish(ITestContext context) {
        log.info("========================================");
        log.info("Test Suite Finished: {}", context.getName());
        log.info("========================================");
        
        // Flush the report
        ExtentManager.flushReport();
        
        String reportPath = ExtentManager.getReportPath();
        log.info("ExtentReport generated at: {}", reportPath);
        
        // Print to console so user can see it
        System.out.println("\n========================================");
        System.out.println("✓ EXTENT REPORT GENERATED");
        System.out.println("Location: " + reportPath);
        System.out.println("========================================\n");
    }
    
    /**
     * Called when a test starts
     */
    @Override
    public void onTestStart(ITestResult result) {
        // Create a new test in the report
        ExtentTest test = extent.createTest(result.getMethod().getMethodName(),
                result.getMethod().getDescription());
        extentTest.set(test);
        
        // Log test start
        extentTest.get().log(Status.INFO, "Test Started: " + result.getMethod().getMethodName());
        log.info("Test Started: {}", result.getMethod().getMethodName());
    }
    
    /**
     * Called when a test passes
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        // Log test pass
        extentTest.get().log(Status.PASS, 
                MarkupHelper.createLabel("Test PASSED: " + result.getMethod().getMethodName(), 
                        ExtentColor.GREEN));
        
        log.info("Test Passed: {}", result.getMethod().getMethodName());
    }
    
    /**
     * Called when a test fails
     */
    @Override
    public void onTestFailure(ITestResult result) {
        // Log test failure
        extentTest.get().log(Status.FAIL, 
                MarkupHelper.createLabel("Test FAILED: " + result.getMethod().getMethodName(), 
                        ExtentColor.RED));
        
        // Log the exception
        extentTest.get().log(Status.FAIL, "Failure Reason: " + result.getThrowable());
        
        // Log stack trace
        String stackTrace = Arrays.toString(result.getThrowable().getStackTrace());
        extentTest.get().log(Status.FAIL, "<details><summary>Stack Trace</summary>" + 
                stackTrace.replaceAll(",", "<br>") + "</details>");
        
        log.error("Test Failed: {}", result.getMethod().getMethodName());
        log.error("Failure Reason: ", result.getThrowable());
        
        // Capture screenshot on failure
        try {
            log.info("Attempting to capture screenshot for failed test: {}", result.getMethod().getMethodName());
            
            String screenshotPath = ScreenshotUtil.captureScreenshot(result.getMethod().getMethodName());
            
            if (screenshotPath != null && !screenshotPath.isEmpty()) {
                // Convert to relative path for ExtentReport
                // ExtentReport HTML is in reports/, screenshot is in screenshots/
                // So we need to go up one level: ../screenshots/filename.png
                String fileName = screenshotPath.substring(screenshotPath.lastIndexOf("/") + 1);
                String relativePath = "../screenshots/" + fileName;
                
                log.info("Screenshot absolute path: {}", screenshotPath);
                log.info("Screenshot relative path for report: {}", relativePath);
                
                extentTest.get().addScreenCaptureFromPath(relativePath, "Failure Screenshot");
                log.info("Screenshot attached to ExtentReport");
            } else {
                extentTest.get().log(Status.WARNING, "Screenshot capture returned null path");
                log.warn("Screenshot path is null - screenshot may not have been captured");
            }
        } catch (Exception e) {
            extentTest.get().log(Status.WARNING, "Could not capture screenshot: " + e.getMessage());
            log.error("Exception while capturing screenshot: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Called when a test is skipped
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        // Log test skip
        extentTest.get().log(Status.SKIP, 
                MarkupHelper.createLabel("Test SKIPPED: " + result.getMethod().getMethodName(), 
                        ExtentColor.YELLOW));
        
        extentTest.get().log(Status.SKIP, "Skip Reason: " + result.getThrowable());
        
        log.warn("Test Skipped: {}", result.getMethod().getMethodName());
    }
    
    /**
     * Get the current ExtentTest instance
     * @return Current ExtentTest
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }
}