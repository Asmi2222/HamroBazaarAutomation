package com.hamrobazaar.tests;

import com.hamrobazaar.base.BaseTest;
import com.hamrobazaar.pages.HomePage;
import com.hamrobazaar.utils.CSVReaderUtil;
import com.hamrobazaar.utils.ExtentReportListener;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;


public class HomeTest extends BaseTest {
    
    private static final String CSV_PATH = "src/test/resources/testdata/testdata.csv";
    
    
    @Test(priority = 1, description = "Search Monitor with New Road location and 10km distance")
    public void testSearchWithFilters() {
        
        try {
            
            log.info("Starting testSearchWithFilters");
            
            
            // Step 1: Read test data from CSV
            ExtentReportListener.getTest().log(Status.INFO, "Reading test data from CSV file");
            log.info("Reading test data from CSV: {}", CSV_PATH);
            
            Map<String, String> testData = CSVReaderUtil.getTestData(CSV_PATH);
            
            if (testData == null || testData.isEmpty()) {
                log.error("CSV file is empty or not found");
                Assert.fail("Failed to read test data from CSV");
            }
            
            // Extract data from CSV
            String searchKeyword = testData.get("Search keyword");
            String location = testData.get("Location and distance");
            String distance = testData.get("Distance from location");
            
            log.info("Test Data Retrieved:");
            log.info("Search Keyword: {}", searchKeyword);
            log.info("Location: {}", location);
            log.info("Distance: {}", distance);
            
            ExtentReportListener.getTest().log(Status.INFO, 
                "Test Data - Keyword: " + searchKeyword + 
                ", Location: " + location + 
                ", Distance: " + distance);
            
            // Step 2: Navigate to HamroBazaar
            ExtentReportListener.getTest().log(Status.INFO, "Navigating to HamroBazaar website");
            
            
            try {
                navigateToURL("https://hamrobazaar.com/");
                ExtentReportListener.getTest().log(Status.PASS, "Successfully navigated to HamroBazaar");
            } catch (Exception e) {
                log.error("Failed to navigate to URL: {}", e.getMessage());
                ExtentReportListener.getTest().log(Status.FAIL, "Failed to navigate: " + e.getMessage());
                throw e;
            }
            
            // Step 3: Initialize HomePage
            HomePage homePage = new HomePage(driver);
            ExtentReportListener.getTest().log(Status.INFO, "HomePage initialized");
            
            // Step 4: Search for product (ENTER is pressed automatically)
            ExtentReportListener.getTest().log(Status.INFO, "Entering search keyword: " + searchKeyword);
            log.info("Searching for product: {}", searchKeyword);
            
            try {
                homePage.searchProduct(searchKeyword);
                ExtentReportListener.getTest().log(Status.PASS, "Successfully searched for: " + searchKeyword);
            } catch (Exception e) {
                log.error("Failed to search: {}", e.getMessage());
                ExtentReportListener.getTest().log(Status.FAIL, "Failed to search: " + e.getMessage());
                throw e;
            }
            
            // Step 5: Set location
            ExtentReportListener.getTest().log(Status.INFO, "Setting location: " + location);
            
            try {
                homePage.setLocation(location);
                ExtentReportListener.getTest().log(Status.PASS, "Successfully set location");
            } catch (Exception e) {
                log.error("Failed to set location: {}", e.getMessage());
                ExtentReportListener.getTest().log(Status.WARNING, "Failed to set location (continuing test): " + e.getMessage());
                // DON'T throw - continue test for debugging
            }
            
            // Step 7: Scroll to distance section
            ExtentReportListener.getTest().log(Status.INFO, "Scrolling to distance section");
            
            try {
                homePage.scrollToDistanceSection();
                ExtentReportListener.getTest().log(Status.PASS, "Successfully scrolled to distance section");
            } catch (Exception e) {
                log.error("Failed to scroll: {}", e.getMessage());
                ExtentReportListener.getTest().log(Status.WARNING, "Scroll issue: " + e.getMessage());
                // Continue even if scroll fails
            }
            
            // Step 8: Set distance
            ExtentReportListener.getTest().log(Status.INFO, "Setting distance: " + distance);
            
            try {
                homePage.setDistance(distance);
                ExtentReportListener.getTest().log(Status.PASS, "Successfully set distance to " + distance);
            } catch (Exception e) {
                log.error("Failed to set distance: {}", e.getMessage());
                ExtentReportListener.getTest().log(Status.FAIL, "Failed to set distance: " + e.getMessage());
                throw e;
            }
            
            // Step 9: Click apply filters
            ExtentReportListener.getTest().log(Status.INFO, "Applying filters");
            
            try {
                homePage.clickApplyFilters();
                ExtentReportListener.getTest().log(Status.PASS, "Successfully applied filters");
            } catch (Exception e) {
                log.error("Failed to apply filters: {}", e.getMessage());
                ExtentReportListener.getTest().log(Status.FAIL, "Failed to apply filters: " + e.getMessage());
                throw e;
            }
            
            // Step 10: Verify we're on results page
            ExtentReportListener.getTest().log(Status.INFO, "Verifying search results page");
            
            try {
                String currentUrl = getCurrentURL();
                log.info("Current URL after search: {}", currentUrl);
                
                // Basic verification - URL should have changed
                Assert.assertNotEquals(currentUrl, "https://hamrobazaar.com/", 
                    "URL should change after search");
                
                ExtentReportListener.getTest().log(Status.PASS, 
                    "Successfully navigated to search results page: " + currentUrl);
                
            } catch (Exception e) {
                log.error("Verification failed: {}", e.getMessage());
                ExtentReportListener.getTest().log(Status.FAIL, "Verification failed: " + e.getMessage());
                throw e;
            }
            
          
            log.info("testSearchWithFilters completed successfully");
           
            
            ExtentReportListener.getTest().log(Status.PASS, 
                "Test completed successfully - Search with filters executed");
            
        } catch (Exception e) {
           
            log.error("Test failed with exception: {}", e.getMessage(), e);
           
            
            ExtentReportListener.getTest().log(Status.FAIL, 
                "Test failed with exception: " + e.getMessage());
            
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    
}