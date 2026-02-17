package com.hamrobazaar.base;

import com.hamrobazaar.utils.DriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;


public class BaseTest {
    
    protected static final Logger log = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;
    
    // Default timeout values (in seconds)
    protected static final int IMPLICIT_WAIT = 10;
    protected static final int EXPLICIT_WAIT = 20;
    protected static final int PAGE_LOAD_TIMEOUT = 30;
    
   
    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browser) {
        
        log.info("Setting up WebDriver for browser: {}", browser);
        
        
        try {
            // Initialize WebDriver based on browser parameter
            driver = initializeDriver(browser);
            
            // Set driver in DriverManager for global access
            DriverManager.setDriver(driver);
            
            // Configure timeouts
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT));
            
            // Maximize browser window
            driver.manage().window().maximize();
            log.info("Browser window maximized");
            
            // Delete all cookies
            driver.manage().deleteAllCookies();
            log.info("All cookies deleted");
            
            log.info("WebDriver setup completed successfully");
            
        } catch (Exception e) {
            log.error("Failed to setup WebDriver: {}", e.getMessage(), e);
            throw new RuntimeException("WebDriver setup failed", e);
        }
    }
    
    
    private WebDriver initializeDriver(String browser) {
        WebDriver driver;
        
        switch (browser.toLowerCase()) {
            
            case "chrome":
                log.info("Initializing Chrome browser");
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                // Uncomment for headless mode
                // chromeOptions.addArguments("--headless");
                driver = new ChromeDriver(chromeOptions);
                break;
                
            case "firefox":
                log.info("Initializing Firefox browser");
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--disable-notifications");
                driver = new FirefoxDriver(firefoxOptions);
                break;
                
            case "edge":
                log.info("Initializing Edge browser");
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--remote-allow-origins=*");
                edgeOptions.addArguments("--disable-notifications");
                driver = new EdgeDriver(edgeOptions);
                break;
                
            default:
                log.warn("Unknown browser: {}. Defaulting to Chrome", browser);
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
        }
        
        return driver;
    }
    
    
    @AfterMethod
    public void tearDown() {
        
        log.info("Tearing down WebDriver");
        
        try {
            if (driver != null) {
                // Quit driver through DriverManager
                DriverManager.quitDriver();
                log.info("WebDriver quit successfully");
            }
        } catch (Exception e) {
            log.error("Error during teardown: {}", e.getMessage(), e);
        }
    }
    
    
    protected void navigateToURL(String url) {
        log.info("Navigating to URL: {}", url);
        driver.get(url);
        log.info("Successfully navigated to: {}", driver.getCurrentUrl());
    }
    
    
    protected String getPageTitle() {
        String title = driver.getTitle();
        log.info("Current page title: {}", title);
        return title;
    }
    
   
    protected String getCurrentURL() {
        String url = driver.getCurrentUrl();
        log.info("Current URL: {}", url);
        return url;
    }
}