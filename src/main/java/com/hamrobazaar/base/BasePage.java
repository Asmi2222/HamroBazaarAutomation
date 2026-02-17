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


public class BasePage {
    
    protected static final Logger log = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    
    
    private static final int DEFAULT_WAIT = 20;
    
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT));
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }
    
   
    protected WebElement waitForElementToBeVisible(WebElement element) {
        try {
            log.debug("Waiting for element to be visible");
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            log.error("Element not visible within timeout: {}", e.getMessage());
            throw e;
        }
    }
    
    
    protected WebElement waitForElementToBeClickable(WebElement element) {
        try {
            log.debug("Waiting for element to be clickable");
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException e) {
            log.error("Element not clickable within timeout: {}", e.getMessage());
            throw e;
        }
    }
    
    
    protected WebElement waitForElement(By locator) {
        try {
            log.debug("Waiting for element: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.error("Element not found: {}", locator, e);
            throw e;
        }
    }
    
    
    protected List<WebElement> waitForElements(By locator) {
        try {
            log.debug("Waiting for elements: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
            log.error("Elements not found: {}", locator, e);
            throw e;
        }
    }
    
    
    protected void waitForElementToDisappear(By locator) {
        try {
            log.debug("Waiting for element to disappear: {}", locator);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.warn("Element still visible after timeout: {}", locator);
        }
    }
    
    
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
    
    
    protected void scrollToBottom() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            log.info("Scrolled to page bottom");
        } catch (Exception e) {
            log.error("Failed to scroll to bottom: {}", e.getMessage());
        }
    }
    
    
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    
    protected boolean isEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}