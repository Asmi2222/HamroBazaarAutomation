package com.hamrobazaar.pages;

import com.hamrobazaar.base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsPage extends BasePage {

    private static final Logger log = LogManager.getLogger(SearchResultsPage.class);

    private final By sortDropdownButton = By.cssSelector("button[aria-label='Sorting-label']");
    private final By sortDropdownByRecent = By.xpath("//button[contains(normalize-space(.),'Recent') and .//*[name()='svg' and contains(@class,'lucide-chevron-down')]]");
    private final By sortDropdownByClass = By.xpath("//button[contains(@class,'button-secondary') and @type='button' and .//*[name()='svg']]");
    private final By sortLowToHighButton = By.xpath("//button[.//span[normalize-space(text())='Low to High (Price)']]");
    private final By sortLowToHighByClass = By.xpath("//button[contains(@class,'flex') and contains(@class,'items-center') and .//span[contains(.,'Low to High')]]");
    private final By productCardsPrimary = By.xpath("//div[@data-index and contains(@class,'w-full') and contains(@class,'mb-3')]");
    private final By productCardsAlternate = By.cssSelector("div.group.bg-white.rounded-\\[12px\\]");
    private final By titleRel = By.xpath(".//a[contains(@class,'heading-h6') and contains(@class,'break-words')]");
    private final By descriptionRel = By.xpath(".//p[contains(@class,'hidden') and contains(@class,'cursor-pointer') and contains(@class,'break-words')]");
    private final By priceRel = By.xpath(".//span[contains(@class,'text-sm') and contains(@class,'font-semibold')]");
    private final By conditionRel = By.xpath(".//span[contains(@class,'inline-flex')]//span[contains(@class,'leading-none')]");
    private final By dateRel = By.xpath(".//span[contains(@class,'block') and contains(@class,'text-xs') and contains(@class,'text-nowrap') and contains(normalize-space(.),'ago')]");
    private final By sellerRel = By.xpath(".//a[contains(@class,'paragraph-secondary-regular') and contains(@class,'truncate')]");
    private final By loadingSpinner = By.xpath("//div[contains(@class,'loading') or contains(@class,'spinner')]");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
        log.info("SearchResultsPage initialized");
    }

    public void openSortDropdown() {
        log.info("Clicking sort dropdown button");
        try {
            waitForPageToLoad();
            WebElement sortButton;
            try {
                sortButton = waitForElementToBeClickable(driver.findElement(sortDropdownButton));
                log.info("Found sort button using aria-label");
            } catch (Exception e1) {
                try {
                    sortButton = waitForElementToBeClickable(driver.findElement(sortDropdownByRecent));
                    log.info("Found sort button using Recent text");
                } catch (Exception e2) {
                    sortButton = waitForElementToBeClickable(driver.findElement(sortDropdownByClass));
                    log.info("Found sort button using class");
                }
            }
            scrollToElementAndHighlight(sortButton);
            try { click(sortButton); } catch (Exception e) { clickUsingJS(sortButton); }
            log.info("Sort dropdown opened");
        } catch (Exception e) {
            log.error("Failed to open sort dropdown: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to open sort dropdown: " + e.getMessage(), e);
        }
    }

    public void sortByPriceLowToHigh() {
        log.info("Clicking Low to High option");
        try {
            waitForPageToLoad();
            WebElement btn;
            try {
                btn = waitForElementToBeClickable(driver.findElement(sortLowToHighButton));
            } catch (Exception e1) {
                btn = waitForElementToBeClickable(driver.findElement(sortLowToHighByClass));
            }
            scrollToElementAndHighlight(btn);
            try { click(btn); } catch (Exception e) { clickUsingJS(btn); }
            waitForPageToLoad();
            log.info("Sorted by Low to High");
        } catch (Exception e) {
            log.error("Failed to sort: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to sort: " + e.getMessage(), e);
        }
    }

    public void applySortLowToHigh() {
        log.info("Applying Low to High sort");
        openSortDropdown();
        sortByPriceLowToHigh();
        log.info("Applied Low to High sort");
    }

    
    public boolean verifyPriceSortedLowToHigh() {
        
        log.info("PRICE SORT VERIFICATION STARTED");
        

        try {
            // Step 1: Find all price elements and store in originalPrices
            List<WebElement> priceElements = driver.findElements(
                By.xpath("//span[contains(@class,'text-sm') and contains(@class,'font-semibold')]")
            );

            log.info("Step 1: Found {} price elements on page", priceElements.size());

            if (priceElements.isEmpty()) {
                log.warn("No price elements found");
                return false;
            }

            List<Double> originalPrices = new ArrayList<>();

            for (WebElement el : priceElements) {
                try {
                    String raw = safeText(el).replaceAll("[^0-9.]", "").trim();
                    if (!raw.isEmpty()) {
                        originalPrices.add(Double.parseDouble(raw));
                    }
                } catch (Exception e) {
                    log.debug("Could not parse price: {}", e.getMessage());
                }
            }

            log.info("Step 1 Complete: Extracted {} valid prices", originalPrices.size());

            if (originalPrices.isEmpty()) {
                log.warn("No numeric prices found");
                return false;
            }

            // Step 2: Copy to sortedPrices
            List<Double> sortedPrices = new ArrayList<>(originalPrices);
            log.info("Step 2 Complete: Copied {} prices to sortedPrices", sortedPrices.size());

            // Step 3: Sort ascending
            Collections.sort(sortedPrices);
            log.info("Step 3 Complete: Sorted copy in ascending order");

            // Step 4: Compare original vs sorted
            log.info("Step 4: Comparing originalPrices vs sortedPrices");
            log.info("{} | {} | {}", padRight("Index", 8), padRight("Original", 15), padRight("Expected(Sorted)", 18));
            log.info("{}", "-".repeat(45));

            boolean isSorted = true;
            for (int i = 0; i < originalPrices.size(); i++) {
                boolean match = originalPrices.get(i).equals(sortedPrices.get(i));
                if (!match) isSorted = false;
                log.info("{} | {} | {} {}",
                    padRight(String.valueOf(i + 1), 8),
                    padRight("Rs " + originalPrices.get(i), 15),
                    padRight("Rs " + sortedPrices.get(i), 18),
                    match ? "MATCH" : "MISMATCH"
                );
            }

            
            if (isSorted) {
                log.info(" VERIFICATION PASSED - Prices ARE sorted Low to High");
            } else {
                log.warn("VERIFICATION FAILED - Prices NOT sorted Low to High");
            }
            

            return isSorted;

        } catch (Exception e) {
            log.error("Error during price verification: {}", e.getMessage(), e);
            return false;
        }
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    private void waitForPageToLoad() {
        try { waitForElementToDisappear(loadingSpinner); } catch (Exception ignored) {}
    }

    public List<WebElement> getProductCards() {
        try {
            waitForPageToLoad();
            List<WebElement> cards = driver.findElements(productCardsPrimary);
            if (cards.isEmpty()) cards = driver.findElements(productCardsAlternate);
            log.info("Found {} product cards", cards.size());
            return cards;
        } catch (Exception e) {
            log.error("Failed to get product cards: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<WebElement> loadProductsByScrolling(int targetCount) {
        log.info("Loading {} products by scrolling", targetCount);
        int previousCount = 0, noChangeCount = 0, scrollAttempt = 0;

        while (scrollAttempt < 20) {
            List<WebElement> current = driver.findElements(productCardsPrimary);
            if (current.isEmpty()) current = driver.findElements(productCardsAlternate);

            int count = current.size();
            log.info("Scroll {}: {} products", scrollAttempt + 1, count);

            if (count >= targetCount) { log.info("Reached target {}", count); break; }

            if (count == previousCount) {
                if (++noChangeCount >= 3) { log.info("No new items, stopping at {}", count); break; }
            } else {
                noChangeCount = 0;
            }

            previousCount = count;
            scrollToBottom();
            waitForPageToLoad();
            scrollAttempt++;
        }

        List<WebElement> finalCards = driver.findElements(productCardsPrimary);
        if (finalCards.isEmpty()) finalCards = driver.findElements(productCardsAlternate);
        log.info("Final count after scrolling: {}", finalCards.size());
        return finalCards;
    }

    
    public List<Map<String, String>> extractWithVirtualScroll(int maxCount) {
        log.info("Starting virtual scroll extraction for {} products", maxCount);

        // Use a Map keyed by data-index to avoid duplicates
        Map<Integer, Map<String, String>> extractedByIndex = new HashMap<>();

        int noNewCount = 0;
        int scrollAttempt = 0;
        int maxAttempts = 30;

        while (extractedByIndex.size() < maxCount && scrollAttempt < maxAttempts) {

            // Get all currently visible cards
            List<WebElement> visibleCards = driver.findElements(productCardsPrimary);
            if (visibleCards.isEmpty()) {
                visibleCards = driver.findElements(productCardsAlternate);
            }

            int beforeCount = extractedByIndex.size();
            log.info("Scroll {}: {} visible cards, {} extracted so far",
                scrollAttempt + 1, visibleCards.size(), beforeCount);

            // Extract from each visible card
            for (WebElement card : visibleCards) {
                try {
                    // Get data-index to track unique products
                    String indexAttr = card.getAttribute("data-index");
                    int dataIndex = (indexAttr != null) ? Integer.parseInt(indexAttr) : -1;

                    // Skip if already extracted this index
                    if (dataIndex >= 0 && extractedByIndex.containsKey(dataIndex)) {
                        continue;
                    }

                    // Extract data
                    Map<String, String> product = extractProductData(card);
                    String title = product.getOrDefault("Title", "N/A");

                    if (!product.isEmpty() && !"N/A".equalsIgnoreCase(title)) {
                        int key = (dataIndex >= 0) ? dataIndex : extractedByIndex.size();
                        extractedByIndex.put(key, product);
                        log.info("Extracted [{}/{}] index={}: {}",
                            extractedByIndex.size(), maxCount, dataIndex, title);
                    }

                } catch (StaleElementReferenceException e) {
                    log.debug("Stale element during extraction, skipping");
                } catch (Exception e) {
                    log.warn("Error extracting card: {}", e.getMessage());
                }

                // Stop if we have enough
                if (extractedByIndex.size() >= maxCount) break;
            }

            // Check if we got new products this scroll
            int afterCount = extractedByIndex.size();
            if (afterCount == beforeCount) {
                noNewCount++;
                log.info("No new products this scroll ({}/3 attempts)", noNewCount);
                if (noNewCount >= 3) {
                    log.info("No more new products after 3 attempts, stopping");
                    break;
                }
            } else {
                noNewCount = 0;
            }

            // Stop if we have enough
            if (extractedByIndex.size() >= maxCount) {
                log.info("Reached target of {} products!", maxCount);
                break;
            }

            // Scroll down to load more
            scrollToBottom();
            waitForPageToLoad();
            scrollAttempt++;
        }

        // Sort by index and return as list
        List<Map<String, String>> result = new ArrayList<>();
        List<Integer> sortedKeys = new ArrayList<>(extractedByIndex.keySet());
        Collections.sort(sortedKeys);
        for (Integer key : sortedKeys) {
            result.add(extractedByIndex.get(key));
        }

        log.info("Virtual scroll complete: extracted {} products in {} scrolls",
            result.size(), scrollAttempt);
        return result;
    }

    public Map<String, String> extractProductData(WebElement card) {
        Map<String, String> data = new HashMap<>();
        try {
            try { waitForElementToBeVisible(card); }
            catch (StaleElementReferenceException e) { log.warn("Stale card, skipping"); return data; }
            catch (Exception e) { log.debug("Visibility wait: {}", e.getMessage()); }

            data.put("Title",        safeFind(card, titleRel));
            data.put("Description",  safeFind(card, descriptionRel));
            data.put("Price",        safeFind(card, priceRel));
            data.put("Condition",    safeFind(card, conditionRel));
            data.put("Ad_Posted_Date", safeFind(card, dateRel));
            data.put("Seller_Name",  safeFind(card, sellerRel));
        } catch (Exception e) {
            log.error("Error extracting data: {}", e.getMessage(), e);
        }
        return data;
    }

    public List<Map<String, String>> extractMultipleProducts(int maxCount) {
        log.info("Extracting up to {} products using virtual scroll", maxCount);
        // Use virtual scroll extraction to handle HamroBazaar's lazy loading
        return extractWithVirtualScroll(maxCount);
    }

    private String safeFind(WebElement parent, By locator) {
        try { return safeText(parent.findElement(locator)); }
        catch (Exception e) { return "N/A"; }
    }

    private String safeText(WebElement el) {
        try { String t = el.getText(); return t == null ? "N/A" : t.trim(); }
        catch (Exception e) { return "N/A"; }
    }

    public int getProductCount() {
        try { return getProductCards().size(); } catch (Exception e) { return 0; }
    }
}