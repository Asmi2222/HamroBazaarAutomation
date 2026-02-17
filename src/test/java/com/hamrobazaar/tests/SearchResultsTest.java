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

public class SearchResultsTest extends BaseTest {

    private static final String CSV_PATH   = "src/test/resources/testdata/testdata.csv";
    private static final String OUTPUT_DIR = "test-output/";

    private static final String TIMESTAMP  =
        new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date());

    private static final String OUTPUT_CSV = OUTPUT_DIR + "Search_Result_" + TIMESTAMP + ".csv";

    @Test(priority = 1, description = "Complete Search, Filter, Sort, Extract, and Save - All in One Session")
    public void testCompleteSearchFlowInOneSession() {

        try {
            log.info("Starting Complete End-to-End Test (Single Session)");

            // STEP 1: Read Test Data
            ExtentReportListener.getTest().log(Status.INFO, "Step 1: Reading test data from CSV");
            Map<String, String> testData = CSVReaderUtil.getTestData(CSV_PATH);

            String searchKeyword = testData.get("Search keyword");
            String location      = testData.get("Location and distance");
            String distance      = testData.get("Distance from location");

            log.info("Test Data - Keyword: {}, Location: {}, Distance: {}", searchKeyword, location, distance);
            ExtentReportListener.getTest().log(Status.INFO,
                "Test Data: " + searchKeyword + ", " + location + ", " + distance);

            // STEP 2: Navigate
            ExtentReportListener.getTest().log(Status.INFO, "Step 2: Navigating to HamroBazaar");
            navigateToURL("https://hamrobazaar.com/");
            ExtentReportListener.getTest().log(Status.PASS, "Navigated to HamroBazaar");

            // STEP 3: Search, Location, Distance, Apply
            ExtentReportListener.getTest().log(Status.INFO, "Step 3: Searching with filters");
            HomePage homePage = new HomePage(driver);

            homePage.searchProduct(searchKeyword);
            ExtentReportListener.getTest().log(Status.PASS, "Searched for: " + searchKeyword);

            homePage.setLocation(location);
            ExtentReportListener.getTest().log(Status.PASS, "Set location: " + location);

            homePage.scrollToDistanceSection();
            homePage.setDistance(distance);
            ExtentReportListener.getTest().log(Status.PASS, "Set distance: " + distance);

            homePage.clickApplyFilters();
            ExtentReportListener.getTest().log(Status.PASS, "Applied filters - Now on results page");

            // STEP 4: Initialize Search Results Page
            ExtentReportListener.getTest().log(Status.INFO, "Step 4: Initializing Search Results Page");
            SearchResultsPage resultsPage = new SearchResultsPage(driver);

            // STEP 5: Sort by Low to High
            ExtentReportListener.getTest().log(Status.INFO, "Step 5: Sorting by Low to High Price");
            resultsPage.applySortLowToHigh();
            ExtentReportListener.getTest().log(Status.PASS, "Sorted by Low to High Price");

            // STEP 6: Verify Price Sorting
            ExtentReportListener.getTest().log(Status.INFO, "Step 6: Verifying prices are sorted Low to High");
            boolean isSorted = resultsPage.verifyPriceSortedLowToHigh();

            if (isSorted) {
                ExtentReportListener.getTest().log(Status.PASS, "Price verification PASSED - Prices confirmed Low to High");
                log.info("Price sort verified: PASSED");
            } else {
                ExtentReportListener.getTest().log(Status.WARNING, "Price verification FAILED - Prices may not be sorted correctly");
                log.warn("Price sort verified: FAILED");
            }

            Assert.assertTrue(isSorted, "Prices are NOT sorted Low to High!");

            // STEP 7: Extract 50 Products
            ExtentReportListener.getTest().log(Status.INFO, "Step 7: Extracting top 50 products");
            List<Map<String, String>> products = resultsPage.extractMultipleProducts(50);

            if (products.isEmpty()) {
                Assert.fail("No products extracted");
            }

            ExtentReportListener.getTest().log(Status.PASS, "Extracted " + products.size() + " products");
            log.info("Successfully extracted {} products", products.size());

            // STEP 8: Save to CSV
            ExtentReportListener.getTest().log(Status.INFO, "Step 8: Saving results to CSV");
            saveToCSV(products, OUTPUT_CSV);
            ExtentReportListener.getTest().log(Status.PASS, "Saved to: " + OUTPUT_CSV);

            // STEP 9: Display Results
            ExtentReportListener.getTest().log(Status.INFO, "Step 9: Displaying results");
            displayResultsTable(products);
            ExtentReportListener.getTest().log(Status.PASS, "Results displayed in console");

            log.info("Complete End-to-End Test PASSED");
            ExtentReportListener.getTest().log(Status.PASS, "ALL STEPS COMPLETED SUCCESSFULLY");

        } catch (Exception e) {
            log.error("Test failed: {}", e.getMessage(), e);
            ExtentReportListener.getTest().log(Status.FAIL, "Test failed: " + e.getMessage());
            Assert.fail("Test failed: " + e.getMessage());
        }
    }

    private void saveToCSV(List<Map<String, String>> products, String filePath) throws IOException {
        log.info("Writing {} products to CSV: {}", products.size(), filePath);

        java.io.File outputFile = new java.io.File(filePath);
        outputFile.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("SN,Title,Description,Price,Condition,Ad_Posted_Date,Seller_Name\n");

            int sn = 1;
            for (Map<String, String> product : products) {
                writer.append(String.valueOf(sn++)).append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Title",        "N/A"))).append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Description",  "N/A"))).append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Price",        "N/A"))).append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Condition",    "N/A"))).append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Ad_Posted_Date","N/A"))).append(",");
                writer.append(escapeCsvValue(product.getOrDefault("Seller_Name",  "N/A"))).append("\n");
            }

            writer.flush();
        }

        log.info("CSV saved: {}", outputFile.getAbsolutePath());
        System.out.println("\nCSV FILE SAVED: " + outputFile.getAbsolutePath());
    }

    private String escapeCsvValue(String value) {
        if (value == null) return "";
        value = value.replace("\n", " ").replace("\r", " ");
        if (value.contains(",") || value.contains("\"")) {
            value = "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void displayResultsTable(List<Map<String, String>> products) {
        System.out.println("\n" + "=".repeat(160));
        System.out.println("SEARCH RESULTS - TOP " + products.size() + " PRODUCTS (Sorted Low to High Price)");
        System.out.println("=".repeat(160));
        System.out.printf("%-4s | %-4s | %-40s | %-15s | %-12s | %-15s | %-25s%n",
            "SN", "No.", "Title", "Price", "Condition", "Posted Date", "Seller");
        System.out.println("-".repeat(160));

        for (int i = 0; i < products.size(); i++) {
            Map<String, String> p = products.get(i);
            System.out.printf("%-4d | %-4d | %-40s | %-15s | %-12s | %-15s | %-25s%n",
                i + 1, i + 1,
                truncate(p.getOrDefault("Title",          "N/A"), 40),
                         p.getOrDefault("Price",          "N/A"),
                truncate(p.getOrDefault("Condition",      "N/A"), 12),
                truncate(p.getOrDefault("Ad_Posted_Date", "N/A"), 15),
                truncate(p.getOrDefault("Seller_Name",    "N/A"), 25));
        }

        System.out.println("=".repeat(160));
        System.out.println("Total Products : " + products.size());
        System.out.println("CSV File       : " + OUTPUT_CSV);
        System.out.println("=".repeat(160) + "\n");
    }

    private String truncate(String str, int max) {
        if (str == null || str.length() <= max) return str;
        return str.substring(0, max - 3) + "...";
    }
}