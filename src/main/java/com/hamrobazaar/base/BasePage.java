package com.hamrobazaar.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * BasePage - Parent class for all Page Object classes
 * Contains common WebDriver operations and wait utilities
 */
public class BasePage {
    
    protected static final Logger log = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    
    // Default wait timeout in seconds
    private static final int DEFAULT_WAIT = 20;
    
    /**
     * Constructor - Initializes WebDriver and utilities
     * 
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT));
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }
    
    //  WAIT METHODS 
    
    /**
     * Wait for element to be visible
     * 
     * @param element WebElement to wait for
     * @return WebElement after it's visible
     */
    protected WebElement waitForElementToBeVisible(WebElement element) {
        try {
            log.debug("Waiting for element to be visible");
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            log.error("Element not visible within timeout: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Wait for element to be clickable
     * 
     * @param element WebElement to wait for
     * @return WebElement after it's clickable
     */
    protected WebElement waitForElementToBeClickable(WebElement element) {
        try {
            log.debug("Waiting for element to be clickable");
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException e) {
            log.error("Element not clickable within timeout: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Wait for element located by locator
     * 
     * @param locator By locator
     * @return WebElement after it's visible
     */
    protected WebElement waitForElement(By locator) {
        try {
            log.debug("Waiting for element: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.error("Element not found: {}", locator, e);
            throw e;
        }
    }
    
    /**
     * Wait for all elements located by locator
     * 
     * @param locator By locator
     * @return List of WebElements
     */
    protected List<WebElement> waitForElements(By locator) {
        try {
            log.debug("Waiting for elements: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
            log.error("Elements not found: {}", locator, e);
            throw e;
        }
    }
    
    /**
     * Wait for element to disappear
     * 
     * @param locator By locator
     */
    protected void waitForElementToDisappear(By locator) {
        try {
            log.debug("Waiting for element to disappear: {}", locator);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.warn("Element still visible after timeout: {}", locator);
        }
    }
    
    // ==================== CLICK METHODS ====================
    
    /**
     * Click on element with wait
     * 
     * @param element WebElement to click
     */
    protected void click(WebElement element) {
        try {
            waitForElementToBeClickable(element);
            element.click();
            log.info("Clicked on element");
        } catch (Exception e) {
            log.error("Failed to click element: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Click using JavaScript (for stubborn elements)
     * 
     * @param element WebElement to click
     */
    protected void clickUsingJS(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", element);
            log.info("Clicked element using JavaScript");
        } catch (Exception e) {
            log.error("Failed to click using JS: {}", e.getMessage());
            throw e;
        }
    }
    
    // ==================== INPUT METHODS ====================
    
    /**
     * Send keys to element with wait and clear
     * 
     * @param element WebElement to type in
     * @param text Text to type
     */
    protected void sendKeys(WebElement element, String text) {
        try {
            waitForElementToBeVisible(element);
            element.clear();
            element.sendKeys(text);
            log.info("Typed text: {}", text);
        } catch (Exception e) {
            log.error("Failed to send keys: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Send keys using JavaScript
     * 
     * @param element WebElement
     * @param text Text to type
     */
    protected void sendKeysUsingJS(WebElement element, String text) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].value='" + text + "';", element);
            log.info("Typed text using JS: {}", text);
        } catch (Exception e) {
            log.error("Failed to send keys using JS: {}", e.getMessage());
            throw e;
        }
    }
    
    // ==================== GET METHODS ====================
    
    /**
     * Get text from element
     * 
     * @param element WebElement
     * @return Text content
     */
    protected String getText(WebElement element) {
        try {
            waitForElementToBeVisible(element);
            String text = element.getText();
            log.debug("Got text: {}", text);
            return text;
        } catch (Exception e) {
            log.error("Failed to get text: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Get attribute value from element
     * 
     * @param element WebElement
     * @param attribute Attribute name
     * @return Attribute value
     */
    protected String getAttribute(WebElement element, String attribute) {
        try {
            waitForElementToBeVisible(element);
            String value = element.getAttribute(attribute);
            log.debug("Got attribute '{}': {}", attribute, value);
            return value;
        } catch (Exception e) {
            log.error("Failed to get attribute: {}", e.getMessage());
            return "";
        }
    }
    
    // ==================== DROPDOWN METHODS ====================
    
    /**
     * Select dropdown option by visible text
     * 
     * @param element Dropdown element
     * @param text Visible text to select
     */
    protected void selectByVisibleText(WebElement element, String text) {
        try {
            waitForElementToBeVisible(element);
            Select select = new Select(element);
            select.selectByVisibleText(text);
            log.info("Selected dropdown option: {}", text);
        } catch (Exception e) {
            log.error("Failed to select dropdown: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Select dropdown option by value
     * 
     * @param element Dropdown element
     * @param value Value attribute
     */
    protected void selectByValue(WebElement element, String value) {
        try {
            waitForElementToBeVisible(element);
            Select select = new Select(element);
            select.selectByValue(value);
            log.info("Selected dropdown value: {}", value);
        } catch (Exception e) {
            log.error("Failed to select dropdown by value: {}", e.getMessage());
            throw e;
        }
    }
    
    // ==================== SCROLL METHODS ====================
    
    /**
     * Scroll to element using JavaScript
     * Scrolls element to center of viewport for better visibility
     * 
     * @param element WebElement to scroll to
     */
    protected void scrollToElement(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            // Scroll to center of viewport instead of top
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});", element);
            log.info("Scrolled to element (centered in viewport)");
        } catch (Exception e) {
            log.error("Failed to scroll to element: {}", e.getMessage());
        }
    }
    
    /**
     * Scroll to element with highlight for better visibility
     * Use this for important actions you want to see
     * 
     * @param element WebElement to scroll to
     */
    protected void scrollToElementAndHighlight(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            // Scroll to center
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});", element);
            
            // Briefly highlight the element
            String originalStyle = element.getAttribute("style");
            js.executeScript("arguments[0].setAttribute('style', 'border: 3px solid red; background: yellow;');", element);
            
            // Wait a moment to see the highlight
            try {
                waitForElementToBeVisible(element);
            } catch (Exception e) {
                // Element is visible
            }
            
            // Restore original style
            if (originalStyle != null && !originalStyle.isEmpty()) {
                js.executeScript("arguments[0].setAttribute('style', '" + originalStyle + "');", element);
            } else {
                js.executeScript("arguments[0].removeAttribute('style');", element);
            }
            
            log.info("Scrolled to and highlighted element");
        } catch (Exception e) {
            log.error("Failed to scroll and highlight element: {}", e.getMessage());
        }
    }
    
    /**
     * Scroll page to bottom
     */
    protected void scrollToBottom() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            log.info("Scrolled to page bottom");
        } catch (Exception e) {
            log.error("Failed to scroll to bottom: {}", e.getMessage());
        }
    }
    
    // ==================== VERIFICATION METHODS ====================
    
    /**
     * Check if element is displayed
     * 
     * @param element WebElement
     * @return true if displayed
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if element is enabled
     * 
     * @param element WebElement
     * @return true if enabled
     */
    protected boolean isEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}