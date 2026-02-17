package com.hamrobazaar.tests;

import com.hamrobazaar.base.BaseTest;
import com.hamrobazaar.pages.HomePage;
import com.hamrobazaar.pages.SearchResultsPage;
import com.hamrobazaar.utils.CSVReaderUtil;
import com.hamrobazaar.utils.ExtentReportListener;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * SearchResultsTest - Test class for search results functionality
 * Sorts results, extracts data, saves to CSV, and displays results
 */
public class SearchResultsTest extends BaseTest {
    
    private static final String CSV_PATH = "src/test/resources/testdata/testdata.csv";
    private static final String OUTPUT_DIR = "test-output/";
    
    // Timestamp generated once per test run
    private static final String TIMESTAMP = 
        new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date());
    
    private static final String OUTPUT_CSV = OUTPUT_DIR + "Search_Result_" + TIMESTAMP + ".csv";
    
    /**
     * Complete end-to-end test - ALL STEPS IN ONE SESSION:
     * 1. Search for Monitor with filters
     * 2. Sort by Low to High Price (WITHOUT CLOSING BROWSER)
     * 3. Extract 50 products
     * 4. Save to CSV
     * 5. Display in console
     */
    @Test(priority = 1, description = "Complete Search, Filter, Sort, Extract, and Save - All in One Session")
    public void testCompleteSearchFlowInOneSession() {
        
        try {
            log.info("========================================");
            log.info("Starting Complete End-to-End Test (Single Session)");
            log.info("========================================");
            
            // ===== STEP 1: Read Test Data =====
            ExtentReportListener.getTest().log(Status.INFO, "Step 1: Reading test data from CSV");
            Map<String, String> testData = CSVReaderUtil.getTestData(CSV_PATH);
            
            String searchKeyword = testData.get("Search keyword");
            String location = testData.get("Location and distance");
            String distance = testData.get("Distance from location");
            
            log.info("Test Data - Keyword: {}, Location: {}, Distance: {}", searchKeyword, location, distance);
            ExtentReportListener.getTest().log(Status.INFO, 
                "Test Data: " + searchKeyword + ", " + location + ", " + distance);
            
            // ===== STEP 2: Navigate to HamroBazaar =====
            ExtentReportListener.getTest().log(Status.INFO, "Step 2: Navigating to HamroBazaar");
            navigateToURL("https://hamrobazaar.com/");
            ExtentReportListener.getTest().log(Status.PASS, " Navigated to HamroBazaar");
            
            // ===== STEP 3: Search with Filters =====
            ExtentReportListener.getTest().log(Status.INFO, "Step 3: Searching with filters");
            log.info("Performing search with filters...");
            
            HomePage homePage = new HomePage(driver);
            
            try {
                homePage.searchProduct(searchKeyword);
                ExtentReportListener.getTest().log(Status.PASS, " Searched for: " + searchKeyword);
            } catch (Exception e) {
                ExtentReportListener.getTest().log(Status.FAIL, "Search failed: " + e.getMessage());
                throw e;
            }
            
            try {
                homePage.setLocation(location);
                ExtentReportListener.getTest().log(Status.PASS, " Set location: " + location);
            } catch (Exception e) {
                ExtentReportListener.getTest().log(Status.FAIL, "Location failed: " + e.getMessage());
                throw e;
            }
            
            try {
                homePage.scrollToDistanceSection();
                homePage.setDistance(distance);
                ExtentReportListener.getTest().log(Status.PASS, " Set distance: " + distance);
            } catch (Exception e) {
                ExtentReportListener.getTest().log(Status.FAIL, "Distance failed: " + e.getMessage());
                throw e;
            }
            
            try {
                homePage.clickApplyFilters();
                ExtentReportListener.getTest().log(Status.PASS, " Applied filters - Now on results page");
            } catch (Exception e) {
                ExtentReportListener.getTest().log(Status.FAIL, "Apply filters failed: " + e.getMessage());
                throw e;
            }
            
            log.info("Search with filters completed - Browser still open");
            
            // ===== STEP 4: Initialize Search Results Page (SAME SESSION) =====
            ExtentReportListener.getTest().log(Status.INFO, "Step 4: Initializing Search Results Page");
            SearchResultsPage resultsPage = new SearchResultsPage(driver);
            log.info("Search Results Page initialized - continuing in same session");
            
            // ===== STEP 5: Sort by Low to High Price =====
            ExtentReportListener.getTest().log(Status.INFO, "Step 5: Sorting by Low to High Price");
            log.info("Applying Low to High price sort...");
            
            try {
                resultsPage.applySortLowToHigh();
                ExtentReportListener.getTest().log(Status.PASS, " Successfully sorted by Low to High Price");
            } catch (Exception e) {
                log.error("Failed to sort results: {}", e.getMessage());
                ExtentReportListener.getTest().log(Status.FAIL, "Sort failed: " + e.getMessage());
                throw e;
            }
            
            // ===== STEP 5: Verify Price Sorting BEFORE Extraction =====
            ExtentReportListener.getTest().log(Status.INFO, "Step 5: Verifying prices are sorted Low to High");
            log.info("Verifying price sort before extraction...");
            
            try {
                boolean isSorted = resultsPage.verifyPriceSortedLowToHigh();
                
                if (isSorted) {
                    ExtentReportListener.getTest().log(Status.PASS,
                        " Price verification PASSED - Prices confirmed Low to High");
                    log.info("Price sort verified: PASSED");
                } else {
                    ExtentReportListener.getTest().log(Status.WARNING,
                        " Price verification FAILED - Prices may not be sorted correctly");
                    log.warn("Price sort verified: FAILED");
                }
                
                Assert.assertTrue(isSorted, "Prices are NOT sorted Low to High!");
                
            } catch (AssertionError ae) {
                ExtentReportListener.getTest().log(Status.FAIL,
                    " Sort assertion failed: " + ae.getMessage());
                throw ae;
            } catch (Exception e) {
                log.error("Error during price verification: {}", e.getMessage());
                ExtentReportListener.getTest().log(Status.WARNING,
                    "Could not verify sorting: " + e.getMessage());
            }
            
            // ===== STEP 6: Extract 50 Products =====
            ExtentReportListener.getTest().log(Status.INFO, "Step 6: Extracting top 50 products with lazy loading");
            log.info("Extracting product data using lazy loading...");
            
            List<Map<String, String>> products;
            try {
                products = resultsPage.extractMultipleProducts(50);
                
                if (products.isEmpty()) {
                    log.error("No products extracted!");
                    ExtentReportListener.getTest().log(Status.FAIL, "No products found");
                    Assert.fail("No products extracted");
                }
                
                ExtentReportListener.getTest().log(Status.PASS, 
                    " Extracted " + products.size() + " products");
                log.info("Successfully extracted {} products", products.size());
                
            } catch (Exception e) {
                log.error("Failed to extract products: {}", e.getMessage());
                ExtentReportListener.getTest().log(Status.FAIL, "Extraction failed: " + e.getMessage());
                throw e;
            }
            
            // ===== STEP 7: Save to CSV =====
            ExtentReportListener.getTest().log(Status.INFO, "Step 7: Saving results to CSV");
            log.info("Saving {} products to CSV: {}", products.size(), OUTPUT_CSV);
            
            try {
                saveToCSV(products, OUTPUT_CSV);
                ExtentReportListener.getTest().log(Status.PASS, 
                    "Saved to: " + OUTPUT_CSV);
            } catch (Exception e) {
                log.error("Failed to save CSV: {}", e.getMessage());
                ExtentReportListener.getTest().log(Status.FAIL, "CSV save failed: " + e.getMessage());
                throw e;
            }
            
            // ===== STEP 8: Display Results in Console =====
            ExtentReportListener.getTest().log(Status.INFO, "Step 8: Displaying results");
            displayResultsTable(products);
            ExtentReportListener.getTest().log(Status.PASS, " Results displayed in console");
            
            // ===== STEP 9: Final Summary =====
            log.info("========================================");
            log.info("Complete End-to-End Test PASSED (Single Session)");
            log.info("========================================");
            
            ExtentReportListener.getTest().log(Status.PASS,
                " ALL STEPS COMPLETED SUCCESSFULLY IN ONE SESSION");
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("Test failed: {}", e.getMessage(), e);
            log.error("========================================");
            
            ExtentReportListener.getTest().log(Status.FAIL, 
                " Test failed: " + e.getMessage());
            
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    /**
     * Save product data to CSV file
     * 
     * @param products List of product data
     * @param filePath Output CSV file path
     */
    private void saveToCSV(List<Map<String, String>> products, String filePath) throws IOException {
        log.info("Writing {} products to CSV: {}", products.size(), filePath);
        
        java.io.File outputFile = new java.io.File(filePath);
        outputFile.getParentFile().mkdirs();
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header with SN
            writer.append("SN,Title,Description,Price,Condition,Ad_Posted_Date,Seller_Name\n");
            
            // Write each product with SN
            int sn = 1;
            for (Map<String, String> product : products) {
                writer.append(String.valueOf(sn++));
                writer.append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Title", "N/A")));
                writer.append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Description", "N/A")));
                writer.append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Price", "N/A")));
                writer.append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Condition", "N/A")));
                writer.append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Ad_Posted_Date", "N/A")));
                writer.append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Seller_Name", "N/A")));
                writer.append("\n");
            }
            
            writer.flush();
        }
        
        log.info("CSV saved: {}", outputFile.getAbsolutePath());
        System.out.println("\n CSV FILE SAVED: " + outputFile.getAbsolutePath());
    }
    
    /**
     * Escape CSV values (handle commas, quotes, newlines)
     * 
     * @param value Value to escape
     * @return Escaped value
     */
    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        
        // Replace newlines with spaces
        value = value.replace("\n", " ").replace("\r", " ");
        
        // If value contains comma, quote, or newline, wrap in quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            // Escape quotes by doubling them
            value = value.replace("\"", "\"\"");
            value = "\"" + value + "\"";
        }
        
        return value;
    }
    
    /**
     * Display results in tabular format in console
     * 
     * @param products List of product data
     */
    private void displayResultsTable(List<Map<String, String>> products) {
        log.info("Displaying {} products in console", products.size());
        
        System.out.println("\n" + "=".repeat(160));
        System.out.println("SEARCH RESULTS - TOP " + products.size() + " PRODUCTS (Sorted Low to High Price)");
        System.out.println("=".repeat(160));
        
        // Table header with SN
        System.out.printf("%-4s | %-4s | %-40s | %-15s | %-12s | %-15s | %-25s%n",
            "SN", "No.", "Title", "Price", "Condition", "Posted Date", "Seller");
        System.out.println("-".repeat(160));
        
        // Table rows with SN
        for (int i = 0; i < products.size(); i++) {
            Map<String, String> product = products.get(i);
            String title     = truncate(product.getOrDefault("Title", "N/A"), 40);
            String price     = product.getOrDefault("Price", "N/A");
            String condition = truncate(product.getOrDefault("Condition", "N/A"), 12);
            String date      = truncate(product.getOrDefault("Ad_Posted_Date", "N/A"), 15);
            String seller    = truncate(product.getOrDefault("Seller_Name", "N/A"), 25);
            
            System.out.printf("%-4d | %-4d | %-40s | %-15s | %-12s | %-15s | %-25s%n",
                i + 1, i + 1, title, price, condition, date, seller);
        }
        
        System.out.println("=".repeat(160));
        System.out.println("Total Products: " + products.size());
        System.out.println("CSV File: " + OUTPUT_CSV);
        System.out.println("=".repeat(160) + "\n");
    }
    
    /**
     * Truncate string to specified length
     * 
     * @param str String to truncate
     * @param maxLength Maximum length
     * @return Truncated string
     */
    private String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
    
}