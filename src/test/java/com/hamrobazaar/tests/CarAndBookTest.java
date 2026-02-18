package com.hamrobazaar.tests;

import com.hamrobazaar.base.BaseTest;
import com.hamrobazaar.enums.SortOrder;
import com.hamrobazaar.pages.FilterPage;
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


public class CarAndBookTest extends BaseTest {

    private static final String CSV_PATH   = "src/test/resources/testdata/testdata.csv";
    private static final String OUTPUT_DIR = "test-output/";

    private static final String TIMESTAMP =
        new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date());

  

    @Test(priority = 1, description = "Car Search - Used condition, High to Low price, 100000-10000000 range")
    public void testCarSearch() {

        try {
            
            log.info("Starting Car Search Test");
            

            // Read row index 1 (Car row) from CSV
            Map<String, String> data = CSVReaderUtil.getTestData(CSV_PATH, 1);

            String keyword    = data.get("Search keyword");
            String location   = data.get("Location and distance");
            String distance   = data.get("Distance from location");
            String condition  = data.get("Quality");
            String minPrice   = data.get("Pricing from");
            String maxPrice   = data.get("Pricing to");
            String negotiable = data.get("Negotiable");
            SortOrder sort    = SortOrder.fromString(data.get("Sort Order"));

            log.info("Car Test Data: keyword={}, location={}, distance={}, condition={}, price={}-{}, negotiable={}, sort={}",
                keyword, location, distance, condition, minPrice, maxPrice, negotiable, sort.getDisplayText());

            ExtentReportListener.getTest().log(Status.INFO,
                "Car: keyword=" + keyword + " | sort=" + sort.getDisplayText() +
                " | condition=" + condition + " | price=" + minPrice + "-" + maxPrice);

            // Step 1: Navigate
            navigateToURL("https://hamrobazaar.com/");
            ExtentReportListener.getTest().log(Status.PASS, "Navigated to HamroBazaar");

            // Step 2: Search + Location + Distance (using HomePage - same as SearchResultsTest)
            HomePage homePage = new HomePage(driver);

            homePage.searchProduct(keyword);
            ExtentReportListener.getTest().log(Status.PASS, "Searched for: " + keyword);

            homePage.setLocation(location);
            ExtentReportListener.getTest().log(Status.PASS, "Set location: " + location);

            homePage.scrollToDistanceSection();
            homePage.setDistance(distance);
            ExtentReportListener.getTest().log(Status.PASS, "Set distance: " + distance);

            // Step 3: Extra Filters (using NEW FilterPage)
            FilterPage filterPage = new FilterPage(driver);

            filterPage.setCondition(condition);
            ExtentReportListener.getTest().log(Status.PASS, "Set condition: " + condition);

            filterPage.setPriceRange(minPrice, maxPrice);
            ExtentReportListener.getTest().log(Status.PASS, "Set price range: " + minPrice + " to " + maxPrice);

            filterPage.setNegotiable(negotiable);
            ExtentReportListener.getTest().log(Status.PASS, "Set negotiable: " + negotiable);

            // Step 4: Apply Filters
            filterPage.clickApplyFilters();
            ExtentReportListener.getTest().log(Status.PASS, "Applied filters");

            // Step 5: Sort
            filterPage.applySortOrder(sort);
            ExtentReportListener.getTest().log(Status.PASS, "Sorted by: " + sort.getDisplayText());

            // Step 6: Verify sort - High to Low
            SearchResultsPage resultsPage = new SearchResultsPage(driver);
            boolean isSorted = filterPage.verifyPriceSortedHighToLow();

            if (isSorted) {
                ExtentReportListener.getTest().log(Status.PASS, "Price sort verified: High to Low");
            } else {
                ExtentReportListener.getTest().log(Status.WARNING, "Price sort verification failed");
            }
            Assert.assertTrue(isSorted, "Car prices are NOT sorted High to Low!");

            // Step 7: Extract 50 products
            List<Map<String, String>> products = resultsPage.extractMultipleProducts(50);
            Assert.assertFalse(products.isEmpty(), "No car products extracted!");
            ExtentReportListener.getTest().log(Status.PASS, "Extracted " + products.size() + " car products");

            // Step 8: Save CSV
            String outputCsv = OUTPUT_DIR + "Car_Results_" + TIMESTAMP + ".csv";
            saveToCSV(products, outputCsv);
            ExtentReportListener.getTest().log(Status.PASS, "Saved to: " + outputCsv);

            // Step 9: Display
            displayResultsTable(products, keyword, sort.getDisplayText());
            ExtentReportListener.getTest().log(Status.PASS, "Results displayed in console");

            log.info("Car Search Test PASSED");
            ExtentReportListener.getTest().log(Status.PASS, "CAR TEST COMPLETED SUCCESSFULLY");

        } catch (Exception e) {
            log.error("Car test failed: {}", e.getMessage(), e);
            ExtentReportListener.getTest().log(Status.FAIL, "Car test failed: " + e.getMessage());
            Assert.fail("Car test failed: " + e.getMessage());
        }
    }

    

    @Test(priority = 2, description = "Book Search - Brand New condition, A to Z sort, 100-1500 price range")
    public void testBookSearch() {

        try {
            
            log.info("Starting Book Search Test");
         

            // Read row index 2 (Book row) from CSV
            Map<String, String> data = CSVReaderUtil.getTestData(CSV_PATH, 2);

            String keyword    = data.get("Search keyword");
            String location   = data.get("Location and distance");
            String distance   = data.get("Distance from location");
            String condition  = data.get("Quality");
            String minPrice   = data.get("Pricing from");
            String maxPrice   = data.get("Pricing to");
            String negotiable = data.get("Negotiable");
            SortOrder sort    = SortOrder.fromString(data.get("Sort Order"));

            log.info("Book Test Data: keyword={}, location={}, distance={}, condition={}, price={}-{}, negotiable={}, sort={}",
                keyword, location, distance, condition, minPrice, maxPrice, negotiable, sort.getDisplayText());

            ExtentReportListener.getTest().log(Status.INFO,
                "Book: keyword=" + keyword + " | sort=" + sort.getDisplayText() +
                " | condition=" + condition + " | price=" + minPrice + "-" + maxPrice);

            // Step 1: Navigate
            navigateToURL("https://hamrobazaar.com/");
            ExtentReportListener.getTest().log(Status.PASS, "Navigated to HamroBazaar");

            // Step 2: Search + Location + Distance (using HomePage - same as SearchResultsTest)
            HomePage homePage = new HomePage(driver);

            homePage.searchProduct(keyword);
            ExtentReportListener.getTest().log(Status.PASS, "Searched for: " + keyword);

            homePage.setLocation(location);
            ExtentReportListener.getTest().log(Status.PASS, "Set location: " + location);

            homePage.scrollToDistanceSection();
            homePage.setDistance(distance);
            ExtentReportListener.getTest().log(Status.PASS, "Set distance: " + distance);

            // Step 3: Extra Filters (using NEW FilterPage)
            FilterPage filterPage = new FilterPage(driver);

            filterPage.setCondition(condition);
            ExtentReportListener.getTest().log(Status.PASS, "Set condition: " + condition);

            filterPage.setPriceRange(minPrice, maxPrice);
            ExtentReportListener.getTest().log(Status.PASS, "Set price range: " + minPrice + " to " + maxPrice);

            filterPage.setNegotiable(negotiable);
            ExtentReportListener.getTest().log(Status.PASS, "Set negotiable: " + negotiable);

            // Step 4: Apply Filters
            filterPage.clickApplyFilters();
            ExtentReportListener.getTest().log(Status.PASS, "Applied filters");

            // Step 5: Sort
            filterPage.applySortOrder(sort);
            ExtentReportListener.getTest().log(Status.PASS, "Sorted by: " + sort.getDisplayText());

            // Step 6: Verify A to Z sort
            SearchResultsPage resultsPage = new SearchResultsPage(driver);
            boolean isSorted = filterPage.verifyTitlesSortedAtoZ();

            if (isSorted) {
                ExtentReportListener.getTest().log(Status.PASS, "A to Z sort verified");
            } else {
                ExtentReportListener.getTest().log(Status.WARNING, "A to Z sort verification failed");
            }
            Assert.assertTrue(isSorted, "Book titles are NOT sorted A to Z!");

            // Step 7: Extract 50 products
            List<Map<String, String>> products = resultsPage.extractMultipleProducts(50);
            Assert.assertFalse(products.isEmpty(), "No book products extracted!");
            ExtentReportListener.getTest().log(Status.PASS, "Extracted " + products.size() + " book products");

            // Step 8: Save CSV
            String outputCsv = OUTPUT_DIR + "Book_Results_" + TIMESTAMP + ".csv";
            saveToCSV(products, outputCsv);
            ExtentReportListener.getTest().log(Status.PASS, "Saved to: " + outputCsv);

            // Step 9: Display
            displayResultsTable(products, keyword, sort.getDisplayText());
            ExtentReportListener.getTest().log(Status.PASS, "Results displayed in console");

            log.info("Book Search Test PASSED");
            ExtentReportListener.getTest().log(Status.PASS, "BOOK TEST COMPLETED SUCCESSFULLY");

        } catch (Exception e) {
            log.error("Book test failed: {}", e.getMessage(), e);
            ExtentReportListener.getTest().log(Status.FAIL, "Book test failed: " + e.getMessage());
            Assert.fail("Book test failed: " + e.getMessage());
        }
    }

    

    private void saveToCSV(List<Map<String, String>> products, String filePath) throws IOException {
        java.io.File outputFile = new java.io.File(filePath);
        outputFile.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("SN,Title,Description,Price,Condition,Ad_Posted_Date,Seller_Name\n");

            int sn = 1;
            for (Map<String, String> product : products) {
                writer.append(String.valueOf(sn++)).append(",");
                writer.append(escape(product.getOrDefault("Title",          "N/A"))).append(",");
                writer.append(escape(product.getOrDefault("Description",    "N/A"))).append(",");
                writer.append(escape(product.getOrDefault("Price",          "N/A"))).append(",");
                writer.append(escape(product.getOrDefault("Condition",      "N/A"))).append(",");
                writer.append(escape(product.getOrDefault("Ad_Posted_Date", "N/A"))).append(",");
                writer.append(escape(product.getOrDefault("Seller_Name",    "N/A"))).append("\n");
            }
            writer.flush();
        }

        log.info("CSV saved: {}", outputFile.getAbsolutePath());
        System.out.println("\nCSV FILE SAVED: " + outputFile.getAbsolutePath());
    }

    private String escape(String value) {
        if (value == null) return "";
        value = value.replace("\n", " ").replace("\r", " ");
        if (value.contains(",") || value.contains("\"")) {
            value = "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void displayResultsTable(List<Map<String, String>> products, String keyword, String sortLabel) {
        System.out.println("\n" + "=".repeat(160));
        System.out.println("RESULTS FOR: " + keyword.toUpperCase() + "  |  Sort: " + sortLabel + "  |  Total: " + products.size());
        System.out.println("=".repeat(160));
        System.out.printf("%-4s | %-40s | %-15s | %-12s | %-15s | %-25s%n",
            "SN", "Title", "Price", "Condition", "Posted Date", "Seller");
        System.out.println("-".repeat(160));

        for (int i = 0; i < products.size(); i++) {
            Map<String, String> p = products.get(i);
            System.out.printf("%-4d | %-40s | %-15s | %-12s | %-15s | %-25s%n",
                i + 1,
                truncate(p.getOrDefault("Title",          "N/A"), 40),
                         p.getOrDefault("Price",          "N/A"),
                truncate(p.getOrDefault("Condition",      "N/A"), 12),
                truncate(p.getOrDefault("Ad_Posted_Date", "N/A"), 15),
                truncate(p.getOrDefault("Seller_Name",    "N/A"), 25));
        }

        System.out.println("=".repeat(160) + "\n");
    }

    private String truncate(String str, int max) {
        if (str == null || str.length() <= max) return str;
        return str.substring(0, max - 3) + "...";
    }
}