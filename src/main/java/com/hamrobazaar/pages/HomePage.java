package com.hamrobazaar.pages;

import com.hamrobazaar.base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * HomePage - Page Object for HamroBazaar Home Page
 * Handles search functionality and initial navigation
 */
public class HomePage extends BasePage {
    
    private static final Logger log = LogManager.getLogger(HomePage.class);
    
    // ==================== LOCATORS - Multiple Strategies ====================
    
    // Search Box - Using class-based and attribute selectors (ID changes dynamically)
    private By searchBoxByCss = By.cssSelector("input[placeholder='Search for anything']");
    private By searchBoxByClass = By.cssSelector("input.peer.w-full.bg-transparent");
    private By searchBoxByAttributes = By.xpath("//input[@placeholder='Search for anything' and @autocomplete='new-first-name']");
    private By searchBoxByType = By.xpath("//input[@type='text' and contains(@class,'peer') and contains(@placeholder,'Search')]");
    
    // Search Button - Using SVG and parent button
    private By searchButtonBySvg = By.xpath("//button[@type='button']//svg[contains(@class,'lucide-search')]");
    private By searchButtonByClass = By.cssSelector("button[class*='h-9 w-9'] svg.lucide-search");
    private By searchButtonByPath = By.xpath("//button[@type='button']//svg[@stroke='currentColor']//circle[@cx='11']");
    private By searchButtonParent = By.xpath("//button[@type='button'][.//svg[contains(@class,'lucide-search')]]");
    
    // Location Input Field - Multiple strategies
    private By locationInputByName = By.name("location");
    private By locationInputByRole = By.cssSelector("input[role='combobox'][name='location']");
    private By locationInputByClass = By.cssSelector("input.peer.w-full.bg-transparent[name='location']");
    private By locationInputByPlaceholder = By.xpath("//input[@name='location' and @role='combobox']");
    private By locationInputGeneric = By.xpath("//input[@autocomplete='off' and @role='combobox']");
    private By locationInputByType = By.cssSelector("input[type='text'][role='combobox']");
    
    // Location Suggestions Dropdown - Dynamic locator
    private By locationSuggestionsList = By.xpath("//div[contains(@class,'font-medium') and contains(text(),'Naya Sadak')]");
    private By locationSuggestionsAll = By.cssSelector("div.font-medium");
    private By locationSuggestionsContainer = By.xpath("//div[contains(@role,'listbox') or contains(@class,'suggestions')]");
    
    // Specific Naya Sadak suggestion
    private By nayaSadakSuggestion = By.xpath("//div[@class='font-medium' and contains(text(),'Naya Sadak, New Road, Kathmandu')]");
    
    // Distance Button (10km) - Based on actual HTML
    private By distanceButton10km = By.xpath("//button[@role='radio' and @aria-label='10km' and text()='10km']");
    private By distanceButton10kmByText = By.xpath("//button[@type='button' and @role='radio'][normalize-space(text())='10km']");
    private By distanceButton10kmByAria = By.cssSelector("button[role='radio'][aria-label='10km']");
    private By distanceButtonGeneric = By.xpath("//button[@role='radio' and contains(@class,'gap-2')][contains(.,'10km')]");
    
    // Apply/Submit Filter Button - Based on actual HTML
    private By applyFilterButtonByType = By.xpath("//button[@type='submit' and contains(@class,'button-secondary')]");
    private By applyFilterButtonByText = By.xpath("//button[@type='submit'][contains(.,'Apply filters')]");
    private By applyFilterButtonByClass = By.cssSelector("button[type='submit'].bg-primary-surface");
    private By applyFilterButtonGeneric = By.xpath("//button[@type='submit' and contains(@class,'rounded-lg')]");
    
    // ==================== DYNAMIC LOCATORS ====================
    
    /**
     * Dynamic locator for selecting specific location from suggestions
     * @param locationName Location name to select
     * @return By locator
     */
    private By locationSuggestionByText(String locationName) {
        return By.xpath("//li[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" 
                + locationName.toLowerCase() + "')]");
    }
    
    /**
     * Advanced location selector - searches for partial match
     * @param partialLocation Partial location name
     * @return By locator
     */
    private By locationSuggestionContains(String partialLocation) {
        return By.xpath("//li[contains(., '" + partialLocation + "') or contains(@data-value, '" + partialLocation + "')]");
    }
    
    /**
     * Distance button by value
     * @param distance Distance value (e.g., "10km", "5000m")
     * @return By locator
     */
    private By distanceButtonByValue(String distance) {
        String normalized = distance.toLowerCase().replace("km", "").replace("m", "");
        return By.xpath("//button[contains(translate(text(),'KM','km'),'" + normalized + "')]");
    }
    
    // ==================== CONSTRUCTOR ====================
    
    public HomePage(WebDriver driver) {
        super(driver);
        log.info("HomePage initialized");
    }
    
    // ==================== PAGE ACTIONS ====================
    
    /**
     * Search for a product using keyword
     * Presses ENTER after typing to trigger search
     * 
     * @param keyword Search keyword
     */
    public void searchProduct(String keyword) {
        log.info("Searching for product: {}", keyword);
        
        try {
            // Try CSS selector with placeholder first (most reliable)
            WebElement searchBox = waitForElement(searchBoxByCss);
            searchBox.clear();
            searchBox.sendKeys(keyword);
            log.info("Entered search keyword: {}", keyword);
            
            // Press ENTER to search
            searchBox.sendKeys(Keys.ENTER);
            log.info("Pressed ENTER to trigger search");
            
        } catch (Exception e) {
            log.warn("CSS locator failed, trying XPath with attributes");
            try {
                WebElement searchBox = waitForElement(searchBoxByAttributes);
                searchBox.clear();
                searchBox.sendKeys(keyword);
                searchBox.sendKeys(Keys.ENTER);
                log.info("Entered search keyword and pressed ENTER using XPath attributes");
            } catch (Exception ex) {
                log.warn("Attributes locator failed, trying class-based locator");
                try {
                    WebElement searchBox = waitForElement(searchBoxByClass);
                    searchBox.clear();
                    searchBox.sendKeys(keyword);
                    searchBox.sendKeys(Keys.ENTER);
                    log.info("Entered search keyword and pressed ENTER using class locator");
                } catch (Exception exc) {
                    log.error("Failed to find search box with all locators");
                    throw new RuntimeException("Unable to locate search box", exc);
                }
            }
        }
    }
    
    /**
     * Click the search button
     * Uses multiple strategies for reliability
     */
    public void clickSearchButton() {
        log.info("Clicking search button");
        
        try {
            // Try parent button first (most reliable)
            WebElement searchButton = waitForElementToBeClickable(driver.findElement(searchButtonParent));
            
            // Try regular click first
            try {
                click(searchButton);
                log.info("Clicked search button using regular click");
            } catch (Exception e) {
                // Fallback to JavaScript click
                log.warn("Regular click failed, using JavaScript click");
                clickUsingJS(searchButton);
            }
            
        } catch (Exception e) {
            log.warn("Parent button locator failed, trying SVG locator");
            try {
                WebElement searchButton = waitForElementToBeClickable(driver.findElement(searchButtonBySvg));
                clickUsingJS(searchButton); // Use JS click for SVG
                log.info("Clicked search button using SVG locator");
            } catch (Exception ex) {
                log.error("Failed to click search button: {}", ex.getMessage());
                throw new RuntimeException("Unable to click search button", ex);
            }
        }
    }
    
    /**
     * Set location with auto-suggestion selection
     * Types location and selects from dropdown
     * 
     * @param location Location name to search and select
     */
    public void setLocation(String location) {
        log.info("Setting location: {}", location);
        
        try {
            // Try multiple locator strategies
            WebElement locationInput = null;
            
            // Try 1: By name attribute
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
            
            if (locationInput == null) {
                throw new RuntimeException("Could not find location input with any locator");
            }
            
            // Scroll to element
            scrollToElement(locationInput);
            
            // Click to activate
            try {
                click(locationInput);
            } catch (Exception e) {
                clickUsingJS(locationInput);
            }
            
            log.info("Clicked on location input");
            
            // Clear existing value
            locationInput.clear();
            log.info("Cleared location input");
            
            // Type location
            locationInput.sendKeys(location);
            log.info("Typed location: {}", location);
            
            // Select from suggestions
            selectLocationFromSuggestions(location);
            
        } catch (Exception e) {
            log.error("Failed to set location: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to set location: " + e.getMessage(), e);
        }
    }
    
    /**
     
     
     * 
     * @param location Location to select
     */
    private void selectLocationFromSuggestions(String location) {
        log.info("Selecting location from suggestions: {}", location);
        
        try {
            // Wait for suggestions to appear using explicit wait
            // Try to find the specific Naya Sadak suggestion
            try {
                WebElement nayaSadak = waitForElement(nayaSadakSuggestion);
                log.info("Found Naya Sadak suggestion using explicit wait");
                
                // Scroll to it
                scrollToElement(nayaSadak);
                
                // Wait for it to be clickable
                waitForElementToBeClickable(nayaSadak);
                
                // Click it
                try {
                    click(nayaSadak);
                    log.info("Clicked Naya Sadak suggestion using regular click");
                } catch (Exception e) {
                    clickUsingJS(nayaSadak);
                    log.info("Clicked Naya Sadak suggestion using JS click");
                }
                
                return;
                
            } catch (Exception e) {
                log.warn("Specific Naya Sadak locator failed, trying all suggestions");
            }
            
            // Fallback: Get all suggestions with class 'font-medium'
            // Wait for at least one suggestion to appear
            try {
                List<WebElement> suggestions = waitForElements(locationSuggestionsAll);
                log.info("Found {} location suggestions", suggestions.size());
                
                for (int i = 0; i < suggestions.size(); i++) {
                    WebElement suggestion = suggestions.get(i);
                    String suggestionText = suggestion.getText();
                    log.info("Suggestion {}: {}", i + 1, suggestionText);
                    
                    // Check if it contains our location
                    if (suggestionText.toLowerCase().contains("naya sadak") || 
                        suggestionText.toLowerCase().contains("new road") ||
                        suggestionText.toLowerCase().contains(location.toLowerCase())) {
                        
                        scrollToElement(suggestion);
                        waitForElementToBeClickable(suggestion);
                        
                        try {
                            click(suggestion);
                            log.info("Selected location: {}", suggestionText);
                        } catch (Exception e) {
                            clickUsingJS(suggestion);
                            log.info("Selected location using JS: {}", suggestionText);
                        }
                        
                        return;
                    }
                }
                
                // If no match found, click first suggestion
                if (!suggestions.isEmpty()) {
                    log.warn("No exact match, clicking first suggestion");
                    WebElement firstSuggestion = suggestions.get(0);
                    scrollToElement(firstSuggestion);
                    waitForElementToBeClickable(firstSuggestion);
                    
                    try {
                        click(firstSuggestion);
                    } catch (Exception e) {
                        clickUsingJS(firstSuggestion);
                    }
                    
                    log.info("Clicked first suggestion: {}", firstSuggestion.getText());
                } else {
                    log.error("No suggestions found!");
                }
                
            } catch (Exception ex) {
                log.error("Failed to find any suggestions: {}", ex.getMessage());
                throw new RuntimeException("Unable to select location from suggestions", ex);
            }
            
        } catch (Exception e) {
            log.error("Failed to select location suggestion: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to select location from suggestions: " + e.getMessage(), e);
        }
    }
    
    /**
     * Scroll down to distance section
     */
    public void scrollToDistanceSection() {
        log.info("Scrolling to distance section");
        
        try {
            // Find distance button and scroll to it using aria-label locator
            WebElement distanceButton = driver.findElement(distanceButton10kmByAria);
            scrollToElement(distanceButton);
            log.info("Scrolled to distance section");
        } catch (Exception e) {
            log.warn("Could not find distance button using aria-label, trying text locator");
            try {
                WebElement distanceButton = driver.findElement(distanceButton10kmByText);
                scrollToElement(distanceButton);
                log.info("Scrolled to distance section using text locator");
            } catch (Exception ex) {
                log.warn("Could not find distance button with any locator, scrolling to bottom");
                scrollToBottom();
            }
        }
    }
    
    /**
     * Set distance/radius filter
     * Clicks on the distance button (10km)
     * 
     * @param distance Distance value (e.g., "10km")
     */
    public void setDistance(String distance) {
        log.info("Setting distance: {}", distance);
        
        try {
            WebElement distanceBtn = null;
            
            // Try multiple locators
            try {
                distanceBtn = driver.findElement(distanceButton10kmByAria);
                log.info("Found 10km button using aria-label");
            } catch (Exception e1) {
                log.warn("Aria-label locator failed");
                try {
                    distanceBtn = driver.findElement(distanceButton10kmByText);
                    log.info("Found 10km button using text");
                } catch (Exception e2) {
                    log.warn("Text locator failed");
                    try {
                        distanceBtn = driver.findElement(distanceButtonGeneric);
                        log.info("Found 10km button using generic locator");
                    } catch (Exception e3) {
                        log.error("All distance button locators failed");
                        throw new RuntimeException("Could not find 10km distance button");
                    }
                }
            }
            
            // Scroll to button
            log.info("Scrolling to distance button");
            scrollToElement(distanceBtn);
            
            // Wait for it to be clickable
            waitForElementToBeClickable(distanceBtn);
            
            // Click
            try {
                click(distanceBtn);
                log.info("Clicked 10km button using regular click");
            } catch (Exception e) {
                log.warn("Regular click failed, using JavaScript");
                clickUsingJS(distanceBtn);
                log.info("Clicked 10km button using JS click");
            }
            
            log.info("Distance set successfully: {}", distance);
            
        } catch (Exception e) {
            log.error("Failed to set distance: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to set distance: " + e.getMessage(), e);
        }
    }
    
    /**
     * Click apply/submit button to apply filters
     */
    public void clickApplyFilters() {
        log.info("Clicking apply filters button");
        
        try {
            WebElement applyButton = null;
            
            // Try multiple locators
            try {
                applyButton = driver.findElement(applyFilterButtonByText);
                log.info("Found Apply button using text");
            } catch (Exception e1) {
                log.warn("Text locator failed");
                try {
                    applyButton = driver.findElement(applyFilterButtonByClass);
                    log.info("Found Apply button using class");
                } catch (Exception e2) {
                    log.warn("Class locator failed");
                    try {
                        applyButton = driver.findElement(applyFilterButtonGeneric);
                        log.info("Found Apply button using generic locator");
                    } catch (Exception e3) {
                        log.error("All apply button locators failed");
                        throw new RuntimeException("Could not find Apply Filters button");
                    }
                }
            }
            
            // Scroll to button
            scrollToElement(applyButton);
            
            // Wait for clickable
            waitForElementToBeClickable(applyButton);
            
            // Click
            try {
                click(applyButton);
                log.info("Clicked apply button using regular click");
            } catch (Exception e) {
                log.warn("Regular click failed, using JavaScript");
                clickUsingJS(applyButton);
                log.info("Clicked apply button using JS click");
            }
            
            log.info("Filters applied successfully");
            
        } catch (Exception e) {
            log.error("Failed to click apply button: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to apply filters: " + e.getMessage(), e);
        }
    }
    
    /**
     * Complete search flow with all filters
     * High-level method that combines all actions
     * 
     * @param keyword Search keyword
     * @param location Location to filter
     * @param distance Distance radius
     */
    public void searchWithFilters(String keyword, String location, String distance) {
        log.info("Starting complete search flow");
        log.info("Keyword: {}, Location: {}, Distance: {}", keyword, location, distance);
        
        searchProduct(keyword); // ENTER is pressed automatically
        setLocation(location);
        scrollToDistanceSection();
        setDistance(distance);
        clickApplyFilters();
        
        log.info("Search with filters completed successfully");
    }
}