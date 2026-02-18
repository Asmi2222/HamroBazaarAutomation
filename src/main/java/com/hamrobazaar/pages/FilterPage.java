package com.hamrobazaar.pages;

import com.hamrobazaar.base.BasePage;
import com.hamrobazaar.enums.SortOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;


public class FilterPage extends BasePage {

    private static final Logger log = LogManager.getLogger(FilterPage.class);

    

    // Condition combobox input
    private final By conditionInput       = By.cssSelector("input[name='condition']");

    // All font-medium divs (used for condition suggestions - same pattern as location)
    private final By conditionSuggestions = By.cssSelector("div.font-medium");

    // Price range inputs
    private final By priceFromInput       = By.cssSelector("input[name='priceFrom']");
    private final By priceToInput         = By.cssSelector("input[name='priceTo']");

    // Sort dropdown button (the "Recent" button)
    // Sort dropdown - aria-label='Sorting-label' and aria-haspopup='dialog' from the HTML
    private final By sortDropdownButton   = By.cssSelector("button[aria-label='Sorting-label'][aria-haspopup='dialog']");
    private final By sortDropdownFallback = By.xpath("//button[@aria-label='Sorting-label']");
    // Apply Filters button
    private final By applyFilterByText    = By.xpath("//button[@type='submit'][contains(.,'Apply filters')]");
    private final By applyFilterByClass   = By.cssSelector("button[type='submit'].bg-primary-surface");
    private final By applyFilterGeneric   = By.xpath("//button[@type='submit' and contains(@class,'rounded-lg')]");

    

    // Negotiable button built from CSV value (Any / Negotiable / Fixed)
    private By negotiableButton(String value) {
        return By.xpath("//button[@role='radio' and @aria-label='" + value.trim() + "']");
    }

    // Sort option - both High to Low and A to Z buttons have: <button class="flex items-center..."><div><svg/><span class="text-sm font-medium">TEXT</span></div></button>
    private By sortOptionByText(String displayText) {
        return By.xpath("//button[contains(@class,'flex') and contains(@class,'items-center')][.//span[contains(@class,'font-medium') and normalize-space(text())='" + displayText + "']]");
    }

    

    public FilterPage(WebDriver driver) {
        super(driver);
        log.info("FilterPage initialized");
    }

    
    public void setCondition(String condition) {
        if (condition == null || condition.trim().isEmpty()) {
            log.info("No condition specified, skipping");
            return;
        }
        log.info("Setting condition: {}", condition);

        try {
            WebElement input = waitForElementToBeClickable(driver.findElement(conditionInput));

            // Scroll to center of viewport first to avoid sticky header blocking click
            scrollToElementAndHighlight(input);

            // JS click bypasses any overlapping elements (sticky nav, etc.)
            clickUsingJS(input);
            log.info("Clicked condition input using JS");

            input.clear();
            input.sendKeys(condition.trim());
            log.info("Typed condition: {}", condition);

            // Wait for suggestions and pick matching one
            List<WebElement> suggestions = waitForElements(conditionSuggestions);
            log.info("Found {} condition suggestions", suggestions.size());

            for (WebElement suggestion : suggestions) {
                String text = suggestion.getText().trim();
                if (text.equalsIgnoreCase(condition.trim())) {
                    scrollToElementAndHighlight(suggestion);
                    clickUsingJS(suggestion);
                    log.info("Selected condition: {}", text);
                    return;
                }
            }

            // Fallback: click first suggestion if no exact match
            if (!suggestions.isEmpty()) {
                WebElement first = suggestions.get(0);
                scrollToElementAndHighlight(first);
                clickUsingJS(first);
                log.warn("No exact match for '{}', selected first: {}", condition, first.getText());
            } else {
                log.error("No condition suggestions found");
            }

        } catch (Exception e) {
            log.error("Failed to set condition '{}': {}", condition, e.getMessage(), e);
            throw new RuntimeException("Unable to set condition: " + e.getMessage(), e);
        }
    }

    
    public void setPriceRange(String minPrice, String maxPrice) {
        boolean hasMin = minPrice != null && !minPrice.trim().isEmpty();
        boolean hasMax = maxPrice != null && !maxPrice.trim().isEmpty();

        if (!hasMin && !hasMax) {
            log.info("No price range specified, skipping");
            return;
        }
        log.info("Setting price range: {} to {}", minPrice, maxPrice);

        try {
            if (hasMin) {
                WebElement fromInput = waitForElementToBeClickable(driver.findElement(priceFromInput));
                scrollToElementAndHighlight(fromInput);
                fromInput.clear();
                fromInput.sendKeys(minPrice.trim());
                log.info("Set price from: {}", minPrice);
            }

            if (hasMax) {
                WebElement toInput = waitForElementToBeClickable(driver.findElement(priceToInput));
                scrollToElementAndHighlight(toInput);
                toInput.clear();
                toInput.sendKeys(maxPrice.trim());
                log.info("Set price to: {}", maxPrice);
            }

        } catch (Exception e) {
            log.error("Failed to set price range: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to set price range: " + e.getMessage(), e);
        }
    }

    
    public void setNegotiable(String negotiable) {
        if (negotiable == null || negotiable.trim().isEmpty()) {
            log.info("No negotiable filter specified, skipping");
            return;
        }
        log.info("Setting negotiable: {}", negotiable);

        try {
            WebElement btn = waitForElementToBeClickable(driver.findElement(negotiableButton(negotiable)));
            scrollToElementAndHighlight(btn);
            try { click(btn); } catch (Exception e) { clickUsingJS(btn); }
            log.info("Set negotiable to: {}", negotiable);
        } catch (Exception e) {
            log.error("Failed to set negotiable '{}': {}", negotiable, e.getMessage(), e);
            throw new RuntimeException("Unable to set negotiable: " + e.getMessage(), e);
        }
    }

    
    public void clickApplyFilters() {
        log.info("Clicking Apply Filters button");

        try {
            WebElement applyBtn;
            try {
                applyBtn = driver.findElement(applyFilterByText);
                log.info("Found Apply button using text");
            } catch (Exception e1) {
                try {
                    applyBtn = driver.findElement(applyFilterByClass);
                    log.info("Found Apply button using class");
                } catch (Exception e2) {
                    applyBtn = driver.findElement(applyFilterGeneric);
                    log.info("Found Apply button using generic locator");
                }
            }

            scrollToElementAndHighlight(applyBtn);
            waitForElementToBeClickable(applyBtn);
            try { click(applyBtn); } catch (Exception e) { clickUsingJS(applyBtn); }
            log.info("Applied filters");

        } catch (Exception e) {
            log.error("Failed to click Apply Filters: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to apply filters: " + e.getMessage(), e);
        }
    }

    
    public boolean verifyPriceSortedHighToLow() {
        
        log.info("PRICE SORT VERIFICATION: HIGH TO LOW");
        

        try {
            List<WebElement> priceElements = driver.findElements(
                By.xpath("//span[contains(@class,'text-sm') and contains(@class,'font-semibold')]")
            );

            log.info("Found {} price elements", priceElements.size());
            if (priceElements.isEmpty()) return false;

            // Step 1: collect into originalPrices
            List<Double> originalPrices = new ArrayList<>();
            for (WebElement el : priceElements) {
                try {
                    String raw = safeText(el).replaceAll("[^0-9.]", "").trim();
                    if (!raw.isEmpty()) originalPrices.add(Double.parseDouble(raw));
                } catch (Exception e) {
                    log.debug("Could not parse price");
                }
            }

            if (originalPrices.isEmpty()) return false;

            // Step 2: copy
            List<Double> sortedPrices = new ArrayList<>(originalPrices);

            // Step 3: sort descending
            sortedPrices.sort(java.util.Collections.reverseOrder());

            // Step 4: compare
            log.info("{} | {} | {}", pad("Index", 8), pad("Original", 15), pad("Expected(Desc)", 18));
            log.info("{}", "-".repeat(45));

            boolean isSorted = true;
            for (int i = 0; i < originalPrices.size(); i++) {
                boolean match = originalPrices.get(i).equals(sortedPrices.get(i));
                if (!match) isSorted = false;
                log.info("{} | {} | {} {}",
                    pad(String.valueOf(i + 1), 8),
                    pad("Rs " + originalPrices.get(i), 15),
                    pad("Rs " + sortedPrices.get(i), 18),
                    match ? "MATCH" : " MISMATCH");
            }

            
            log.info(isSorted ? " PASSED - High to Low" : " FAILED - NOT High to Low");
            
            return isSorted;

        } catch (Exception e) {
            log.error("Error verifying High to Low: {}", e.getMessage(), e);
            return false;
        }
    }

    
    public boolean verifyTitlesSortedAtoZ() {
        
        log.info("TITLE SORT VERIFICATION: A TO Z");
        

        try {
            List<WebElement> titleElements = driver.findElements(
                By.xpath("//a[contains(@class,'heading-h6') and contains(@class,'break-words')]")
            );

            log.info("Found {} title elements", titleElements.size());
            if (titleElements.isEmpty()) return false;

            // Step 1: collect into originalTitles
            List<String> originalTitles = new ArrayList<>();
            for (WebElement el : titleElements) {
                String text = safeText(el);
                if (!text.isEmpty() && !text.equals("N/A")) {
                    originalTitles.add(text.toLowerCase().trim());
                }
            }

            if (originalTitles.isEmpty()) return false;

            // Step 2: copy
            List<String> sortedTitles = new ArrayList<>(originalTitles);

            // Step 3: sort ascending
            java.util.Collections.sort(sortedTitles);

            // Step 4: compare
            log.info("{} | {} | {}", pad("Index", 8), pad("Original", 35), pad("Expected(A-Z)", 35));
            log.info("{}", "-".repeat(82));

            boolean isSorted = true;
            for (int i = 0; i < originalTitles.size(); i++) {
                boolean match = originalTitles.get(i).equals(sortedTitles.get(i));
                if (!match) isSorted = false;
                log.info("{} | {} | {} {}",
                    pad(String.valueOf(i + 1), 8),
                    pad(originalTitles.get(i), 35),
                    pad(sortedTitles.get(i), 35),
                    match ? "MATCH" : " MISMATCH");
            }

            
            log.info(isSorted ? "PASSED - A to Z" : "FAILED - NOT A to Z");
         
            return isSorted;

        } catch (Exception e) {
            log.error("Error verifying A to Z: {}", e.getMessage(), e);
            return false;
        }
    }

    private String safeText(WebElement el) {
        try { String t = el.getText(); return t == null ? "N/A" : t.trim(); }
        catch (Exception e) { return "N/A"; }
    }

    private String pad(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    
    public void applySortOrder(SortOrder sortOrder) {
        log.info("Applying sort order: {}", sortOrder.getDisplayText());

        try {
            // Step 1: Open dropdown
            WebElement sortBtn;
            try {
                sortBtn = waitForElementToBeClickable(driver.findElement(sortDropdownButton));
                log.info("Found sort dropdown using aria-label");
            } catch (Exception e) {
                sortBtn = waitForElementToBeClickable(driver.findElement(sortDropdownFallback));
                log.info("Found sort dropdown using fallback");
            }
            scrollToElementAndHighlight(sortBtn);
            try { click(sortBtn); } catch (Exception e) { clickUsingJS(sortBtn); }
            log.info("Opened sort dropdown");

            // Step 2: Click the matching option
            WebElement sortOption = waitForElementToBeClickable(
                driver.findElement(sortOptionByText(sortOrder.getDisplayText()))
            );
            scrollToElementAndHighlight(sortOption);
            try { click(sortOption); } catch (Exception e) { clickUsingJS(sortOption); }
            log.info("Selected: {}", sortOrder.getDisplayText());

        } catch (Exception e) {
            log.error("Failed to apply sort order '{}': {}", sortOrder, e.getMessage(), e);
            throw new RuntimeException("Unable to apply sort: " + e.getMessage(), e);
        }
    }
}