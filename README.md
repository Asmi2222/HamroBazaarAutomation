# HamroBazaar Automation Framework

A Selenium WebDriver test automation framework built with Java, TestNG, and Maven.
Automates product search, filtering, sorting, and data extraction on HamroBazaar.com.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Test Flow](#test-flow)
- [Output Files](#output-files)
- [Reports](#reports)
- [Design Patterns](#design-patterns)
- [Troubleshooting](#troubleshooting)

---

## Project Overview

This framework automates the following end-to-end flow on HamroBazaar.com:

1. Navigate to hamrobazaar.com
2. Search for "Monitor"
3. Filter by Location - Naya Sadak, New Road, Kathmandu
4. Set Distance radius to 10km
5. Apply filters
6. Sort results by Price: Low to High
7. Verify sorting is correct by comparing original price array against a sorted copy
8. Extract the top 50 products using virtual scroll and lazy loading
9. Save results to a timestamped Search_Result CSV file
10. Display results in a formatted console table
11. Generate a detailed ExtentReport HTML report

---

## Tech Stack

| Technology          | Version | Purpose                           |
|---------------------|---------|-----------------------------------|
| Java                | 11      | Programming language              |
| Selenium WebDriver  | 4.18.1  | Browser automation                |
| TestNG              | 7.9.0   | Test framework                    |
| ExtentReports       | 5.1.1   | HTML test reporting               |
| WebDriverManager    | 5.7.0   | Automatic ChromeDriver management |
| OpenCSV             | 5.9     | CSV read and write                |
| Apache Commons IO   | 2.15.1  | File operations                   |
| Log4j2              | 2.22.1  | Logging                           |
| Maven               | 3.8+    | Build and dependency management   |

---

## Prerequisites

Before running this project, ensure the following are installed on your machine.

### 1. Java JDK 11 or higher

```bash
java -version
```

Download from: https://www.oracle.com/java/technologies/downloads/

### 2. Apache Maven 3.8 or higher

```bash
mvn -version
```

Download from: https://maven.apache.org/download.cgi

### 3. Google Chrome Browser

Latest stable version is recommended.
WebDriverManager automatically downloads the matching ChromeDriver so no manual driver installation is needed.

### 4. Eclipse IDE or IntelliJ IDEA

Eclipse download: https://www.eclipse.org/downloads/

If using Eclipse, install the TestNG plugin:
- Help -> Eclipse Marketplace -> Search "TestNG" -> Install -> Restart Eclipse

### 5. Git (Optional)

```bash
git --version
```

---

## Project Structure

```
HamroBazaarAutomation/
|
+-- src/
|   +-- main/java/com/hamrobazaar/
|   |   +-- base/
|   |   |   +-- BasePage.java              Common WebDriver operations and explicit waits
|   |   |   +-- BaseTest.java              WebDriver setup, teardown, and base config
|   |   |
|   |   +-- pages/
|   |   |   +-- HomePage.java              Search, location, distance, apply filters
|   |   |   +-- SearchResultsPage.java     Sort, verify prices, extract products
|   |   |
|   |   +-- utils/
|   |       +-- CSVReaderUtil.java          Read test input data from CSV
|   |       +-- DriverManager.java          Thread-safe WebDriver management
|   |       +-- ExtentManager.java          ExtentReports singleton instance
|   |       +-- ExtentReportListener.java   TestNG listener for report generation
|   |       +-- ScreenshotUtil.java         Capture screenshots on failure
|   |
|   +-- test/
|       +-- java/com/hamrobazaar/tests/
|       |   +-- HomeTest.java               Standalone test for homepage actions only
|       |   +-- SearchResultsTest.java      Main end-to-end test (runs everything)
|       |
|       +-- resources/
|           +-- testdata/
|           |   +-- testdata.csv            Test input: keyword, location, distance
|           +-- config/
|           |   +-- config.properties       Application configuration
|           |   +-- extent-config.xml       ExtentReport theme configuration
|           +-- log4j2.xml                  Logging configuration
|           +-- testng.xml                  TestNG suite configuration
|
+-- logs/                                   Log files (auto-generated)
+-- reports/                                ExtentReport HTML files (auto-generated)
+-- screenshots/                            Failure screenshots (auto-generated)
+-- test-output/                            CSV output files (auto-generated)
+-- pom.xml                                 Maven dependencies and build config
+-- README.md                               This file
```

---

## Setup Instructions

### Step 1: Clone or Download the Project

```bash
git clone <repository-url>
cd HamroBazaarAutomation
```

Or download the ZIP file and extract it.

### Step 2: Import into Eclipse

1. Open Eclipse IDE
2. Go to File -> Import
3. Select Maven -> Existing Maven Projects
4. Browse to the project root folder
5. Click Finish

### Step 3: Update Maven Dependencies

1. Right-click the project in Package Explorer
2. Select Maven -> Update Project
3. Check "Force Update of Snapshots/Releases"
4. Click OK

Wait for all dependencies to download. An internet connection is required.

### Step 4: Verify Test Data

Open the following file and confirm it has the correct content:

```
src/test/resources/testdata/testdata.csv
```

Expected content:

```
Test name,Search keyword,Location and distance,Distance from location
HamroBazaarMonitorSearch,monitor,Naya Sadak New Road Kathmandu-22 Kathmandu,10km
```

---

## Configuration

### Test Data (testdata.csv)

| Column                 | Value                                      | Description                |
|------------------------|--------------------------------------------|----------------------------|
| Test name              | HamroBazaarMonitorSearch                   | Label for the test run     |
| Search keyword         | monitor                                    | Product to search for      |
| Location and distance  | Naya Sadak New Road Kathmandu-22 Kathmandu | Location to filter by      |
| Distance from location | 10km                                       | Radius distance filter     |

To change search parameters, edit the CSV values only. No code changes are required.

### Timeouts (BaseTest.java)

```java
protected static final int IMPLICIT_WAIT     = 10;  // seconds
protected static final int EXPLICIT_WAIT     = 20;  // seconds
protected static final int PAGE_LOAD_TIMEOUT = 30;  // seconds
```

### Logging (log4j2.xml)

- Console output level: INFO
- File output saved to: logs/test-execution.log
- Package com.hamrobazaar logs at INFO level
- All other packages log at WARN level

---

## Running Tests

### Option 1: Eclipse (Recommended)

1. Expand src/test/resources in Package Explorer
2. Right-click testng.xml
3. Select Run As -> TestNG Suite

### Option 2: Maven Command Line

```bash
# Run all tests
mvn clean test

# Force dependency refresh and run tests
mvn clean test -U
```

### Option 3: Run a Single Test Class

1. Open SearchResultsTest.java
2. Right-click anywhere in the editor
3. Select Run As -> TestNG Test

---

## Test Flow

The entire test runs in a single browser session. The browser does not close or restart between steps.

```
Step 1    Read test data from testdata.csv
Step 2    Navigate to hamrobazaar.com
Step 3    Type search keyword and press ENTER
Step 4    Type location in the location input and select the suggestion
Step 5    Click the 10km distance button
Step 6    Click Apply Filters
Step 7    Click the sort dropdown (shows "Recent" by default)
Step 8    Select "Low to High (Price)"
Step 9    Verify prices are sorted correctly
Step 10   Extract 50 products using virtual scroll
Step 11   Save results to timestamped CSV
Step 12   Display formatted results table in console
```

### Price Verification Logic

```
1. findElements to collect all price elements on the page
2. Extract numeric values and store in originalPrices list
3. Copy originalPrices into a new sortedPrices list
4. Run Collections.sort(sortedPrices) to sort ascending
5. Compare originalPrices against sortedPrices index by index
6. If all positions match: PASSED
7. If any position differs: log the mismatch and FAIL
```

### Virtual Scroll Extraction

HamroBazaar uses a virtual scroller. Only around 24 product cards exist in the DOM at any time.
As you scroll down, old cards are removed from the DOM and new ones are added.

The framework handles this by:

- Extracting data from visible cards on each scroll
- Using the data-index HTML attribute to identify unique cards and skip duplicates
- Continuing to scroll and extract until 50 unique products are collected

---

## Output Files

### Search Results CSV

Each test run creates a new file with a timestamp so previous results are never overwritten:

```
test-output/Search_Result_2024-02-17_10-30-45.csv
```

File format:

```
SN,Title,Description,Price,Condition,Ad_Posted_Date,Seller_Name
1,Yamaha HS3 Monitor...,Packaging: Pair...,1,Brand New,1 years ago,Nepal Music Gallery
2,...
50,...
```

### Console Table

A formatted table is printed to the console after extraction:

```
=============================================================================
SEARCH RESULTS - TOP 50 PRODUCTS (Sorted Low to High Price)
=============================================================================
SN   | No. | Title                    | Price  | Condition  | Posted  | Seller
-----------------------------------------------------------------------------
1    | 1   | Yamaha HS3 Monitor...    | 1      | Brand New  | 1y ago  | Nepal Music...
...
=============================================================================
Total Products: 50
```

### Log File

```
logs/test-execution.log
```

Contains all INFO level and above messages with timestamps from the test run.

### Failure Screenshots

If any step fails, a screenshot is automatically captured and saved:

```
screenshots/<TestName>_FAILED_<timestamp>.png
```

The screenshot is also embedded in the ExtentReport HTML.

---

## Reports

After each test run, an HTML report is generated automatically in the reports folder:

```
reports/HamroBazaar_Report_<YYYY-MM-DD_HH-mm-ss>.html
```

To view the report:
1. Navigate to the reports/ folder
2. Open the HTML file in any web browser

The report includes:
- Pass or Fail status for each individual test step
- Screenshots attached on failure
- Execution time per step
- System information including OS, Java version, and browser
- Complete step-by-step log for each test method

---

## Design Patterns

| Pattern           | Where Used                   | Purpose                                     |
|-------------------|------------------------------|---------------------------------------------|
| Page Object Model | pages/ folder                | Separates page actions from test logic      |
| Singleton         | ExtentManager.java           | One shared ExtentReports instance           |
| ThreadLocal       | DriverManager.java           | Thread-safe driver access for parallel runs |
| Listener/Observer | ExtentReportListener.java    | Reacts to TestNG pass, fail, skip events    |

---

## Key Technical Notes

### No Thread.sleep

All waiting uses Selenium WebDriverWait:

```java
waitForElementToBeClickable(element);
waitForElementToBeVisible(element);
waitForElementToDisappear(loadingSpinner);
```

### Multiple Locator Fallbacks

Every element has a primary locator and one or two fallbacks in case the website structure changes:

```java
// Primary
By.cssSelector("button[aria-label='Sorting-label']")
// Fallback
By.xpath("//button[contains(.,'Recent')]")
```

### Visual Highlighting

Before clicking any important element, the framework scrolls it to the center of the viewport and briefly applies a visible highlight so actions can be observed while the test is running.

---

## Troubleshooting

### TestNG listener not found error

Ensure testng.xml only references ExtentReportListener:

```xml
<listeners>
    <listener class-name="com.hamrobazaar.utils.ExtentReportListener"/>
</listeners>
```

### Only 24 products extracted instead of 50

This is caused by the virtual scroll behavior on HamroBazaar. The fix is implemented via extractWithVirtualScroll() which is called internally by extractMultipleProducts(). If you still see only 24, confirm that extractMultipleProducts() has not been modified to use the old approach.

### Stale element reference exception

This is handled automatically. The framework re-finds elements by index before each extraction rather than holding onto old element references across scrolls.

### Element not found or locator failure

HamroBazaar uses dynamic element IDs that change on every page load. The framework avoids these by using stable attributes such as placeholder, aria-label, name, and role. If a locator fails, check whether the website has updated its HTML structure.

### ExtentReport HTML not generated

Look for the following in the console output after the test completes:

```
EXTENT REPORT GENERATED
Location: reports/HamroBazaar_Report_...html
```

If it is missing, run Project -> Clean in Eclipse and rerun the test.

### Maven build fails or dependencies are missing

```bash
# Force re-download all dependencies
mvn clean install -U

# Build the project without running tests
mvn clean install -DskipTests
```

### ChromeDriver version mismatch

This project uses WebDriverManager which automatically downloads the correct ChromeDriver version to match the installed Chrome browser. Ensure an internet connection is available on the first run.

---

## Author

Asmi Bajracharya
QA Automation
