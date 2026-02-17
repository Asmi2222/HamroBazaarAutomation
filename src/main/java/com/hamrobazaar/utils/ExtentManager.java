package com.hamrobazaar.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ExtentManager - Creates and manages ExtentReports instance
 * This class sets up the HTML report with configuration
 */
public class ExtentManager {
    
    private static ExtentReports extent;
    private static String reportPath;
    
    /**
     * Get or create ExtentReports instance
     * @return ExtentReports instance
     */
    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }
    
    /**
     * Create ExtentReports instance with configuration
     * @return ExtentReports instance
     */
    private static ExtentReports createInstance() {
        // Create reports directory if not exists
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
        
        // Generate report file name with timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        reportPath = "reports/HamroBazaar_Report_" + timestamp + ".html";
        
        // Create ExtentSparkReporter (HTML reporter)
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        
        // Configure the report
        sparkReporter.config().setDocumentTitle("HamroBazaar Automation Report");
        sparkReporter.config().setReportName("Monitor Search Test Report");
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
        sparkReporter.config().setEncoding("UTF-8");
        
        // Create ExtentReports instance and attach reporter
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        
        // Add system information to report
        extent.setSystemInfo("Application", "HamroBazaar");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Automation Engineer", "Asmi Bajracharya");
        extent.setSystemInfo("Role", "QA Engineer");
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        
        return extent;
    }
    
    /**
     * Get the report file path
     * @return Report file path
     */
    public static String getReportPath() {
        return reportPath;
    }
    
    /**
     * Flush the report (save all data to file)
     */
    public static void flushReport() {
        System.out.println("=== Flushing ExtentReport ===");
        if (extent != null) {
            extent.flush();
            System.out.println("✓ ExtentReport flushed successfully");
            System.out.println("Report should be at: " + reportPath);
            
            // Check if file exists
            if (reportPath != null) {
                java.io.File reportFile = new java.io.File(reportPath);
                if (reportFile.exists()) {
                    System.out.println("✓ Report file confirmed exists: " + reportFile.getAbsolutePath());
                    System.out.println("File size: " + reportFile.length() + " bytes");
                } else {
                    System.err.println("✗ WARNING: Report file not found at: " + reportFile.getAbsolutePath());
                }
            }
        } else {
            System.err.println("✗ WARNING: ExtentReports instance is null!");
        }
    }
}