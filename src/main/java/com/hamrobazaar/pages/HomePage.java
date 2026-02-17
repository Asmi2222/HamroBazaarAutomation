package com.hamrobazaar.pages;

import com.hamrobazaar.base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;


public class HomePage extends BasePage {

    private static final Logger log = LogManager.getLogger(HomePage.class);

    

    // Search Box
    private final By searchBoxByCss        = By.cssSelector("input[placeholder='Search for anything']");
    private final By searchBoxByClass      = By.cssSelector("input.peer.w-full.bg-transparent");
    private final By searchBoxByAttributes = By.xpath("//input[@placeholder='Search for anything' and @autocomplete='new-first-name']");

    // Search Button
    private final By searchButtonParent    = By.xpath("//button[@type='button'][.//svg[contains(@class,'lucide-search')]]");
    private final By searchButtonBySvg     = By.xpath("//button[@type='button']//svg[contains(@class,'lucide-search')]");

    // Location Input
    private final By locationInputByName   = By.name("location");
    private final By locationInputByRole   = By.cssSelector("input[role='combobox'][name='location']");
    private final By locationInputByClass  = By.cssSelector("input.peer.w-full.bg-transparent[name='location']");

    // All suggestions (used as fallback to loop through)
    private final By locationSuggestionsAll = By.cssSelector("div.font-medium");

   
    private By suggestionByLocation(String location) {
        return By.xpath("//div[@class='font-medium' and contains(text(),'" + location + "')]");
    }

    
    private By distanceButtonByAria(String distance) {
        return By.cssSelector("button[role='radio'][aria-label='" + distance + "']");
    }

    private By distanceButtonByText(String distance) {
        return By.xpath("//button[@role='radio'][normalize-space(text())='" + distance + "']");
    }

    private By distanceButtonByGeneric(String distance) {
        return By.xpath("//button[@role='radio'][contains(.,'" + distance + "')]");
    }

    // Apply Filters Button
    private final By applyFilterButtonByText    = By.xpath("//button[@type='submit'][contains(.,'Apply filters')]");
    private final By applyFilterButtonByClass   = By.cssSelector("button[type='submit'].bg-primary-surface");
    private final By applyFilterButtonGeneric   = By.xpath("//button[@type='submit' and contains(@class,'rounded-lg')]");

    

    public HomePage(WebDriver driver) {
        super(driver);
        log.info("HomePage initialized");
    }

    

    public void searchProduct(String keyword) {
        log.info("Searching for product: {}", keyword);

        try {
            WebElement searchBox = waitForElement(searchBoxByCss);
            searchBox.clear();
            searchBox.sendKeys(keyword);
            searchBox.sendKeys(Keys.ENTER);
            log.info("Searched for: {}", keyword);
        } catch (Exception e) {
            log.warn("CSS locator failed, trying XPath");
            try {
                WebElement searchBox = waitForElement(searchBoxByAttributes);
                searchBox.clear();
                searchBox.sendKeys(keyword);
                searchBox.sendKeys(Keys.ENTER);
                log.info("Searched using XPath attributes locator");
            } catch (Exception ex) {
                log.warn("XPath locator failed, trying class locator");
                try {
                    WebElement searchBox = waitForElement(searchBoxByClass);
                    searchBox.clear();
                    searchBox.sendKeys(keyword);
                    searchBox.sendKeys(Keys.ENTER);
                    log.info("Searched using class locator");
                } catch (Exception exc) {
                    log.error("All search box locators failed");
                    throw new RuntimeException("Unable to locate search box", exc);
                }
            }
        }
    }

    public void setLocation(String location) {
        log.info("Setting location: {}", location);

        try {
            WebElement locationInput = null;

            try {
                locationInput = waitForElementToBeClickable(driver.findElement(locationInputByName));
                log.info("Found location input using name attribute");
            } catch (Exception e1) {
                log.warn("Name locator failed, trying role locator");
                try {
                    locationInput = waitForElementToBeClickable(driver.findElement(locationInputByRole));
                    log.info("Found location input using role attribute");
                } catch (Exception e2) {
                    log.warn("Role locator failed, trying class locator");
                    locationInput = waitForElementToBeClickable(driver.findElement(locationInputByClass));
                    log.info("Found location input using class locator");
                }
            }

            scrollToElementAndHighlight(locationInput);

            try {
                click(locationInput);
            } catch (Exception e) {
                clickUsingJS(locationInput);
            }

            locationInput.clear();
            locationInput.sendKeys(location);
            log.info("Typed location: {}", location);

            // Select matching suggestion using the location value from CSV
            selectLocationFromSuggestions(location);

        } catch (Exception e) {
            log.error("Failed to set location: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to set location: " + e.getMessage(), e);
        }
    }

    
    private void selectLocationFromSuggestions(String location) {
        log.info("Selecting suggestion for: {}", location);

        try {
            // Step 1: Try to find suggestion matching the exact location text from CSV
            try {
                By dynamicSuggestion = suggestionByLocation(location);
                WebElement suggestion = waitForElement(dynamicSuggestion);
                log.info("Found suggestion using dynamic location locator");

                scrollToElementAndHighlight(suggestion);
                waitForElementToBeClickable(suggestion);

                try {
                    click(suggestion);
                    log.info("Clicked suggestion: {}", location);
                } catch (Exception e) {
                    clickUsingJS(suggestion);
                    log.info("Clicked suggestion using JS: {}", location);
                }

                return;

            } catch (Exception e) {
                log.warn("Dynamic locator did not match, falling back to scanning all suggestions");
            }

            // Step 2: Loop through all visible suggestions and pick the best match
            List<WebElement> suggestions = waitForElements(locationSuggestionsAll);
            log.info("Found {} suggestions in dropdown", suggestions.size());

            for (WebElement suggestion : suggestions) {
                String text = suggestion.getText();
                log.info("Checking suggestion: {}", text);

                if (text.toLowerCase().contains(location.toLowerCase())) {
                    scrollToElementAndHighlight(suggestion);
                    waitForElementToBeClickable(suggestion);

                    try {
                        click(suggestion);
                        log.info("Selected matching suggestion: {}", text);
                    } catch (Exception e) {
                        clickUsingJS(suggestion);
                        log.info("Selected suggestion using JS: {}", text);
                    }

                    return;
                }
            }

            // Step 3: No match found - click first available suggestion
            if (!suggestions.isEmpty()) {
                log.warn("No exact match found for '{}', clicking first suggestion", location);
                WebElement first = suggestions.get(0);
                scrollToElementAndHighlight(first);
                waitForElementToBeClickable(first);

                try {
                    click(first);
                } catch (Exception e) {
                    clickUsingJS(first);
                }

                log.info("Clicked first suggestion: {}", first.getText());
            } else {
                log.error("No suggestions found in dropdown");
                throw new RuntimeException("No suggestions appeared for location: " + location);
            }

        } catch (Exception e) {
            log.error("Failed to select location suggestion: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to select location: " + e.getMessage(), e);
        }
    }

    public void scrollToDistanceSection() {
        log.info("Scrolling to distance section");
        // Scroll to bottom where distance buttons are
        scrollToBottom();
        log.info("Scrolled to distance section");
    }

   
    public void setDistance(String distance) {
        log.info("Setting distance: {}", distance);

        try {
            WebElement distanceBtn = null;

            // Try aria-label first (built from CSV value)
            try {
                distanceBtn = driver.findElement(distanceButtonByAria(distance));
                log.info("Found distance button using aria-label: {}", distance);
            } catch (Exception e1) {
                log.warn("Aria-label locator failed for: {}", distance);
                try {
                    distanceBtn = driver.findElement(distanceButtonByText(distance));
                    log.info("Found distance button using text: {}", distance);
                } catch (Exception e2) {
                    log.warn("Text locator failed for: {}", distance);
                    distanceBtn = driver.findElement(distanceButtonByGeneric(distance));
                    log.info("Found distance button using generic locator: {}", distance);
                }
            }

            scrollToElementAndHighlight(distanceBtn);
            waitForElementToBeClickable(distanceBtn);

            try {
                click(distanceBtn);
                log.info("Clicked distance button: {}", distance);
            } catch (Exception e) {
                clickUsingJS(distanceBtn);
                log.info("Clicked distance button using JS: {}", distance);
            }

        } catch (Exception e) {
            log.error("Failed to set distance '{}': {}", distance, e.getMessage(), e);
            throw new RuntimeException("Unable to set distance: " + e.getMessage(), e);
        }
    }

    public void clickApplyFilters() {
        log.info("Clicking Apply Filters button");

        try {
            WebElement applyButton = null;

            try {
                applyButton = driver.findElement(applyFilterButtonByText);
                log.info("Found Apply Filters button using text");
            } catch (Exception e1) {
                log.warn("Text locator failed");
                try {
                    applyButton = driver.findElement(applyFilterButtonByClass);
                    log.info("Found Apply Filters button using class");
                } catch (Exception e2) {
                    log.warn("Class locator failed");
                    applyButton = driver.findElement(applyFilterButtonGeneric);
                    log.info("Found Apply Filters button using generic locator");
                }
            }

            scrollToElementAndHighlight(applyButton);
            waitForElementToBeClickable(applyButton);

            try {
                click(applyButton);
                log.info("Clicked Apply Filters");
            } catch (Exception e) {
                clickUsingJS(applyButton);
                log.info("Clicked Apply Filters using JS");
            }

        } catch (Exception e) {
            log.error("Failed to click Apply Filters: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to apply filters: " + e.getMessage(), e);
        }
    }

   
    public void searchWithFilters(String keyword, String location, String distance) {
        log.info("Starting search flow - keyword: {}, location: {}, distance: {}", keyword, location, distance);
        searchProduct(keyword);
        setLocation(location);
        scrollToDistanceSection();
        setDistance(distance);
        clickApplyFilters();
        log.info("Search with filters completed");
    }
}